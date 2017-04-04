package com.esl.dao;

import com.esl.model.Grade;
import com.esl.model.PhoneticQuestion;

import java.util.List;

public interface IPhoneticQuestionDAO extends IESLDao<PhoneticQuestion>  {
	public PhoneticQuestion getPhoneticQuestionByWord(String word);
	public List<PhoneticQuestion> getRandomQuestionsByGrade(Grade grade, int total, boolean isRandom);
	public List<PhoneticQuestion> getNotEnrichedQuestions();
	public void makePersistent(PhoneticQuestion question);
	public void makeTransient(PhoneticQuestion question);
}
