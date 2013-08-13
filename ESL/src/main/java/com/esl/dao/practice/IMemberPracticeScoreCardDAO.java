package com.esl.dao.practice;

import java.sql.Date;
import java.util.List;

import com.esl.dao.IESLDao;
import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.enumeration.ESLPracticeType;
import com.esl.model.Member;

public interface IMemberPracticeScoreCardDAO extends IESLDao<MemberPracticeScoreCard> {
	/**
	 * Created a new one if the card cannot be found
	 */
	public MemberPracticeScoreCard getScoreCard(Member member, ESLPracticeType practiceType, Date scoreCardDate);

	/**
	 * Create the score card and commit to db
	 */
	public void persistAndCommit(MemberPracticeScoreCard scoreCard);

	/**
	 * List all score card ordered by standing
	 * @param scoreCardDate: will trim to month
	 */
	public List<MemberPracticeScoreCard> listByStanding(ESLPracticeType practiceType, Date scoreCardDate);

	/**
	 * Create a new score card or update the standing only if score card already exist
	 * @param scoreCard
	 */
	public MemberPracticeScoreCard saveOrUpdateStanding(MemberPracticeScoreCard scoreCard);
}
