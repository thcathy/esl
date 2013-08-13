package com.esl.test.entity.dictation;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.esl.model.PhoneticQuestion;
import com.esl.util.WebUtil;

@RunWith(PowerMockRunner.class)
public class TestPhoneticQuestion {

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
			assertTrue(path.contains("bing.net/images/thumbnail"));
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
}
