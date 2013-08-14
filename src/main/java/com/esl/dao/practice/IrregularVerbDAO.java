package com.esl.dao.practice;

import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.ESLDao;
import com.esl.entity.practice.qa.IrregularVerb;

@Repository("irregularVerbDAO")
public class IrregularVerbDAO extends ESLDao<IrregularVerb> implements IIrregularVerbDAO {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true)
	public List<IrregularVerb> getRandomVerbs(int count) {
		final String logPrefix = "getRandomVerbs: ";
		logger.info("{}START: input count [{}]", logPrefix, count);

		// construct query string
		String queryStr = "SELECT v FROM IrregularVerb v ORDER BY RAND()";
		Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
		query.setMaxResults(count);
		return query.list();
	}
}
