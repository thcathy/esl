package com.esl.dao.practice;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.ESLDao;
import com.esl.entity.practice.PracticeMedal;
import com.esl.enumeration.ESLPracticeType;
import com.esl.exception.IllegalParameterException;
import com.esl.util.DateUtil;

@Transactional
@Repository("practiceMedalDAO")
public class PracticeMedalDAO extends ESLDao<PracticeMedal> implements IPracticeMedalDAO {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(PracticeMedalDAO.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<PracticeMedal> listPracticeMedals(ESLPracticeType type, Date date) {
		final String logPrefix = "listPracticeMedals:";
		logger.debug("{} START: type [{}], date [{}]", new Object[] {logPrefix, type, date});
		if (type==null || date==null) throw new IllegalParameterException(new String[]{"type, date"}, new Object[]{type, date});

		date = DateUtil.toFirstDayOfMonth(date);
		String queryStr = "FROM PracticeMedal AS m WHERE m.practiceType = :practiceType AND m.awardedDate = :awardedDate ORDER BY m.createdDate";
		Query query = sessionFactory.getCurrentSession().createQuery(queryStr).setParameter("practiceType", type).setParameter("awardedDate", date);
		List<PracticeMedal> result = query.list();
		Collections.sort(result, new PracticeMedal.TopMedalComparator());
		return result;
	}

	@Override
	public PracticeMedal getRandomPracticeMedal() {
		final String logPrefix = "getRandomPracticeMedal:";
		logger.debug("{} START", logPrefix);
		String queryStr = "FROM PracticeMedal ORDER BY RAND()";
		Query query = sessionFactory.getCurrentSession().createQuery(queryStr).setMaxResults(1);

		return (PracticeMedal) query.uniqueResult();
	}



}
