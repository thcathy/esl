package com.esl.dao;

import java.util.List;
import java.util.TreeSet;

import com.esl.model.*;
import com.esl.model.TopResult.OrderType;
import com.esl.model.practice.PhoneticSymbols.Level;

public interface IPracticeResultDAO extends IESLDao<PracticeResult> {

	public PracticeResult getPracticeResultById(Long id);
	public PracticeResult getPracticeResult(Member member, Grade grade, String practiceType);
	public PracticeResult getPracticeResult(Member member, Grade grade, String practiceType, Level level);
	public PracticeResult getRandomPracticeResult(Member member, Grade grade, String practiceType);
	public List<PracticeResult> getGradedPracticeResults(Member member, String practiceType);
	public List<PracticeResult> getGradedPracticeResults(Member member, String practiceType, Level level);
	public void makePersistent(PracticeResult result);
	public void makeTransient(PracticeResult result);

	public PracticeResult getBestPracticeResultByMember(Member member, String practiceType);
	public PracticeResult getFavourPracticeResultByMember(Member member, String practiceType);
	public List<PracticeResult> getTopScores(Grade grade, String practiceType);
	public List<PracticeResult> getTopScores(Grade grade, String practiceType, Level level);
	public List<PracticeResult> getTopRatings(Grade grade, String practiceType);
	public List<PracticeResult> getTopRatings(Grade grade, String practiceType, Level level);
	public List<PracticeResult> getResultLower(OrderType orderType, PracticeResult result);
	public List<PracticeResult> getResultHigher(OrderType orderType, PracticeResult result);
	public List<Grade> getAvailableGrades(String practiceType);
	public List<Grade> getAvailableGrades(String practiceType, Level level);
	public TreeSet<PracticeResult> getAllResults(String practiceType, OrderType orderType,  boolean withGrade);
	public int getPosition(OrderType orderType, PracticeResult pr);

	/**
	 * Get the result have highest grading
	 */
	public PracticeResult getHighestGradingByMember(Member member, String practiceType);

	public void initLinkage(PracticeResult practiceResult);
}
