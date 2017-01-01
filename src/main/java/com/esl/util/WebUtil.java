package com.esl.util;

import com.esl.entity.rest.WebItem;
import com.esl.service.rest.WebParserRestService;
import com.google.common.base.Strings;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class WebUtil {
	private static Logger logger = LoggerFactory.getLogger(WebUtil.class);
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

	public static String[] searchImageBinary(String query) {
		logger.info("getImageUrlFromWeb from word [{}]", query);

		try {
			WebItem[] items = getInstance().searchGoogleImage(query + " clipart").get().getBody();
			return Arrays.stream(items)
					.map(i -> {
						try {
							System.out.println(i.url);
							String extension = i.url.substring(i.url.lastIndexOf('.') + 1);
							System.out.println(extension);
							byte[] byteArray = IOUtils.toByteArray(Unirest.get(i.url).asBinary().getBody());
							System.out.println(byteArray);
							String url = "data:image/" + extension + ";base64," + StringUtils.newStringUtf8(org.apache.commons.codec.binary.Base64.encodeBase64(byteArray, false));
							return url;
						} catch (IOException e) {
							e.printStackTrace();
						} catch (UnirestException e) {
							e.printStackTrace();
						}
						return "";
					})
					.collect(Collectors.toList()).toArray(new String[10]);
		} catch (Exception e) {
			logger.warn("Cannot getImageUrlFromWeb", e);
			return new String[0];
		}
	}
}
