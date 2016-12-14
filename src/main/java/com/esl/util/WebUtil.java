package com.esl.util;

import com.esl.entity.rest.WebItem;
import com.esl.service.rest.WebParserRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WebUtil {
	private static Logger logger = LoggerFactory.getLogger("ESL");
	public static int MAX_QUERY_RESULT = 50;

	public static String[] searchImageUrls(String query) {
		logger.info("getImageUrlFromWeb from word [{}]", query);
		WebParserRestService service = new WebParserRestService(System.getProperty("APISERVER_HOST"));

		try {
			WebItem[] items = service.searchGoogleImage(query + " clipart").get().getBody();
			String[] urls = Arrays.stream(items).map(i -> i.url).collect(Collectors.toList()).toArray(new String[]{});
			logger.info("Urls found: {}", urls);
			return urls;
		} catch (Exception e) {
			logger.warn("Cannot getImageUrlFromWeb", e);
			return new String[]{};
		}
	}
}
