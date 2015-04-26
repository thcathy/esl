package com.esl.test;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlEncodeTest {
	static Logger log = LoggerFactory.getLogger(UrlEncodeTest.class);

	@Test
	public void testUrlEncode() {
		String orgText = "=developerdemokeydeveloperdemokey&action=convert&voice=usenglishfemale1&text=Hello,+this+is+a+test+message"; 
		String encodedText = "%3Ddeveloperdemokeydeveloperdemokey%26action%3Dconvert%26voice%3Dusenglishfemale1%26text%3DHello%2C%2Bthis%2Bis%2Ba%2Btest%2Bmessage";
				
		try {
			assertEquals(encodedText, URLEncoder.encode(orgText, "UTF-8"));			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
