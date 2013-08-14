package com.esl.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.model.*;

@Transactional
@Repository("phoneticPracticeHistoryDAO")
public class PhoneticPracticeHistoryDAO extends ESLDao<PhoneticPracticeHistory> implements IPhoneticPracticeHistoryDAO {
	private static final String GET_MOST_FREQUENT_GRADE = "SELECT grade_id, COUNT(grade_id) AS x FROM phonetic_practice_history WHERE member_id = :memberId GROUP BY grade_id ORDER BY x DESC";

	public PhoneticPracticeHistory getPhoneticPracticeHistoryById(Long id) {
		return (PhoneticPracticeHistory) sessionFactory.getCurrentSession().get(PhoneticPracticeHistory.class, id);
	}

	public void makePersistent(PhoneticPracticeHistory history) {
		sessionFactory.getCurrentSession().saveOrUpdate(history);
	}

	public void makeTransient(PhoneticPracticeHistory history) {
		sessionFactory.getCurrentSession().delete(history);
	}

	public Grade getMostFrequentGradeIDbyMember(Member member) {
		if (member == null) return null;

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(GET_MOST_FREQUENT_GRADE).addScalar("grade_id",Hibernate.LONG).addScalar("x",Hibernate.INTEGER);
		query.setParameter("memberId", member.getId());
		List results = query.list();
		if (results.size() < 1) {
			Logger.getLogger("ESL").info("getMostFrequentGradeIDbyMember:Do not find any grade of in phonetic_practice_history of member:" + member);
			return null;
		}

		Object objects[] = (Object[]) results.get(0);
		return (Grade) sessionFactory.getCurrentSession().get(Grade.class, (Long)objects[0]);
	}

	public PhoneticPracticeHistory getLastPracticeHistoryByMember(Member member) {
		if (member == null) return null;

		Session session = sessionFactory.getCurrentSession();
		String queryString = "FROM PhoneticPracticeHistory h WHERE h.member = :member ORDER BY h.completedTime DESC";
		Query query = session.createQuery(queryString).setEntity("member", member);
		return (PhoneticPracticeHistory) query.setMaxResults(1).list().get(0);
	}

}
