package com.esl.util.web;

import java.net.HttpURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YahooDictionaryParser implements DictionaryParser {
	private Logger logger = LoggerFactory.getLogger("ESL");

	final String URLPrefix = "https://hk.dictionary.yahoo.com/dictionary?p=";
	final String referer = "https://hk.dictionary.yahoo.com/";
	
	private final String word;
	
	String ipa;
	String audioLink;
	
	private YahooDictionaryParser(String word) {
		this.word = word;
	}
	
	public static DictionaryParser toParse(String word) {
		return new YahooDictionaryParser(word);
	}

	@Override
	public String getIpa() {
		return ipa;
	}

	@Override
	public String getAudioLink() {
		return audioLink;
	}

	@Override
	public boolean parse() {
		Document doc;
		
		try {			
			HttpURLConnection connection = new HttpURLConnectionBuilder().setURL(url())
												.referer(referer)
												.createConnection();
			doc = Jsoup.parse(connection.getInputStream(), "UTF-8", connection.getURL().getPath());
			
			Elements dataDiv = doc.select("div.provider-kanhan + div");
			ipa = trimBracket(dataDiv.get(0).child(0).child(3).ownText());			
			audioLink = dataDiv.select("source[type=audio/mpeg]").attr("src");
			
			logger.debug("Parsed ipa[{}], audio[{}] for word[{}]", ipa, audioLink, word);
			return true;
		} catch (Exception e1) {
			logger.warn("cannot get reader when query [{}] from url [{}], reason [{}]", new Object[]{word, url(), e1.toString()});
		}
		
		return false;
	}

	private String trimBracket(String text) {
		return text.substring(1, text.length()-1);
	}

	private String url() {
		return URLPrefix + word;
	}
	
}
