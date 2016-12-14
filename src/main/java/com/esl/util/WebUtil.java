package com.esl.util;

import com.esl.entity.rest.WebItem;
import com.esl.service.rest.WebParserRestService;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WebUtil {
	private static Logger logger = LoggerFactory.getLogger("ESL");
	public static int MAX_QUERY_RESULT = 50;

	private static WebParserRestService service = null;

	public static synchronized WebParserRestService getInstance() {
		if (service == null) {
			String host = System.getenv("APISERVER_HOST");
			if (Strings.isNullOrEmpty(host)) host = System.getProperty("APISERVER_HOST");
			if (Strings.isNullOrEmpty(host)) host = "funfunspell.com:8091";
			service = new WebParserRestService(host);
		}
		return service;
	}

	public static String[] searchImageUrls(String query) {
		logger.info("getImageUrlFromWeb from word [{}]", query);

		try {
			WebItem[] items = getInstance().searchGoogleImage(query + " clipart").get().getBody();
			String[] urls = Arrays.stream(items).map(i -> i.url).collect(Collectors.toList()).toArray(new String[]{});
			logger.info("Urls found: {}", urls);
			return urls;
		} catch (Exception e) {
			logger.warn("Cannot getImageUrlFromWeb", e);
			return new String[]{};
		}
	}
}
