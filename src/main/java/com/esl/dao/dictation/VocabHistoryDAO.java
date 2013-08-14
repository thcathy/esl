package com.esl.dao.dictation;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.ESLDao;
import com.esl.entity.dictation.Vocab;
import com.esl.entity.dictation.VocabHistory;
import com.esl.exception.IllegalParameterException;

@Transactional
@Repository("vocabHistoryDAO")
public class VocabHistoryDAO extends ESLDao<VocabHistory> implements IVocabHistoryDAO {
	private static Logger logger = Logger.getLogger("ESL");


	public int removeByVocabs(Collection<Vocab> vocabs) {
		final String logPrefix = "removeByVocabs: ";
		logger.info(logPrefix + "START");
		if (vocabs == null) throw new IllegalParameterException(new String[]{"vocabs"}, new Object[]{vocabs});

		logger.info(logPrefix + "input vocab size[" + vocabs.size() + "]");
		if (vocabs.size() < 1) return 0;
		String queryStr = "DELETE FROM VocabHistory AS h WHERE h.vocab in (:vocabs)";
		Query query = sessionFactory.getCurrentSession().createQuery(queryStr).setParameterList("vocabs", vocabs);
		int result = query.executeUpdate();
		logger.info(logPrefix + "deleted history size[" + result + "]");
		return result;
	}
}
