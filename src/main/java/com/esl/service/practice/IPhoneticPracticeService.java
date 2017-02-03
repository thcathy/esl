package com.esl.service.practice;

import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.model.*;
import com.esl.web.model.practice.VocabPracticeSummary;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface IPhoneticPracticeService {
	// Use for check answer
	public static final String CORRECT_ANSWER = "CORRECT_ANSWER";
	public static final String WRONG_ANSWER = "WRONG_ANSWER";

	// Use for save History
	public static final String SAVE_HISTORY_COMPLETED = "SAVE_HISTORY_COMPLETED";

	// Use for check level up
	public static final String LEVEL_RETAIN = "LEVEL_RETAIN";
	public static final String LEVEL_UP = "LEVEL_UP";

	// Use for create practice result
	public static final String PRACTICE_RESULT_EXIST = "PRACTICE_RESULT_EXIST";

	// Use for all function
	public static final String INVALID_INPUT = "INVALID_INPUT";
	public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
	public static final String COMPLETED = "COMPLETED";

	public List getUserAvailableGrades(String userId);
	public PhoneticPractice generatePractice(Member member, String gradeTitle);
	public String checkAnswer(PhoneticPractice practice, String answer);
	public String saveHistory(PhoneticPractice practice);
	public String checkLevelUp(Member member, PhoneticPractice practice, PracticeResult result);
	//public ChartValues getSummaryChartValues(List<PracticeResult> results);
	public PracticeResult getTotalResultByGrade(Member member, Grade grade);
	public PracticeResult getTotalResultByFrequentGrade(Member member);
	public String createPracticeResult(Member member);
	public void findIPAAndPronoun(PhoneticQuestion question);									// single thread find ipa and pronoun

	// use for memberWord
	public Map<PhoneticQuestion, Boolean> getUnSavedMap(PhoneticPractice practice);

	/**
	 * Get the model contain data of vocab practice for member summary page
	 */
	public VocabPracticeSummary getVocabPracticeSummary(Member member);

	/**
	 * Update score card
	 */
	public MemberPracticeScoreCard updateScoreCard(Member member, Date today, boolean isCorrect, PhoneticQuestion question);

}
