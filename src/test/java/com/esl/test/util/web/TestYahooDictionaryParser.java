package com.esl.test.util.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.esl.util.web.DictionaryParser;
import com.esl.util.web.YahooDictionaryParser;

public class TestYahooDictionaryParser {

	@Test
	public void testParseBanana() {
		DictionaryParser p = YahooDictionaryParser.toParse("banana");

		assertTrue(p.parse());
		assertEquals("bəˋnɑ:nə",p.getIpa());
		assertEquals("https://s.yimg.com/tn/dict/kh/v1/18498.mp3",p.getAudioLink());
	}

}
