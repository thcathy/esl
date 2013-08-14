package com.esl.util.web;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class HttpURLConnectionBuilder {
	String uri;
	String encodedParams;

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
			connection.setRequestProperty ("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/5.0)");
			
			if (encodedParams != null) {
				connection.setRequestMethod("POST");				
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", "" + Integer.toString(encodedParams.getBytes().length));
				connection.setUseCaches(false);
			    connection.setDoInput(true);
			    connection.setDoOutput(true);
				
				// Set request params
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(encodedParams);
				wr.flush();
				wr.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return connection;
	}
	
	public HttpURLConnectionBuilder setURL(String uri) {
		this.uri = uri;
		return this;
	}
	
	public HttpURLConnectionBuilder setEncodedParams(String params) {
		this.encodedParams = params;
		return this;
	}
		
	public static String getProxyUrl() {return proxyUrl;}
	public static void setProxyUrl(String proxyUrl) {HttpURLConnectionBuilder.proxyUrl = proxyUrl;}

	public static int getProxyPort() {return proxyPort;}
	public static void setProxyPort(int proxyPort) {HttpURLConnectionBuilder.proxyPort = proxyPort;}
}
