/*
package com.esl.batch;

import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.PhoneticQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

@EnableAutoConfiguration
public class PhoneticQuestionEnrichmentBatch {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private IPhoneticQuestionDAO dao;
	private PhoneticQuestionService questionService;
	
	public PhoneticQuestionEnrichmentBatch(IPhoneticQuestionDAO dao, PhoneticQuestionService questionService) {
		this.dao = dao;
        this.questionService = questionService;
	}
	
	public long processAllQuestions(int maxProcess) {
		List<PhoneticQuestion> questions = dao.getAll();
        if (questions.size() < maxProcess)
            maxProcess = questions.size();
        questionService.enrichIfNeeded(questions.subList(0, maxProcess));
		logger.info("Enriched Question {} out of {}", maxProcess, questions.size());
		return maxProcess;
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/com/esl/ESL-context.xml");
			    
		IPhoneticQuestionDAO dao = (IPhoneticQuestionDAO)ctx.getBean("phoneticQuestionDAO");
        PhoneticQuestionService service = ctx.getBean(PhoneticQuestionService.class);
		PhoneticQuestionEnrichmentBatch batch = new PhoneticQuestionEnrichmentBatch(dao, service);
		batch.processAllQuestions(2800);
		
		System.exit(0);
	}
}*/
