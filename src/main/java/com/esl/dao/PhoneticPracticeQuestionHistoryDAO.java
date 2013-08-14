package com.esl.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.model.PhoneticPracticeQuestionHistory;

@Transactional
@Repository("phoneticPracticeQuestionHistoryDAO")
public class PhoneticPracticeQuestionHistoryDAO extends ESLDao<PhoneticPracticeQuestionHistory> implements IPhoneticPracticeQuestionHistoryDAO {
	public PhoneticPracticeQuestionHistory getPhoneticPracticeQuestionHistoryById(Long id) {
		return (PhoneticPracticeQuestionHistory) sessionFactory.getCurrentSession().get(PhoneticPracticeQuestionHistory.class, id);
	}

	public void makePersistent(PhoneticPracticeQuestionHistory history) {
		sessionFactory.getCurrentSession().saveOrUpdate(history);
	}

	public void makeTransient(PhoneticPracticeQuestionHistory history) {
		sessionFactory.getCurrentSession().delete(history);
	}
}
