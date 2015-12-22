package com.esl.test.util.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.esl.util.web.DictionaryParser;
import com.esl.util.web.YahooDictionaryParser;

public class YahooDictionaryParserTest {

	@Test
	public void testParseBanana() {
		DictionaryParser p = YahooDictionaryParser.toParse("banana");

		assertTrue(p.parse());
		assertEquals("bəˋnɑ:nə",p.getIpa());
		assertEquals("https://s.yimg.com/tn/dict/dreye/live/f/banana.mp3",p.getAudioLink());
	}
	
	@Test
	public void testParseBananaUpperCase() {
		DictionaryParser p = YahooDictionaryParser.toParse("BANANA");

		assertTrue(p.parse());
		assertEquals("bəˋnɑ:nə",p.getIpa());
		assertEquals("https://s.yimg.com/tn/dict/dreye/live/f/banana.mp3",p.getAudioLink());
	}
	
	@Test
	public void parse_givenWordIn_ShouldSuccess() {
		DictionaryParser p = YahooDictionaryParser.toParse("in");

		assertTrue(p.parse());
		assertEquals("in",p.getIpa());
		assertEquals("https://s.yimg.com/tn/dict/dreye/live/f/in.mp3",p.getAudioLink());
	}


}
