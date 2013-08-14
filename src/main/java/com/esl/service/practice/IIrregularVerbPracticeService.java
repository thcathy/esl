package com.esl.service.practice;

import java.sql.Date;

import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.entity.practice.qa.IrregularVerb;
import com.esl.model.Member;
import com.esl.model.PhoneticQuestion;

public interface IIrregularVerbPracticeService {
	/**
	 * Input the orginial irregular verb entity and user input answer
	 * @return total match verb between question and answer (-1 for the match of question)
	 */
	public int checkAnswer(IrregularVerb entity, IrregularVerb input);

	/**
	 * @return a phonetic question including pronounce and IPA
	 */
	public PhoneticQuestion getPhoneticQuestionByVerb(IrregularVerb verb);

	/**
	 * Update score card
	 */
	public MemberPracticeScoreCard updateScoreCard(Member member, Date today, int addScore);
}
