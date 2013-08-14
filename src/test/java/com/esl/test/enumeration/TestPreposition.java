package com.esl.test.enumeration;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.enumeration.Preposition;
import com.esl.test.dao.TestDictationDao;

public class TestPreposition {
	Logger log = LoggerFactory.getLogger(TestDictationDao.class);

	@Test
	public void testPrepositionRegex() {
		log.debug("Preposition regex [{}]", Preposition.PREPOSITION_REGEX);
		Pattern p = Pattern.compile(Preposition.PREPOSITION_REGEX);
		String[] result = p.split("I am in front of the bus inside a tree.");
		assertEquals("I am ", result[0]);
		assertEquals(" the bus ",result[1]);
		assertEquals(" a tree.",result[2]);
	}
}
