package com.esl.batch;
import org.junit.Test;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.extension.listener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;

import com.esl.batch.PhoneticQuestionEnrichmentBatch;
import com.esl.dao.PhoneticQuestionDAO;
import com.esl.util.web.DictionaryParserFactory;


@PowerMockListener(AnnotationEnabler.class)
public class TestPhoneticQuestionEnrichmentBatch {

	@Mock private PhoneticQuestionDAO dao;
	@Mock private DictionaryParserFactory parserFactory;
	
	@Test
	public void processAllQuestions_givenTwoQuestion_shouldProcessThem() {
		
		PhoneticQuestionEnrichmentBatch batch = new PhoneticQuestionEnrichmentBatch(dao, new DictionaryParserFactory());
	}
}
