package com.esl.service.practice;

import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.enumeration.VocabDifficulty;
import com.esl.model.Member;
import com.esl.model.PhoneticQuestion;
import com.esl.model.practice.PhoneticSymbols;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.web.model.practice.PhoneticPracticeSummary;

import java.sql.Date;
import java.util.Set;

public interface IPhoneticSymbolPracticeService extends IPhoneticPracticeService {
	public Set<String> getPhonicsListByLevel(PhoneticSymbols.Level level, String requiredPhonics);
	public boolean checkAnswer(PhoneticQuestion question, String answer);

	/**
	 * Get the model contain data of phonetic practice for member summary page
	 */
	public PhoneticPracticeSummary getPhoneticPracticeSummary(Member member);

	/**
	 * Update score card
	 */
	public MemberPracticeScoreCard updateScoreCard(Member member, Date today, boolean isCorrect, PhoneticQuestion question, Level level);

	public int calculateScore(VocabDifficulty difficulty, Level level);
}
