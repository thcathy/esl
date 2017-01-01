package com.esl.util.web;

import com.esl.model.practice.PhoneticSymbols;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.HttpURLConnection;

public class CambridgeDictionaryParser implements DictionaryParser {
	private Logger logger = LoggerFactory.getLogger(CambridgeDictionaryParser.class);

	final String URLPrefix = "http://dictionary.cambridge.org/dictionary/british/";
	
	String ipa;
	String audioLink;
	String query;			// input word

	// ------------------------ getter / setter --------------------- //
	public CambridgeDictionaryParser(String query) {
		this.query = query.toLowerCase();
	}

	public boolean isContentFind() {
		return StringUtils.hasText(ipa) && StringUtils.hasText(audioLink);
	}

	public String getIpa() {return this.ipa;}
	public String getAudioLink() {return this.audioLink;}

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
			
			Elements audioLinkSource = doc.select("span.audio_play_button.uk");
			audioLink = audioLinkSource.get(0).attr("data-src-mp3");
			
			String orgIPA = doc.select("span.ipa").get(0).ownText();
			logger.debug("org IPA [{}]", orgIPA);
			ipa = PhoneticSymbols.filterIPA(PhoneticSymbols.convertGoogleIPA(orgIPA));
		} catch (Exception e1) {
			logger.warn("cannot get reader when query [{}] from url [{}], reason [{}]", new Object[]{query, url, e1.toString()});
		}
		return isContentFind();
	}
	
	private String concatURL() {
		return URLPrefix + query;
	}

	private String concatURL2() {
		return URLPrefix + query + "_1?q=" + query;
	}
	
	private String concatURL3() {
		return URLPrefix + query + "_2?q=" + query;
	}

}
