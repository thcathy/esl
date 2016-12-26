package com.esl.batch;
import com.esl.dao.PhoneticQuestionDAO;
import com.esl.model.PhoneticQuestion;
import com.esl.util.web.DictionaryParserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhoneticQuestionEnrichmentBatchTest {

	@Mock private PhoneticQuestionDAO mockDao;
	@Mock private DictionaryParserFactory mockParserFactory;
	
	@Test
	public void processAllQuestions_givenTwoQuestion_shouldProcessThem() {
		PhoneticQuestion q1 = new PhoneticQuestion("banana", "");
		PhoneticQuestion q2 = new PhoneticQuestion("zinc", "");
		when(mockDao.getAll()).thenReturn(Arrays.asList(q1, q2));
		
		/*PhoneticQuestionEnrichmentBatch batch = new PhoneticQuestionEnrichmentBatch(mockDao, new DictionaryParserFactory());
		long enrichedQuestions = batch.processAllQuestions();
		
		assertEquals(2, enrichedQuestions);
		verify(mockDao, times(2)).persist(Mockito.any());*/
	}
	
	@Test
	public void processAllQuestions_givenWrongQuestion_shouldNotProcessThem() {
		PhoneticQuestion q1 = new PhoneticQuestion("!@#!@#", "");
		PhoneticQuestion q2 = new PhoneticQuestion("", "");
		when(mockDao.getAll()).thenReturn(Arrays.asList(q1, q2));
		
		/*PhoneticQuestionEnrichmentBatch batch = new PhoneticQuestionEnrichmentBatch(mockDao, new DictionaryParserFactory());
		batch.processAllQuestions();
		long enrichedQuestions = batch.processAllQuestions();
		
		assertEquals(0, enrichedQuestions);
		verify(mockDao, times(0)).persist(Mockito.any());*/
	}
}
