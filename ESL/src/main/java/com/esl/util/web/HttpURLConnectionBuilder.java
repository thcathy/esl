package com.esl.util.web;

import java.net.*;

public class HttpURLConnectionBuilder {
	String uri;

	static String proxyUrl;
	static int proxyPort;

	public HttpURLConnection createConnection() {
		HttpURLConnection connection = null;
		try {
			URL url = new URL (uri);

			if (proxyUrl != null) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
				connection = (HttpURLConnection)url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection)url.openConnection();
			}
			//connection.setRequestMethod ("POST");
			connection.setRequestProperty ("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/5.0)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public HttpURLConnectionBuilder setURL(String uri) {
		this.uri = uri;
		return this;
	}

	public static String getProxyUrl() {return proxyUrl;}
	public static void setProxyUrl(String proxyUrl) {HttpURLConnectionBuilder.proxyUrl = proxyUrl;}

	public static int getProxyPort() {return proxyPort;}
	public static void setProxyPort(int proxyPort) {HttpURLConnectionBuilder.proxyPort = proxyPort;}
}
