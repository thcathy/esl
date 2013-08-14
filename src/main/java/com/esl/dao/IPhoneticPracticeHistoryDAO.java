package com.esl.dao;

import com.esl.model.*;

public interface IPhoneticPracticeHistoryDAO extends IESLDao<PhoneticPracticeHistory> {
	public PhoneticPracticeHistory getPhoneticPracticeHistoryById(Long id);
	public void makePersistent(PhoneticPracticeHistory history);
	public void makeTransient(PhoneticPracticeHistory history);
	public Grade getMostFrequentGradeIDbyMember(Member member);
	public PhoneticPracticeHistory getLastPracticeHistoryByMember(Member member);
}
