package com.esl.util.web;

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

public class CambridgeDictionaryParser implements SourceChecker, DictionaryParser {
	private Logger logger = LoggerFactory.getLogger("ESL");

	final String URLPrefix = "http://dictionary.cambridge.org/dictionary/british/";
	
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
		if (!tryParseFromUrl(concatURL()))
			if (!tryParseFromUrl(concatURL2()))
				if (!tryParseFromUrl(concatURL3())) {}
		
		return isContentFind();
	}
	
	private boolean tryParseFromUrl(String url) {
		Document doc;
		
		try {			
			HttpURLConnection connection = new HttpURLConnectionBuilder().setURL(url).createConnection();
			doc = Jsoup.parse(connection.getInputStream(), "UTF-8", connection.getURL().getPath());
			
			Elements audioLinkSource = doc.select("span.sound.audio_play_button.pron-icon.uk");
			audioLink = audioLinkSource.get(0).attr("data-src-mp3");
			
			String orgIPA = doc.select("span.ipa").get(0).ownText();
			logger.debug("org IPA [{}]", orgIPA);
			ipa = PhoneticSymbols.filterIPA(PhoneticSymbols.convertGoogleIPA(orgIPA));
		} catch (Exception e1) {
			logger.warn("cannot get reader when query [{}] from url [{}], reason [{}]", new Object[]{query, url, e1.toString()});
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
	
	private String concatURL() {
		return URLPrefix + query;
	}

	private String concatURL2() {
		return URLPrefix + query + "_1?q=" + query;
	}
	
	private String concatURL3() {
		return URLPrefix + query + "_2?q=" + query;
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
