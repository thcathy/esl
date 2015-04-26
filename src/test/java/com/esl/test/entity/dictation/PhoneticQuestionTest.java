package com.esl.test.entity.dictation;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.esl.model.PhoneticQuestion;
import com.esl.util.WebUtil;
import com.esl.util.practice.PhoneticQuestionUtil;
import com.esl.util.practice.PhoneticQuestionUtil.FindIPAAndPronoun;

@RunWith(PowerMockRunner.class)
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
	public void returnListOfFullPathIfNoFilePathStored() {
		PhoneticQuestion q = new PhoneticQuestion("abc", "abc");
		String[] picsFullPaths = q.getPicsFullPaths();

		assertEquals(WebUtil.MAX_QUERY_RESULT, picsFullPaths.length);
		for (String path : picsFullPaths) {
			assertTrue(path.contains("bing.net"));
		}
	}

	@Test @PrepareForTest(WebUtil.class)
	public void queryWebOnceOnlyAfterFirstCall() {
		mockStatic(WebUtil.class);
		expect(WebUtil.getThumbnailsFromBing("abc")).andReturn(new String[] {"filepath1"}).once();
		replay(WebUtil.class);

		PhoneticQuestion q = new PhoneticQuestion("abc", "abc");
		String[] picsFullPaths = q.getPicsFullPaths();
		picsFullPaths = q.getPicsFullPaths();

		verify(WebUtil.class);

		assertEquals(1, picsFullPaths.length);
	}

	@Test @PrepareForTest(WebUtil.class)
	public void testGetPicsFullPathsString() {
		mockStatic(WebUtil.class);
		expect(WebUtil.getThumbnailsFromBing("abc")).andReturn(new String[] {"filepath1", "filepath2"}).once();
		replay(WebUtil.class);
		String expectedString = "filepath1,filepath2";

		PhoneticQuestion q = new PhoneticQuestion("abc", "abc");
		assertEquals(expectedString, q.getPicsFullPathsInString());
	}
	
	@Test
	public void testGetText2SpeechMp3() {
		PhoneticQuestion question = new PhoneticQuestion("It is not a word", null);		
		PhoneticQuestionUtil util = new PhoneticQuestionUtil();
		
		FindIPAAndPronoun service = util.new FindIPAAndPronoun(new ArrayList<PhoneticQuestion>(), question, null, null);		
		service.run();
			
		assertNotNull(question.getPronouncedLink());
		assertTrue(question.getPronouncedLink().contains(".mp3"));
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
}
