package com.esl.test.entity.dictation;

import com.esl.model.PhoneticQuestion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhoneticQuestionTest {

	@Test
	public void onlyReturnStoredFilePathIfAny() {
		PhoneticQuestion q = new PhoneticQuestion("abc", "abc");
		q.setPicFileName("file1.png");

		String[] picsFullPaths = q.getPicsFullPaths();
		assertEquals(1, picsFullPaths.length);
		assertEquals("/ESL/images/graphic/word/file1.png",picsFullPaths[0]);
	}
	
	@Test
	public void toString_givenDetail_ShouldBePrint() {
		String word = "apple";
		String backupUrl = "abc2.com";
		String pronunUrl = "abc.com";
		String ipa = "xyz";
		PhoneticQuestion question = new PhoneticQuestion(word, ipa, pronunUrl, backupUrl);
		
		String printedStr = question.toString();
		assertTrue(printedStr.contains(word));
		assertTrue(printedStr.contains(backupUrl));
		assertTrue(printedStr.contains(pronunUrl));
		assertTrue(printedStr.contains(ipa));
	}
	
	@Test
	public void ipaEqual_givenWithoutStress_ShouldRemainTrue() {
		PhoneticQuestion banana = new PhoneticQuestion("banana", "bəˋnɑ:nə");
		assertTrue(banana.ipaEqual("bənɑ:nə"));
	}
	
	@Test
	public void wordEqual_givenSpaceOrHyphen_ShouldRemainTrue() {
		PhoneticQuestion busstop = new PhoneticQuestion("busstop", "");
		assertTrue(busstop.wordEqual("busstop"));
		assertTrue(busstop.wordEqual("bus-stop"));
		assertTrue(busstop.wordEqual("bus stop"));
		assertTrue(busstop.wordEqual(" bus stop "));
		
		PhoneticQuestion busstopWithHyphen = new PhoneticQuestion("bus-stop", "");
		assertTrue(busstopWithHyphen.wordEqual("busstop"));
		assertTrue(busstopWithHyphen.wordEqual("bus-stop"));
		assertTrue(busstopWithHyphen.wordEqual("bus stop"));
		
		PhoneticQuestion busstopWithSpace = new PhoneticQuestion("bus stop", "");
		assertTrue(busstopWithSpace.wordEqual("busstop"));
		assertTrue(busstopWithSpace.wordEqual("bus-stop"));
		assertTrue(busstopWithSpace.wordEqual("bus stop"));		
	}
	
	@Test
	public void wordEqual_shouldIgnoreCase() {
		PhoneticQuestion busstop = new PhoneticQuestion("busstop", "");
		assertTrue(busstop.wordEqual("Busstop"));
		assertTrue(busstop.wordEqual("Bus-Stop"));
		assertTrue(busstop.wordEqual("BUS STOP"));		
	}
}
