package com.esl.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.model.Grade;
import com.esl.model.PhoneticQuestion;
import com.esl.util.*;
import com.esl.util.practice.PhoneticQuestionUtil;
import com.esl.util.practice.PhoneticQuestionUtil.FindIPAAndPronoun;

@Transactional
@Repository("phoneticQuestionDAO")
public class PhoneticQuestionDAO extends ESLDao<PhoneticQuestion> implements IPhoneticQuestionDAO {
	private static final String GET_PHONETIC_QUESTION_BY_WORD = "from PhoneticQuestion phoneticQuestion where phoneticQuestion.word = :word";
	private static final String GET_QUESTIONS_BY_GRADE = "SELECT pq.phoneticquestion_id id FROM phonetic_question pq, grade_phoneticquestion gpq WHERE pq.phoneticquestion_id = gpq.phoneticquestion_id AND gpq.grade_id = :gradeId LIMIT 0, :total";

	public PhoneticQuestionDAO() {}

	public PhoneticQuestion getPhoneticQuestionByWord(String word) {
		List result = sessionFactory.getCurrentSession().createQuery(GET_PHONETIC_QUESTION_BY_WORD).setParameter("word", word).list();
		if (result.size() > 0)
			return (PhoneticQuestion) result.get(0);
		else {
			Logger.getLogger("ESL").info("Cannot find the PhoneticQuestion by word:" + word);
			return null;
		}
	}

	public void makePersistent(PhoneticQuestion question) {
		sessionFactory.getCurrentSession().saveOrUpdate(question);
	}

	public void makeTransient(PhoneticQuestion question) {
		sessionFactory.getCurrentSession().delete(question);
	}

	public List<PhoneticQuestion> getRandomQuestionsByGrade(Grade grade, int total, boolean isRandom) {
		List<PhoneticQuestion> questions = new ArrayList<PhoneticQuestion>();
		String queryString = "SELECT pq.phoneticquestion_id id FROM phonetic_question pq, grade_phoneticquestion gpq WHERE pq.phoneticquestion_id = gpq.phoneticquestion_id AND gpq.grade_id = :gradeId";
		if (isRandom) queryString += " ORDER BY RAND()";

		// Get Questions ID
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(queryString).addScalar("id",Hibernate.LONG);
		query.setParameter("gradeId", grade.getId());
		query.setMaxResults(total);
		Logger.getLogger("ESL").info("getRandomQuestionsByGrade: queryString[" + queryString + "]");

		List<Long> results = query.list();
		if (results.size() < 1) {
			Logger.getLogger("ESL").info("Cannot get any PhoneticQuestion by SQL:" + queryString);
			return null;
		}

		List<Thread> threads = new ArrayList<Thread>();
		PhoneticQuestionUtil pqUtil = new PhoneticQuestionUtil();

		// Generate the questions List
		if (results.size() < total) total = results.size();
		for (int i=0; i < total; i++) {
			PhoneticQuestion question = new PhoneticQuestion();
			question = (PhoneticQuestion)sessionFactory.getCurrentSession().get(PhoneticQuestion.class, results.get(i));
			if (question != null) {
				FindIPAAndPronoun finder = pqUtil.new FindIPAAndPronoun(questions, question, null, null);
				Thread newThread = new Thread(finder);
				Logger.getLogger("ESL").info("Start a new thread for Find IPA and Pronoun with id:" + newThread.getId() + ", Word[" + question.getWord()  + "]");
				newThread.start();
				threads.add(newThread);
			} else {
				Logger.getLogger("ESL").info("Cannot find the PhoneticQuestion by id:" + results.get(i));
			}
		}
		Logger.getLogger("ESL").info("All thread for Find IPA and Pronoun STARTED");

		while (threads.size() > 0 && questions.size() < total) {
			Logger.getLogger("ESL").info("questions.size()=" + questions.size());
			try {
				synchronized (this) {
					for (int i = 0; i < threads.size(); i++) {
						if (!threads.get(i).isAlive())
							threads.remove(i);
					}
					Thread.sleep(250);	// 0.25 sec
				}
			} catch (InterruptedException ex) {
				Logger.getLogger("ESL").error(ex);
			}
		}

		return questions;
	}

	public static void main(String[] args)
	{
		HibernateTransactionManager sf = (HibernateTransactionManager) SpringUtil.getContext().getBean("transactionManager");
		sf.getSessionFactory().openStatelessSession();
		IPhoneticQuestionDAO dao = (PhoneticQuestionDAO) SpringUtil.getContext().getBean("phoneticQuestionDAO");

		try
		{
			Grade grade = new Grade("a",1);
			grade.setId(new Long(1));
			List<PhoneticQuestion> r = dao.getRandomQuestionsByGrade(grade, 10, true);
			for (int i =0 ; i< r.size(); i++) {
				System.out.println(r.get(i).getWord());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		sf.getSessionFactory().close();
	}
}
