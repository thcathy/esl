package com.esl.test.entity.dictation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationSentence;

public class TestSentence {
	DictationSentence d1s1;
	DictationSentence d1s2;
	Dictation d1;

	@Before
	public void setup() {
		d1 = new Dictation();
		d1.setId(1l);
		d1s1 = new DictationSentence("AAA", 0, d1);
		d1s1.setId(1l);
		d1s2 = new DictationSentence("BBB", 1, d1);
		d1s2.setId(2l);
	}

	@Test
	public void isEquals() {
		DictationSentence dummyd1s1 = new DictationSentence();
		dummyd1s1.setId(1l);
		assertTrue(d1s1.equals(dummyd1s1));
		assertEquals(d1s1.hashCode(), dummyd1s1.hashCode());

		assertFalse(d1s2.equals(d1s1));
		assertNotSame(d1s1.hashCode(), d1s2.hashCode());
	}

	@Test
	public void sentenceToString() {
		String expectedTextFord1s1 = "DictationSentence(1) [content=AAA, ordering=0, dictationId=1]";
		String expectedTextFordummyd1s1 = "DictationSentence(null) [content=AAA, ordering=0, dictationId=null]";
		DictationSentence dummyd1s1 = new DictationSentence("AAA", 0, d1);
		dummyd1s1.setDictation(null);

		assertEquals(expectedTextFord1s1, d1s1.toString());
		assertEquals(expectedTextFordummyd1s1, dummyd1s1.toString());
	}

}
