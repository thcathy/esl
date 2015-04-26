package com.esl.test.web.jsf.controller.dictation;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.esl.dao.dictation.DictationDAO;
import com.esl.dao.dictation.IDictationDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.web.jsf.controller.dictation.DictationPracticeController;

public class DictationPracticeControllerTest extends EasyMockSupport {
	IDictationDAO mockDictationDAO;

	//	@Before
	//	public void setup() {
	//
	//	}
	//
	@Test
	public void testRecommendDictationSuccess() {
		mockDictationDAO = createMock(DictationDAO.class);
		DictationPracticeController c = new DictationPracticeController();
		Dictation d = new Dictation();
		c.setDictationDAO(mockDictationDAO);
		c.setDictation(d);
		expect(mockDictationDAO.attachSession(d)).andReturn(d);
		mockDictationDAO.persist(anyObject());
		replayAll();

		String target = c.recommendDictation();
		assertEquals("Total recommended + 1", 1, d.getTotalRecommended());
		assertNull("Success call return null string", target);
		assertEquals("Turn on recommended flag", true, c.isRecommended());
	}

	@Test
	public void testRecommendDictationFailWithoutDictation() {
		DictationPracticeController c = new DictationPracticeController();
		String target = c.recommendDictation();
		assertEquals("Dictation is null should return error view","/error", target);
		assertEquals("Recommended flag remain false", false, c.isRecommended());
	}
}
