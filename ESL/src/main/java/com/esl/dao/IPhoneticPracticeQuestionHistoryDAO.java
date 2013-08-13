package com.esl.dao;

import com.esl.model.PhoneticPracticeQuestionHistory;

public interface IPhoneticPracticeQuestionHistoryDAO extends IESLDao<PhoneticPracticeQuestionHistory>  {
	public PhoneticPracticeQuestionHistory getPhoneticPracticeQuestionHistoryById(Long id);
	public void makePersistent(PhoneticPracticeQuestionHistory history);
	public void makeTransient(PhoneticPracticeQuestionHistory history);
}
