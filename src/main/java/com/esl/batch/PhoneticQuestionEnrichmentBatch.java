package com.esl.batch;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.model.PhoneticQuestion;
import com.esl.util.web.DictionaryParser;
import com.esl.util.web.DictionaryParserFactory;

public class PhoneticQuestionEnrichmentBatch {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private IPhoneticQuestionDAO dao;
	private DictionaryParserFactory parserFactory;
	
	public PhoneticQuestionEnrichmentBatch(IPhoneticQuestionDAO dao, DictionaryParserFactory factory) {
		this.dao = dao;
		this.parserFactory = factory;
	}
	
	public long processAllQuestions() {
		List<PhoneticQuestion> questions = dao.getAll();
		long proceed = questions.stream().map(this::processSingle).filter(x -> x).count();
		logger.info("Enriched Question {} out of {}", proceed, questions.size());
		return proceed;
	}
	
	private boolean processSingle(PhoneticQuestion question) {
		logger.debug("Process single word [{}]", question.getWord());
		Optional<PhoneticQuestion> enrichedQuestion = enrich(question, parserFactory.yahooParserWith(question.getWord()), parserFactory.cambridgeParserWith((question.getWord())));
		enrichedQuestion.ifPresent(dao::persist);		
		
		if (enrichedQuestion.isPresent()) 
			return true;
		else {
			logger.debug("Cannot enrich [{}]", question.getWord());
			return false;
		}
	}
	
	private Optional<PhoneticQuestion> enrich(PhoneticQuestion question, DictionaryParser primary, DictionaryParser secondary) {
		if (!primary.parse()) 
			return Optional.empty();
		
		if (!secondary.parse())
		{
			logger.debug("Fail to parse [{}] from secondary source, use primary only", question.getWord());
			question.setIPA(primary.getIpa());
			question.setPronouncedLink(primary.getAudioLink());
		} else {
			question.setIPA(primary.getIpa());
			question.setPronouncedLinkBackup(primary.getAudioLink());
			question.setPronouncedLink(secondary.getAudioLink());
		}
		return Optional.of(question);
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/com/esl/ESL-context.xml");
			    
		IPhoneticQuestionDAO dao = (IPhoneticQuestionDAO)ctx.getBean("phoneticQuestionDAO");
		PhoneticQuestionEnrichmentBatch batch = new PhoneticQuestionEnrichmentBatch(dao, new DictionaryParserFactory());
		batch.processAllQuestions();
		
		System.exit(0);
	}
}