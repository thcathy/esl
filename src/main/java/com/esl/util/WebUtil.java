package com.esl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtil {
	private static Logger logger = LoggerFactory.getLogger("ESL");
	public static int MAX_QUERY_RESULT = 50;

	public static BufferedReader getReaderFromURL(String address) throws IOException {
		try {
			HttpURLConnection connection;
			URL url = new URL (address);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod ("GET");
			connection.setRequestProperty ("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/5.0)");
			InputStream in = connection.getInputStream();
			return new BufferedReader(new InputStreamReader(in));
		} catch (IOException e) {
			logger.debug("Exception during connect to " + address, e);
			throw e;
		}
	}

	public static String[] getThumbnailsFromBing(String query) {
		String address = "https://api.datamarket.azure.com/Bing/Search/v1/Image?Adult=%27Strict%27&ImageFilters=%27Style%3AGraphics%27&Query=%27";
		address += query + "%27&$top=" + MAX_QUERY_RESULT;
		byte[] encodedPassword = ("duhLqrHx48aVaR4J/uWDXYMEtKJUbImkuuLnG1XJ170=" + ":" + "duhLqrHx48aVaR4J/uWDXYMEtKJUbImkuuLnG1XJ170=").getBytes();

		try {
			HttpURLConnection connection;
			URL url = new URL (address);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod ("GET");
			//connection.setRequestProperty ("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/5.0)");
			connection.setRequestProperty("Authorization", "Basic " + new String((new org.apache.commons.codec.binary.Base64()).encodeBase64(encodedPassword)));
			
			String[] thumbnailPaths = new String[MAX_QUERY_RESULT];
			Document doc = Jsoup.parse(connection.getInputStream(),null,"",Parser.xmlParser());
			int i=0;
			for (Iterator<Element> iter = doc.select("d|Thumbnail d|mediaurl").iterator(); iter.hasNext(); ) {				
				Element e = iter.next();
				thumbnailPaths[i++]=e.text();
			}
			
			return thumbnailPaths;
		} catch (IOException e) {
			logger.debug("Exception during getThumbnailsFromBing from " + address, e);
			return new String[]{};
		}
	}
}
