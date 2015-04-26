package com.esl.service.practice;

import java.sql.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.dao.practice.IMemberPracticeScoreCardDAO;
import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.entity.practice.qa.IrregularVerb;
import com.esl.enumeration.ESLPracticeType;
import com.esl.exception.IllegalParameterException;
import com.esl.model.Member;
import com.esl.model.PhoneticQuestion;
import com.esl.util.practice.PhoneticQuestionUtil;

@Service("irregularVerbPracticeService")
public class IrregularVerbPracticeService implements IIrregularVerbPracticeService {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	// supporting class
	@Resource private IPhoneticQuestionDAO phoneticQuestionDAO;
	@Resource private IMemberPracticeScoreCardDAO scoreCardDAO;
	@Resource private PhoneticQuestionUtil phoneticQuestionUtil;

	@Override
	public int checkAnswer(IrregularVerb entity, IrregularVerb input) {
		final String logPrefix = "checkAnswer: ";
		logger.info("{}START", logPrefix);
		logger.debug("{}Question [{}], Answer [{}]", new Object[] {logPrefix, entity, input});

		int corrects = 0;

		if (entity.getPresent().toLowerCase().equals(input.getPresent().toLowerCase())) corrects++;
		if (entity.getPresentParticiple().toLowerCase().equals(input.getPresentParticiple().toLowerCase())) corrects++;
		if (entity.getPast().toLowerCase().equals(input.getPast().toLowerCase())) corrects++;
		if (entity.getPastParticiple().toLowerCase().equals(input.getPastParticiple().toLowerCase())) corrects++;

		return corrects - 1;
	}

	@Override
	public PhoneticQuestion getPhoneticQuestionByVerb(IrregularVerb verb) {
		final String logPrefix = "getPhoneticQuestionByVerb: ";
		logger.info("{}START", logPrefix);
		PhoneticQuestion pq = phoneticQuestionDAO.getPhoneticQuestionByWord(verb.getPresent());

		if (pq == null || !pq.enriched()) {
			logger.info("Cannot find phonetic question [{}]", verb.getPresent());
			pq = new PhoneticQuestion();
			pq.setWord(verb.getPresent());
			phoneticQuestionUtil.findIPA(pq);
		}	
		
		return pq;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)	// commit the score card asap
	public MemberPracticeScoreCard updateScoreCard(Member member, Date today, int addScore) {
		final String logPrefix = "updateScoreCard: ";
		logger.info("{}START", logPrefix);
		if (member == null || today==null) throw new IllegalParameterException(new String[]{"member, today"}, new Object[]{member, today});
		logger.debug("{}input userId[{}], today[{}], addScore[{}]", new Object[] {member.getUserId(), today, addScore});

		MemberPracticeScoreCard scoreCard = scoreCardDAO.getScoreCard(member, ESLPracticeType.IrregularVerbPractice, today);
		scoreCard.addScore(addScore);
		scoreCardDAO.persist(scoreCard);
		return scoreCard;
	}

	// ============== Setter / Getter ================//
	public void setPhoneticQuestionDAO(IPhoneticQuestionDAO phoneticQuestionDAO) {this.phoneticQuestionDAO = phoneticQuestionDAO;}
	public void setMemberPracticeScoreCardDAO(IMemberPracticeScoreCardDAO scoreCardDAO) {this.scoreCardDAO = scoreCardDAO; }
	public void setPhoneticQuestionUtil(PhoneticQuestionUtil util) {this.phoneticQuestionUtil = util; }
}
