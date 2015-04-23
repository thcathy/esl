package com.esl.service.practice;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.enumeration.ESLPracticeType;
import com.esl.exception.IllegalParameterException;
import com.esl.model.Member;
import com.esl.model.PhoneticQuestion;
import com.esl.model.PracticeResult;
import com.esl.model.practice.PhoneticSymbols;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.web.model.practice.PhoneticPracticeSummary;

@Service("phoneticSymbolPracticeService")
@Transactional
public class PhoneticSymbolPracticeService extends PhoneticPracticeService implements IPhoneticSymbolPracticeService {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	/**
	 * Check answer for phonetic symbol practice
	 */
	public boolean checkAnswer(PhoneticQuestion question, String answer) {
		logger.info("checkAnswer: START");
		if (question == null) throw new IllegalParameterException(new String[]{"phonetic question"}, new Object[]{question});

		logger.info("checkAnswer: answer [{}], question.IPA [{}]", answer, question.getIPA());

		return question.ipaEqual(answer);
	}

	/**
	 * Return a random set contain phonics gif name including input phonics list
	 */
	public Set<String> getPhonicsListByLevel(PhoneticSymbols.Level level, String requiredPhonics) {
		logger.info("getPhonicsListByLevel: START");
		logger.info("getPhonicsListByLevel: required phonics [" +requiredPhonics + "]");
		logger.info("getPhonicsListByLevel: level [" + level + "]");

		int totalPhonics = PhoneticSymbols.difficultyValueMap.get(level);
		logger.info("getPhonicsListByLevel: totalPhonics from difficulty [" + totalPhonics + "]");

		Set<String> resultSet = new HashSet<String>();

		if (totalPhonics >= PhoneticSymbols.allPhonics.size()) {
			resultSet.addAll(PhoneticSymbols.allPhonics);				// totalPhonics larger then allPhonics
		} else {
			resultSet.addAll(getPhonics(requiredPhonics));							// normal case that need to random phonics

			// add random phoncis for selection
			if (totalPhonics > resultSet.size()) {
				Set<String> availablePhonics = new HashSet<String>();
				availablePhonics.addAll(PhoneticSymbols.allPhonics);
				for (String s : resultSet) {								// remove requiredPhonics from available
					availablePhonics.remove(s);
				}
				List<String> resortList = new ArrayList<String>();
				resortList.addAll(availablePhonics);
				Collections.shuffle(resortList);							// shuffle available phonics
				resultSet.addAll(resortList.subList(0, totalPhonics - resultSet.size()));
			}
		}

		return resultSet;
	}

	public PhoneticPracticeSummary getPhoneticPracticeSummary(Member member) {
		final String logPrefix = "getPhoneticPracticeSummary: ";
		logger.debug(logPrefix + "START");
		if (member == null) return null;

		PhoneticPracticeSummary summary = new PhoneticPracticeSummary();

		// Random a level
		Random r = new Random();
		summary.setOverallPracticeResult(practiceResultDAO.getRandomPracticeResult(member, null, PracticeResult.PHONETICSYMBOLPRACTICE));
		summary.setFavourPracticeResult(practiceResultDAO.getFavourPracticeResultByMember(member, PracticeResult.PHONETICSYMBOLPRACTICE));

		return summary;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)	// commit the score card asap
	public MemberPracticeScoreCard updateScoreCard(Member member, Date today, boolean isCorrect, PhoneticQuestion question, Level level) {
		final String logPrefix = "updateScoreCard: ";
		logger.debug("{}START", logPrefix);
		if (member == null || today==null || question==null) throw new IllegalParameterException(new String[]{"member, today"}, new Object[]{member, today, question});
		logger.debug("{}input userId[{}], today[{}], isCorrect[{}], question.word[{}], level[{}]", new Object[] {logPrefix, member.getUserId(), today, isCorrect, question.getWord(), level});

		MemberPracticeScoreCard scoreCard = scoreCardDAO.getScoreCard(member, ESLPracticeType.PhoneticPractice, today);
		if (isCorrect) {
			phoneticQuestionDAO.makePersistent(question);
			int gradeLevel = question.getGrades().get(0).getLevel();
			logger.debug("{}question level weight[{}]", logPrefix, gradeLevel);
			scoreCard.addScore(gradeLevel * level.weight);
			scoreCardDAO.persist(scoreCard);
			logger.debug("{}Added [{}] to scoreCard [{}]", new Object[] {logPrefix, gradeLevel * level.weight, scoreCard});
		}

		return scoreCard;
	}

	// -------------------- Supporting Functions ------------------- //

	protected Set<String> getPhonics(String requiredPhonics) {
		Set<String> symbols = new HashSet<String>();
		int startPos = 0;

		while (startPos < requiredPhonics.length()) {
			if (startPos+1 < requiredPhonics.length() && PhoneticSymbols.getAllPhonics().contains(requiredPhonics.substring(startPos, startPos+2))) {
				symbols.add(requiredPhonics.substring(startPos, startPos+2));
				startPos = startPos +2;
			} else {
				symbols.add(requiredPhonics.substring(startPos, startPos+1));
				startPos++;
			}
		}
		logger.info("getPhonics: return symbols[" + symbols + "]");
		return symbols;
	}
}
