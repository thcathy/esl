package com.esl.dao;

import java.util.List;

import com.esl.model.Grade;
import com.esl.model.PhoneticQuestion;

public interface IPhoneticQuestionDAO extends IESLDao<PhoneticQuestion>  {
	public PhoneticQuestion getPhoneticQuestionByWord(String word);
	public List<PhoneticQuestion> getRandomQuestionsByGrade(Grade grade, int total, boolean isRandom);
	public void makePersistent(PhoneticQuestion question);
	public void makeTransient(PhoneticQuestion question);
}
