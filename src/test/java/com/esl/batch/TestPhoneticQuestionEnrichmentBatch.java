package com.esl.batch;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mock;

import com.esl.dao.PhoneticQuestionDAO;
import com.esl.model.PhoneticQuestion;
import com.esl.util.web.DictionaryParserFactory;


public class TestPhoneticQuestionEnrichmentBatch {

	@Mock private PhoneticQuestionDAO mockDao;
	@Mock private DictionaryParserFactory mockParserFactory;
	
	@Test
	public void processAllQuestions_givenTwoQuestion_shouldProcessThem() {
		PhoneticQuestion q1 = new PhoneticQuestion("banana", "");
		PhoneticQuestion q2 = new PhoneticQuestion("zinc", "");
		when(mockDao.getAll()).thenReturn(Arrays.asList(q1, q2));
		
		PhoneticQuestionEnrichmentBatch batch = new PhoneticQuestionEnrichmentBatch(mockDao, new DictionaryParserFactory());
		batch.processAllQuestions();
		
		verify(mockDao, times(2));
	}
}
