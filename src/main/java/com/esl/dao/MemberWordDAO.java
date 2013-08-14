package com.esl.dao;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.exception.IllegalParameterException;
import com.esl.model.*;

@Transactional
@Repository("memberWordDAO")
public class MemberWordDAO extends ESLDao<MemberWord> implements IMemberWordDAO {
	private static Logger logger = Logger.getLogger("ESL");
	public static int LEARNT_CORRECT_REQUIRE = 5;

	@Value("${MemberWordDAO.LearntCorrectRequire}") public void setLearntCorrectRequire(int learntCorrectRequire) { LEARNT_CORRECT_REQUIRE = learntCorrectRequire;}

	public MemberWordDAO() {}

	public MemberWord getMemberWordById(Long id) {
		return (MemberWord) sessionFactory.getCurrentSession().get(MemberWord.class, id);
	}

	public MemberWord getWord(Member member, PhoneticQuestion word) throws IllegalParameterException {
		if (member == null || word == null) throw new IllegalParameterException(new String[]{"member", "word"}, new Object[]{member, word});

		logger.info("getWord: input member[" + member.getUserId() + "], word[" + word.getWord() + "]");

		String query = "from MemberWord w where w.member = :member and w.word = :word";
		Query q = sessionFactory.getCurrentSession().createQuery(query);
		List result = q.setParameter("member", member).setParameter("word", word).list();

		if (result.size() > 0 ) return (MemberWord) result.get(0);
		else {
			logger.info("Do not find Member-Word [" + member.getUserId() + "," + word.getWord() + "]");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<MemberWord> listLearntWords(Member member) throws IllegalParameterException {
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});
		logger.info("listLearntWords: input member[" + member.getUserId() + "]");

		String query = "from MemberWord w where w.member = :member and w.correctCount >= :count";
		return sessionFactory.getCurrentSession().createQuery(query).setParameter("member", member).setParameter("count", LEARNT_CORRECT_REQUIRE).list();
	}

	@SuppressWarnings("unchecked")
	public List<MemberWord> listWords(Member member) throws IllegalParameterException {
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});
		logger.info("listWords: input member[" + member.getUserId() + "]");

		String query = "from MemberWord w where w.member = :member";
		return sessionFactory.getCurrentSession().createQuery(query).setParameter("member", member).list();
	}

	@Override
	public void persist(MemberWord word) {
		sessionFactory.getCurrentSession().saveOrUpdate(word);
		logger.info("persist: memberWord[" + word + "] is saved");
	}

	public int totalWords(Member member) throws IllegalParameterException {
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});
		logger.info("totalWords: input member[" + member.getUserId() + "]");

		String queryStr = "SELECT COUNT(*) as counter FROM MemberWord w WHERE w.member = :member";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryStr);
		query.setParameter("member", member);

		List results = query.list();
		Long counter = (Long) results.get(0);
		logger.info("totalWords: return total:" + counter);
		return counter.intValue();
	}

	public void transit(MemberWord word) {
		sessionFactory.getCurrentSession().delete(word);
		logger.info("transit: memberWord[" + word + "] is deleted");
	}

	public List<MemberWord> listRandomWords(Member member, int total, Collection<MemberWord> excludeWords) throws IllegalParameterException {
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});

		logger.info("listRandomWords: input member[" + member.getUserId() + "], total[" + total + "]");

		String queryString = "from MemberWord w where w.member = :member #isExclude# order by RAND()";

		// set "not in" list in sql
		if (excludeWords == null || excludeWords.size() < 1) {
			logger.info("listRandomWords: input excludeWords is null or empty");
			queryString = queryString.replace("#isExclude#", "");
		} else {
			logger.info("listRandomWords: input excludeWords.size[" + excludeWords.size() + "]");
			queryString = queryString.replace("#isExclude#", "and w not in (:excludeWords)");
		}
		logger.info("listRandomWords: queryString[" + queryString + "]");

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("member", member);
		if (queryString.contains(":excludeWords")) query.setParameterList("excludeWords", excludeWords);
		query.setMaxResults(total);

		return query.list();
	}

	public int deleteWordsByCorrectCount(Member member, int count) throws IllegalParameterException {
		if (member == null) throw new IllegalParameterException(new String[]{"member", "count"}, new Object[]{member,count});
		logger.info("deleteWordsByCorrectCount: input member[" + member.getUserId() + "], count[" + count + "]");

		String queryString = "delete MemberWord w where w.member = :member and w.correctCount >= :count";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("member", member);
		query.setParameter("count", count);

		return query.executeUpdate();
	}

	public int deleteWordsByRate(Member member, double rate) throws IllegalParameterException {
		if (member == null) throw new IllegalParameterException(new String[]{"member", "rate"}, new Object[]{member,rate});
		logger.info("deleteWordsByRate: input member[" + member.getUserId() + "], rate[" + rate + "]");

		String queryString = "delete MemberWord w where w.member = :member and w.correctCount / w.trialCount >= :count";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("member", member);
		query.setParameter("rate", rate);

		return query.executeUpdate();
	}
}
