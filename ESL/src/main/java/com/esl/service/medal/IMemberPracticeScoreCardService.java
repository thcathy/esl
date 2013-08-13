package com.esl.service.medal;

import java.util.List;

import com.esl.entity.practice.MemberPracticeScoreCard;

public interface IMemberPracticeScoreCardService {
	/**
	 * create next month score cards with last month standing
	 * @param lastMonthScoreCards
	 * @return total score cards created
	 */
	public int createNextMonthScoreCards(List<MemberPracticeScoreCard> lastMonthScoreCards);
}
