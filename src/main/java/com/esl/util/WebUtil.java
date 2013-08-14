package com.esl.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
		String address = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/Image?Adult=%27Strict%27&ImageFilters=%27Style%3aGraphics%27&Query=%27";
		address += query + "%27&$top=" + MAX_QUERY_RESULT;
		byte[] encodedPassword = ("duhLqrHx48aVaR4J/uWDXYMEtKJUbImkuuLnG1XJ170=" + ":" + "duhLqrHx48aVaR4J/uWDXYMEtKJUbImkuuLnG1XJ170=").getBytes();

		try {
			HttpURLConnection connection;
			URL url = new URL (address);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod ("GET");
			connection.setRequestProperty ("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/5.0)");
			connection.setRequestProperty("Authorization", "Basic " + new String((new org.apache.commons.codec.binary.Base64()).encodeBase64(encodedPassword)));

			NodeList nodeList = getNodesFromInputStreamByTagName(connection.getInputStream(), "d:Thumbnail");
			String[] thumbnailPaths = new String[nodeList.getLength()];
			for (int i=0; i<nodeList.getLength(); i++) {
				thumbnailPaths[i] = nodeList.item(i).getFirstChild().getTextContent();
			}
			return thumbnailPaths;
		} catch (IOException e) {
			logger.debug("Exception during getThumbnailsFromBing from " + address, e);
			return new String[]{};
		}
	}

	public static NodeList getNodesFromInputStreamByTagName(InputStream in, String tagName) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			return doc.getDocumentElement().getElementsByTagName(tagName);
		} catch (Exception e) {
			logger.debug("Exception during getNodesFromInputStreamByTagName", e);
			return null;
		}
	}
}
