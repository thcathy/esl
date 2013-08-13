package com.esl.dao.practice;

import java.util.List;

import com.esl.dao.IESLDao;
import com.esl.entity.practice.PracticeMedal;
import com.esl.enumeration.ESLPracticeType;

public interface IPracticeMedalDAO extends IESLDao<PracticeMedal> {
	/**
	 * Get all medals of given type and month
	 * @param type
	 * @param date Sql Date which will round to start of date
	 */
	public List<PracticeMedal> listPracticeMedals(ESLPracticeType type, java.sql.Date date);

	/**
	 * Random return a practice medal
	 * @return a PracticeMedal
	 */
	public PracticeMedal getRandomPracticeMedal();
}
