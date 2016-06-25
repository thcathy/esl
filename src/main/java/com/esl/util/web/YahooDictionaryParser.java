package com.esl.util.web;

import java.net.HttpURLConnection;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YahooDictionaryParser implements DictionaryParser {
	private Logger logger = LoggerFactory.getLogger(getClass());

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
			
			Elements tagDDs = doc.select("div.pronun").select("dt:containsOwn(DJ) + dd");
			
			Optional<Boolean> result = tagDDs.stream().map(this::parseIpaAndAudioLink).filter(x->x).findFirst();			
			
			if (result.isPresent()) {
				logger.debug("Parsed ipa[{}], audio[{}] for word[{}]", ipa, audioLink, word);
				return true;
			} else {
				logger.warn("Cannot parse word [{}] from url [{}]", word, url());
				return false;
			}
		} catch (Exception e1) {
			logger.warn("Exception found when parse " + word + " from url " + url(), e1);
		}
		
		return false;
	}
	
	private boolean parseIpaAndAudioLink(Element dd) {
		try {
			ipa = trimBracket(dd.ownText());
			audioLink = dd.parent().parent().select("source[type=audio/mpeg]").attr("src");
			return true;
		} catch (Exception e1) {
			logger.warn("Exception found when parse " + word + " from url " + url(), e1);
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
