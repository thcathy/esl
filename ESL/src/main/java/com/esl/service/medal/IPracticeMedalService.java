package com.esl.service.medal;

import java.util.List;

import com.esl.entity.practice.PracticeMedal;
import com.esl.enumeration.ESLPracticeType;

public interface IPracticeMedalService {
	/**
	 * Call by the spring quartz generate all type of medal
	 */
	public void generateMedalBatch();

	/**
	 * Generate the medals
	 * @param type
	 * @param date no need to round to start of month
	 */
	public void generateMedal(ESLPracticeType type, java.sql.Date date);

	/**
	 * Random get a medal and return all medal of the same month and type
	 * @return a list of medals start from Gold
	 */
	public List<PracticeMedal> getRandomTopMedals();
}
