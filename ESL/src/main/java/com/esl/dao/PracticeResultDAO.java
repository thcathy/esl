package com.esl.dao;

import java.util.List;
import java.util.TreeSet;

import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.exception.IllegalParameterException;
import com.esl.model.*;
import com.esl.model.TopResult.OrderType;
import com.esl.model.practice.PhoneticSymbols.Level;

@Transactional
@Repository("practiceResultDAO")
public class PracticeResultDAO extends ESLDao<PracticeResult> implements IPracticeResultDAO {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	public static int TOP_RESULT_QUANTITY = 10;
	public static int MIN_FULL_MARK = 20;

	@Value("${PracticeResultDAO.TopResultQuantity}") public void setTopResultQuantity(int topResultQuantity) {TOP_RESULT_QUANTITY = topResultQuantity;}
	@Value("${PracticeResultDAO.MinFullMark}")  public void setMinFullMark(int minFullMark) { MIN_FULL_MARK = minFullMark;}

	public PracticeResultDAO() {}

	public PracticeResult getPracticeResult(Member member, Grade grade, String practiceType) {
		return getPracticeResult(member, grade, practiceType, null);
	}

	public PracticeResult getPracticeResult(Member member, Grade grade, String practiceType, Level level) {
		logger.info("getPracticeResult: START");
		String queryString = "FROM PracticeResult pr WHERE pr.member.id = :memberId AND pr.practiceType = :practiceType";

		if (grade != null) queryString += " AND pr.grade.id = :gradeId";
		else queryString += " AND pr.grade is null";

		if (level != null) queryString += " AND pr.level = :level";
		else queryString += " AND pr.level is null";
		logger.info("query string [" + queryString + "]");

		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("memberId", member.getId()).setParameter("practiceType", practiceType);
		if (grade != null) query.setParameter("gradeId", grade.getId());
		if (level != null) query.setParameter("level", level.toString());

		PracticeResult r = null;
		r = (PracticeResult)query.uniqueResult();
		return r;
	}

	@Override
	public PracticeResult getRandomPracticeResult(Member member, Grade grade, String practiceType) {
		final String logPrefix = "getRandomPracticeResult: ";
		logger.info("{}START", logPrefix);

		StringBuilder querySb = new StringBuilder("FROM PracticeResult pr WHERE pr.member.id = :memberId AND pr.practiceType = :practiceType");
		if (grade != null) querySb.append(" AND pr.grade.id = :gradeId");
		else querySb.append(" AND pr.grade is null");
		querySb.append(" ORDER BY RAND()");
		logger.info("{}query string [{}]", logPrefix, querySb.toString());

		Query query = sessionFactory.getCurrentSession().createQuery(querySb.toString());
		query.setParameter("memberId", member.getId()).setParameter("practiceType", practiceType);
		if (grade != null) query.setParameter("gradeId", grade.getId());

		List<PracticeResult> results = query.list();
		if (results != null && results.size()>0)
			return results.get(0);
		else
			return null;
	}

	public List<PracticeResult> getGradedPracticeResults(Member member, String practiceType) {
		return getGradedPracticeResults(member, practiceType, null);
	}

	public List<PracticeResult> getGradedPracticeResults(Member member, String practiceType, Level level) {
		String queryString = "SELECT pr FROM PracticeResult pr INNER JOIN pr.grade as g WHERE pr.member.id = :memberId AND pr.practiceType = :practiceType AND pr.grade IS NOT NULL";
		if (level != null) queryString += " AND pr.level = :level";
		else queryString += " AND pr.level IS NULL";
		queryString += " ORDER BY g.level";

		Query query = sessionFactory.getCurrentSession().createQuery(queryString).setParameter("memberId", member.getId()).setParameter("practiceType", practiceType);
		if (level != null) query.setParameter("level", level.toString());
		return query.list();
	}

	public List<PracticeResult> getPracticeResults(Member member, String practiceType) {
		String queryString = "SELECT pr FROM PracticeResult pr INNER JOIN pr.grade as g WHERE pr.member = :member AND pr.practiceType = :practiceType AND pr.grade IS NOT NULL ORDER BY g.level";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString).setEntity("member", member).setParameter("practiceType", practiceType);
		return query.list();
	}

	public PracticeResult getBestPracticeResultByMember(Member member, String practiceType) {
		if (member == null) return null;

		String queryString = "FROM PracticeResult r WHERE r.member = :member AND r.grade IS NOT NULL AND r.practiceType = :practiceType ORDER BY r.rate DESC, r.mark DESC";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString).setEntity("member", member).setParameter("practiceType", practiceType);
		List results = query.setMaxResults(1).list();
		if (results.size() > 0)
			return (PracticeResult) results.get(0);
		else {
			logger.info("getBestPracticeResultByMember:Cannot find PracticeResult of member:" + member + " practiceType:" + practiceType);
			return null;
		}
	}

	public PracticeResult getPracticeResultById(Long id) {
		return (PracticeResult) sessionFactory.getCurrentSession().get(PracticeResult.class, id);
	}

	public void makePersistent(PracticeResult result) {
		super.persist(result);
	}

	public void makeTransient(PracticeResult result) {
		super.delete(result);
	}

	// Find out the result with the grade that the member mostly practice
	public PracticeResult getFavourPracticeResultByMember(Member member, String practiceType) {
		if (member == null) return null;

		String queryString = "FROM PracticeResult r WHERE r.member = :member1 AND r.grade is not null AND r.practiceType=:practiceType  AND r.totalPractices = (SELECT MAX(b.totalPractices) FROM PracticeResult b WHERE b.member = :member2 AND b.practiceType = :practiceType2 AND b.grade is not null)";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString).setEntity("member1", member).setEntity("member2", member).setParameter("practiceType", practiceType).setParameter("practiceType2", practiceType);

		List results = query.setMaxResults(1).list();
		if (results.size() > 0)
			return (PracticeResult) results.get(0);
		else {
			logger.info("getFavourPracticeResultByMember:Cannot find PracticeResult of member:" + member + " practiceType:" + practiceType);
			return null;
		}
	}

	// Get the top result by mark only, input grade == null if search for all grade
	public List<PracticeResult> getTopScores(Grade grade, String practiceType) {
		return getTopScores(grade, practiceType, null);
	}

	// Get the top result by mark only, input grade == null if search for all grade
	public List<PracticeResult> getTopScores(Grade grade, String practiceType, Level level) {
		// Construct query string
		String queryString = "FROM PracticeResult pr WHERE pr.practiceType = :practiceType";
		if (grade != null) queryString += " AND pr.grade.id = :gradeId ";
		else queryString +=" AND pr.grade IS NULL ";
		if (level != null) queryString += " AND pr.level = :level ";
		else queryString +=" AND pr.level IS NULL ";
		queryString += " ORDER BY pr.mark DESC, pr.rate DESC, pr.createdDate ASC";

		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("practiceType", practiceType).setMaxResults(TOP_RESULT_QUANTITY);
		if (grade != null) query.setParameter("gradeId", grade.getId());
		if (level != null) query.setParameter("level", level.toString());
		return query.list();
	}

	// Get the top result by rating (require reach min fullMark), input grade == null for all grade
	public List<PracticeResult> getTopRatings(Grade grade, String practiceType) {
		return getTopRatings(grade, practiceType, null);
	}

	public List<PracticeResult> getTopRatings(Grade grade, String practiceType, Level level) {
		// Construct query string
		String queryString = "FROM PracticeResult pr WHERE pr.practiceType = :practiceType AND pr.fullMark >= :fullMark";
		if (grade != null) queryString += " AND pr.grade.id = :gradeId ";
		else queryString +=" AND pr.grade IS NULL ";
		if (level != null) queryString += " AND pr.level = :level ";
		else queryString +=" AND pr.level IS NULL ";
		queryString += " ORDER BY pr.rate DESC, pr.mark DESC, pr.createdDate ASC";

		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("practiceType", practiceType);
		query.setParameter("fullMark", MIN_FULL_MARK);
		query.setMaxResults(TOP_RESULT_QUANTITY);
		if (grade != null) query.setParameter("gradeId", grade.getId());
		if (level != null) query.setParameter("level", level.toString());
		return query.list();
	}

	public List<Grade> getAvailableGrades(String practiceType) {
		return getAvailableGrades(practiceType, null);
	}

	/**
	 * Return all distinct grades of that practice type and level
	 */
	public List<Grade> getAvailableGrades(String practiceType, Level level) {
		String queryString = "SELECT DISTINCT pr.grade FROM PracticeResult pr WHERE pr.practiceType = :practiceType";
		if (level != null) queryString += " AND pr.level = :level";
		else queryString += " AND pr.level IS NULL";

		Query query = sessionFactory.getCurrentSession().createQuery(queryString).setParameter("practiceType", practiceType);
		if (level != null) query.setParameter("level", level.toString());
		return query.list();
	}

	/**
	 *  Hibernate init all linkage
	 * @param practiceResult
	 */
	public void initLinkage(PracticeResult practiceResult) {
		Session session = sessionFactory.getCurrentSession();
		if (!session.contains(practiceResult)) {
			session.lock(practiceResult, LockMode.NONE);
		}
		Hibernate.initialize(practiceResult.getGrade());
		Hibernate.initialize(practiceResult.getMember());
	}

	/**
	 * Get a sorted results set by practice Type and order field with grade or not
	 */
	public TreeSet<PracticeResult> getAllResults(String practiceType, OrderType orderType, boolean withGrade) {
		// Construct query string
		String queryString = "FROM PracticeResult pr WHERE pr.practiceType = :practiceType ";
		if (withGrade)
			queryString += " AND pr.grade is not null ";
		else
			queryString += " AND pr.grade is null ";

		switch (orderType) {
		case Score:
			queryString += " ORDER BY pr.mark DESC "; break;
		case Rate:
			queryString += " ORDER BY pr.rate DESC "; break;
		}
		logger.info("getAllResults: queryString:" + queryString);
		logger.info("getAllResults: practiceType:" + practiceType);
		logger.info("getAllResults: orderType:" + orderType);

		// query result
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("practiceType", practiceType);
		TreeSet<PracticeResult> set = null;

		switch (orderType) {
		case Score:
			set = new TreeSet<PracticeResult>(new PracticeResult.TopScoreComparator()); break;
		case Rate:
			set = new TreeSet<PracticeResult>(new PracticeResult.TopRateComparator()); break;
		}

		set.addAll(query.list());
		logger.info("getAllResults: returned set size:" + set.size());

		return set;
	}

	/**
	 * Get results which is lower then the inputed result
	 */
	public List<PracticeResult> getResultLower(OrderType orderType, PracticeResult result) {
		logger.info("getResultLower: START");

		// input check
		if (result == null) {
			logger.info("getResultLower: input practice result is null!");
			return null;
		}

		// Construct query String
		String queryString = "FROM PracticeResult pr WHERE pr.member != :member AND pr.practiceType = :practiceType ";
		if (result.getGrade() == null) queryString += " AND pr.grade IS NULL ";
		else queryString += " AND pr.grade = :grade ";
		if (result.getLevel() == null || "".equals(result.getLevel())) queryString += " AND pr.level IS NULL";
		else queryString += " AND pr.level = :level ";

		switch (orderType) {
		case Score:
		{
			queryString += " AND (pr.mark < :mark OR (pr.mark = :mark AND pr.rate < :rate) OR (pr.mark = :mark AND pr.rate = :rate AND pr.createdDate >= :createdDate))" +
			" ORDER BY pr.mark DESC, pr.rate DESC, pr.createdDate ASC";
			break;
		}
		case Rate:
		{
			queryString += " AND (pr.rate < :rate OR (pr.rate = :rate AND pr.mark < :mark) OR (pr.rate = :rate AND pr.mark = :mark AND pr.createdDate >= :createdDate)) AND pr.fullMark >= :fullMark" +
			" ORDER BY pr.rate DESC, pr.mark DESC, pr.createdDate ASC";
			break;
		}
		}
		logger.info("getResultLower: queryString[" + queryString + "]");

		// query result
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("member", result.getMember());
		query.setParameter("practiceType", result.getPracticeType());
		if (result.getGrade() != null) query.setParameter("grade", result.getGrade());
		if (result.getLevel() != null && !"".equals(result.getLevel())) query.setParameter("level", result.getLevel());
		query.setParameter("mark", result.getMark());
		query.setParameter("rate", result.getRate());
		query.setParameter("createdDate", result.getCreatedDate());
		if (orderType.equals(TopResult.OrderType.Rate))	query.setParameter("fullMark", MIN_FULL_MARK);
		query.setMaxResults(TOP_RESULT_QUANTITY - 1);

		List<PracticeResult> list = query.list();
		if (list != null) {
			logger.info("getResultLower: return list size: " + list.size());
		}
		return list;
	}

	/**
	 * Get results which is higher then the inputed result
	 */
	public List<PracticeResult> getResultHigher(OrderType orderType, PracticeResult result) {
		logger.info("getResultHigher: START");

		// input check
		if (result == null) {
			logger.info("getResultHigher: input practice result is null!");
			return null;
		}

		// Construct query String
		String queryString = "FROM PracticeResult pr WHERE pr.member != :member AND pr.practiceType = :practiceType ";
		if (result.getGrade() == null) queryString += " AND pr.grade IS NULL ";
		else queryString += " AND pr.grade = :grade ";
		if (result.getLevel() == null || "".equals(result.getLevel())) queryString += " AND pr.level IS NULL";
		else queryString += " AND pr.level = :level ";

		switch (orderType) {
		case Score:
		{
			queryString += " AND (pr.mark > :mark OR (pr.mark = :mark AND pr.rate > :rate) OR (pr.mark = :mark AND pr.rate = :rate AND pr.createdDate < :createdDate))" +
			" ORDER BY pr.mark ASC, pr.rate ASC, pr.createdDate DESC";
			break;
		}
		case Rate:
		{
			queryString += " AND (pr.rate > :rate OR (pr.rate = :rate AND pr.mark > :mark) OR (pr.rate = :rate AND pr.mark = :mark AND pr.createdDate < :createdDate)) AND pr.fullMark >= :fullMark " +
			"ORDER BY pr.rate ASC, pr.mark ASC, pr.createdDate DESC";
			break;
		}
		}
		logger.info("getResultHigher: queryString[" + queryString + "]");

		// query result
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("member", result.getMember());
		query.setParameter("practiceType", result.getPracticeType());
		if (result.getGrade() != null) query.setParameter("grade", result.getGrade());
		if (result.getLevel() != null && !"".equals(result.getLevel())) query.setParameter("level", result.getLevel());
		query.setParameter("mark", result.getMark());
		query.setParameter("rate", result.getRate());
		query.setParameter("createdDate", result.getCreatedDate());
		if (orderType.equals(TopResult.OrderType.Rate))	query.setParameter("fullMark", MIN_FULL_MARK);
		query.setMaxResults(TOP_RESULT_QUANTITY - 1);

		List<PracticeResult> list = query.list();
		if (list != null) {
			logger.info("getResultHigher: return list size: " + list.size());
		}
		return list;
	}

	/**
	 * Return the position of the input result in DB
	 */
	public int getPosition(OrderType orderType, PracticeResult pr) {
		logger.info("getPosition: START");

		// input check
		if (pr == null) {
			logger.warn("getPosition: input practice result is null, return -1");
			return -1;
		}
		logger.info("getPosition: input practice result[" + pr + "]");

		// contrust query string
		String queryStr = "SELECT COUNT(*) as counter FROM practice_result pr WHERE pr.practice_result_id != :practice_result_id AND pr.practice_type = :practice_type";
		if (pr.getGrade() == null) {
			queryStr += " AND pr.grade_id IS NULL ";
		} else {
			queryStr += " AND pr.grade_id = :grade_id ";
		}
		if (pr.getLevel() != null && !"".equals(pr.getLevel()))
			queryStr += " AND pr.level = :level ";
		else
			queryStr += " AND pr.level IS NULL ";
		switch (orderType) {
		case Score:
			queryStr += " AND (pr.mark > :mark OR (pr.mark = :mark AND pr.rate > :rate) OR (pr.mark = :mark AND pr.rate = :rate AND pr.created_date < :created_date))"; break;
		case Rate:
			queryStr += " AND pr.full_mark >= :full_mark AND (pr.rate > :rate OR (pr.rate = :rate AND pr.mark > :mark) OR (pr.mark = :mark AND pr.rate = :rate AND pr.created_date < :created_date))"; break;
		}
		logger.info("getPosition: queryString[" + queryStr + "]");

		// Call to DB
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(queryStr).addScalar("counter",Hibernate.INTEGER);
		query.setParameter("practice_result_id", pr.getId());
		query.setParameter("practice_type", pr.getPracticeType());
		query.setParameter("mark", pr.getMark());
		query.setParameter("rate", pr.getRate());
		query.setParameter("created_date", pr.getCreatedDate());
		if (pr.getGrade() != null) query.setParameter("grade_id", pr.getGrade().getId());
		if (pr.getLevel() != null && !"".equals(pr.getLevel())) query.setParameter("level", pr.getLevel());
		if (orderType.equals(TopResult.OrderType.Rate))	query.setParameter("full_mark", MIN_FULL_MARK);

		List results = query.list();
		if (results.size() < 1) {
			logger.warn("getPosition : no counter retrieved");
			return -1;
		}

		Integer counter = (Integer) results.get(0);
		counter++;	// add 1 for the input result
		logger.info("getPosition: return counter:" + counter);
		return counter.intValue();
	}

	public PracticeResult getHighestGradingByMember(Member member, String practiceType) {
		final String logPrefix = "getHighestGradingByMember: ";
		logger.info(logPrefix + "START");
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});

		final String queryStr = "SELECT pr FROM PracticeResult pr LEFT JOIN pr.grade g WHERE pr.grade IS NOT NULL AND pr.member = :member AND pr.practiceType = :practiceType ORDER BY g.level DESC";
		Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
		query.setParameter("member", member);
		query.setParameter("practiceType", practiceType);
		query.setMaxResults(1);
		return (PracticeResult) query.uniqueResult();
	}
}
