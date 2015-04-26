package com.esl.test.service.practice;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.entity.practice.qa.IrregularVerb;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.IrregularVerbPracticeService;
import com.esl.util.practice.PhoneticQuestionUtil;

@RunWith(MockitoJUnitRunner.class)
public class IrregularVerbPracticeServiceTest {
	@Mock PhoneticQuestionUtil mockPhoneticQuestionUtil;
	@Mock IPhoneticQuestionDAO mockPhoneticQuestionDao;
	IrregularVerbPracticeService service;
	
	@Before
	public void setupService() {
		service = new IrregularVerbPracticeService();
		service.setPhoneticQuestionDAO(mockPhoneticQuestionDao);
		service.setPhoneticQuestionUtil(mockPhoneticQuestionUtil);
	}
	
	@Test
	public void getPhoneticQuestionByVerb_givenQuestionInDb_shouldNotFindFromWeb() {
		final PhoneticQuestion expectedQuestion = new PhoneticQuestion("dummy","dummy", "url", "backupUrl");
		when(mockPhoneticQuestionDao.getPhoneticQuestionByWord(Mockito.any())).thenReturn(expectedQuestion);
		
		PhoneticQuestion question = service.getPhoneticQuestionByVerb(new IrregularVerb("","","",""));
		
		assertEquals(expectedQuestion.getWord(), question.getWord());
		assertEquals(expectedQuestion.getIPA(), question.getIPA());		
		verify(mockPhoneticQuestionUtil, times(0)).findIPA(Mockito.any());
	}
	
	@Test
	public void getPhoneticQuestionByVerb_givenQuestionNotInDb_shouldFindFromWeb() {
		when(mockPhoneticQuestionDao.getPhoneticQuestionByWord(Mockito.any())).thenReturn(null);
		
		PhoneticQuestion question = service.getPhoneticQuestionByVerb(new IrregularVerb("","","",""));
		
		verify(mockPhoneticQuestionUtil, times(1)).findIPA(Mockito.any());
	}
	
	@Test
	public void getPhoneticQuestionByVerb_givenQuestionNotEnriched_shouldFindFromWeb() {
		final String DUMMY = "dummy";
		when(mockPhoneticQuestionDao.getPhoneticQuestionByWord(Mockito.any())).thenReturn(new PhoneticQuestion(DUMMY,null));
		
		PhoneticQuestion question = service.getPhoneticQuestionByVerb(new IrregularVerb(DUMMY,"","",""));
		
		verify(mockPhoneticQuestionUtil, times(1)).findIPA(Mockito.any());
	}
}
