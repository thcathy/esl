package com.esl.util.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.MessageFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.esl.model.practice.PhoneticSymbols;
import com.esl.util.SourceChecker;
import com.esl.util.WebUtil;

public class CambridgeDictionaryParser implements SourceChecker, DictionaryParser {
	private Logger logger = LoggerFactory.getLogger("ESL");

	final String URLPrefix = "http://dictionary.cambridge.org/dictionary/british/";
	final String IPAPrefix = "<span class=\"ipa\">";
	final String audioPrefix = "<audio id=\"audio_pron-uk_0\" ";
	final String audioSuffix = ".mp3";
	final String audioURLPrefix = "http://dictionary.cambridge.org/media/";
	final int IPAPositionLimit = 1000;

	String ipa;
	String audioLink;
	String query;			// input word
	String expectedContent; 	// used for SourceChecker

	// ------------------------ getter / setter --------------------- //
	public CambridgeDictionaryParser(String query) {
		this.query = query.toLowerCase();
	}

	public boolean isContentFind() {
		return StringUtils.hasText(ipa) && StringUtils.hasText(audioLink);
	}

	public String getIpa() {return this.ipa;}
	public String getAudioLink() {return this.audioLink;}

	public void setParsedContentCheck(String expected) {this.expectedContent = expected;}
	public String getParsedContentCheck() { return expectedContent;}

	// ------------------------ function --------------------- //
	public boolean parse() {
		//BufferedReader br;
		Document doc;
				
		try {
			//br = WebUtil.getReaderFromURL(concatURL());
			HttpURLConnection connection = new HttpURLConnectionBuilder().setURL(concatURL()).createConnection();
			doc = Jsoup.parse(connection.getInputStream(), "UTF-8", connection.getURL().getPath());			
		} catch (IOException e1) {
			try {				
				HttpURLConnection connection = new HttpURLConnectionBuilder().setURL(concatURL2()).createConnection();
				doc = Jsoup.parse(connection.getInputStream(), "UTF-8", connection.getURL().getPath());
			} catch (IOException e) {
				logger.warn("cannot get reader when query [{}]", query);
				return false;
			}
		}

		try {
			
			Elements audioLinkSource = doc.select("source.audio_file_source");
			audioLink = audioLinkSource.get(0).attr("src");
			
			String orgIPA = doc.select("span.ipa").get(0).ownText();
			logger.debug("org IPA [{}]", orgIPA);
			ipa = PhoneticSymbols.filterIPA(PhoneticSymbols.convertGoogleIPA(orgIPA));			
		} catch (Exception e) {
			logger.warn("cannot get reader when query [{}]", query);
		}

		return isContentFind();
	}

//	private void extractIPA(String line, int lastPos) {
//		line = PhoneticSymbols.unescapeNumericHTML(line);
//		int startPos = line.indexOf(IPAPrefix, lastPos);
//		int endPos = line.indexOf("/", startPos + IPAPrefix.length());
//		int endPosOfSmallThan = line.indexOf("<", startPos + IPAPrefix.length());
//		if (endPosOfSmallThan > 0 && endPosOfSmallThan < endPos) {
//			endPos = endPosOfSmallThan;
//		}
//
//		// only set the IPA if the ipa found within limit
//		if (startPos > 0 && startPos < (lastPos + IPAPositionLimit)) {
//			ipa = PhoneticSymbols.filterIPA(PhoneticSymbols.convertGoogleIPA(line.substring(startPos + IPAPrefix.length(), endPos)));
//		}
//	}

	private int extractAudioLink(String line) {
		int startPos = line.indexOf(audioPrefix);
		if (startPos < 0) return startPos;

		// audio line find
		startPos = line.indexOf(audioURLPrefix, startPos);
		int endPos = line.indexOf(audioSuffix, startPos);
		audioLink = line.substring(startPos, endPos) + audioSuffix;
		audioLink = audioLink.replace("#skLicensedUrl('')/media/", "");
		audioLink = audioLink.replace("$dictCode", "british");
		return endPos;
	}

	private String concatURL() {
		return URLPrefix + query;
	}

	private String concatURL2() {
		return URLPrefix + query + "_1?q=" + query;
	}

	@Override
	public String getSourceLink() {
		return concatURL() + " or " + concatURL2();
	}

	@Override
	public String getParsedContent() {
		return MessageFormat.format("[ipa:{0}],[audioLink:{1}]", ipa, audioLink);
	}


	@Override
	public boolean isContentCorrect() {
		return getParsedContent().equals(expectedContent);
	}
}
