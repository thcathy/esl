package com.esl.dao.group;

import java.util.Collections;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.PracticeResultDAO;
import com.esl.exception.DBException;
import com.esl.exception.IllegalParameterException;
import com.esl.model.*;
import com.esl.model.group.MemberGroup;
import com.esl.model.group.MemberGroupPracticeResult;

@Transactional
@Repository("groupPracticeResultDAO")
public class GroupPracticeResultDAO extends PracticeResultDAO implements IGroupPracticeResultDAO {
	private static Logger logger = LoggerFactory.getLogger(GroupPracticeResultDAO.class);

	public GroupPracticeResultDAO() {}

	/**
	 * Return all practice result of that group by grade
	 */
	public List<PracticeResult> listResultsByGroup(MemberGroup group, Grade grade, String practiceType) throws IllegalParameterException {
		if (group == null) throw new IllegalParameterException(new String[]{"group"}, new Object[]{group});

		String queryStr = "SELECT pr FROM PracticeResult pr LEFT JOIN pr.member.groups AS g WHERE g = :group AND pr.practiceType = :practiceType";

		if (grade == null) queryStr += " AND pr.grade IS NULL";
		else queryStr += " AND pr.grade = :grade";
		logger.info("listResultsByGroup: queryStr[" + queryStr + "]");

		Session s = sessionFactory.getCurrentSession();
		Query query = s.createQuery(queryStr).setParameter("group", group);
		query.setParameter("practiceType", practiceType);
		if (grade != null) query.setParameter("grade", grade);
		return query.list();
	}

	public MemberGroupPracticeResult getGroupResult(MemberGroup group, Grade grade, String practiceType) throws IllegalParameterException {
		if (group == null || practiceType == null) throw new IllegalParameterException(new String[]{"group", "practiceType"}, new Object[]{group, practiceType});

		String queryStr = "FROM MemberGroupPracticeResult pr WHERE pr.group = :group AND pr.practiceType = :practiceType";

		if (grade == null) queryStr += " AND pr.grade IS NULL";
		else queryStr += " AND pr.grade = :grade";
		logger.info("getGroupResultByGroup: queryStr[" + queryStr + "]");

		Session s = sessionFactory.getCurrentSession();
		Query query = s.createQuery(queryStr).setParameter("group", group);
		query.setParameter("practiceType", practiceType);
		if (grade != null) query.setParameter("grade", grade);

		List<MemberGroupPracticeResult> results = query.list();
		if (results == null || results.size() < 1) {
			logger.info("getGroupResult: No result found");
			return null;
		}
		return results.get(0);
	}

	public int getPosition(MemberGroup group, TopResult.OrderType orderType, PracticeResult pr) throws IllegalParameterException, DBException {
		// input check
		if (pr == null || group == null) throw new IllegalParameterException(new String[]{"group","practiceResult"}, new Object[]{group,pr});

		logger.info("getPosition: input practice result[" + pr + "]");

		// contrust query string
		String queryStr = "SELECT COUNT(*) as counter FROM PracticeResult pr LEFT JOIN pr.member.groups AS g WHERE g = :group AND pr.id != :id AND pr.practiceType = :practiceType";

		if (pr.getGrade() == null) queryStr += " AND pr.grade IS NULL ";
		else queryStr += " AND pr.grade = :grade";

		switch (orderType) {
		case Score:
			queryStr += " AND (pr.mark > :mark OR (pr.mark = :mark AND pr.rate > :rate) OR (pr.mark = :mark AND pr.rate = :rate AND pr.createdDate < :createdDate))"; break;
		case Rate:
			queryStr += " AND (pr.rate > :rate OR (pr.rate = :rate AND pr.mark > :mark) OR (pr.mark = :mark AND pr.rate = :rate AND pr.createdDate < :createdDate))"; break;
		}
		logger.info("getPosition: queryString[" + queryStr + "]");

		// Call to DB
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryStr);
		query.setParameter("id", pr.getId());
		query.setParameter("practiceType", pr.getPracticeType());
		query.setParameter("group", group);
		query.setParameter("mark", pr.getMark());
		query.setParameter("rate", pr.getRate());
		query.setParameter("createdDate", pr.getCreatedDate());
		if (pr.getGrade() != null) query.setParameter("grade", pr.getGrade());

		List results = query.list();
		if (results.size() < 1) throw new DBException("no counter retrieved");

		Long counter = (Long) results.get(0);
		counter++;	// add 1 for the input result
		logger.info("getPosition: return counter:" + counter);
		return counter.intValue();
	}

	public void removeAllGroupResult() {
		logger.info("removeAllGroupResult: START");
		String queryStr = "DELETE FROM MemberGroupPracticeResult";
		Session session = sessionFactory.getCurrentSession();
		int count = session.createQuery(queryStr).executeUpdate();
		logger.info("removeAllGroupResult: total " + count + " rows removed");
	}

	public void importGroupResult() {
		logger.info("importGroupResult: START");

		String queryStr = "INSERT INTO member_group_practice_result (group_id, practice_type, grade_id, mark, full_mark, created_date) " +
				" SELECT m.id, pr.practice_type, pr.grade_id, sum(pr.mark), sum(pr.full_mark), now() " +
				" FROM member_group m, member_group_member mm, practice_result pr " +
				" WHERE m.id = mm.member_group_id AND mm.member_id = pr.member_id " +
				" GROUP BY m.id, pr.practice_type, pr.grade_id";

		Session session = sessionFactory.getCurrentSession();
		int count = session.createSQLQuery(queryStr).executeUpdate();
		logger.info("importGroupResult: total " + count + " rows added to member_group_practice_result");
	}

	public int getRank(MemberGroupPracticeResult result) throws IllegalParameterException {
		// input check
		if (result == null) throw new IllegalParameterException(new String[]{"practiceResult"}, new Object[]{result});

		attachSession(result);

		// construct query string
		String queryStr = "SELECT COUNT(*) as counter FROM member_group_practice_result pr WHERE pr.group_id != :group_id AND pr.practice_type = :practice_type";
		if (result.getGrade() == null) {
			queryStr += " AND pr.grade_id IS NULL ";
		} else {
			queryStr += " AND pr.grade_id = :grade_id ";
		}
		queryStr += " AND pr.mark > :mark";
		logger.info("getRank: queryString[" + queryStr + "]");

		// Call to DB
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(queryStr).addScalar("counter",Hibernate.INTEGER);
		query.setParameter("group_id", result.getGroup().getId());
		query.setParameter("practice_type", result.getPracticeType());
		query.setParameter("mark", result.getMark());

		if (result.getGrade() != null) query.setParameter("grade_id", result.getGrade().getId());

		List results = query.list();
		if (results.size() < 1) {
			logger.warn("getRank : no counter retrieved");
			return -1;
		}

		Integer counter = (Integer) results.get(0);
		counter++;	// add 1 for the input result
		logger.info("getRank: return counter[" + counter + "]");
		return counter.intValue();
	}

	/**
	 * Get results which is lower then the inputed result
	 */
	public List<MemberGroupPracticeResult> listResultsLower(MemberGroupPracticeResult result) {
		logger.info("listResultsLower: START");

		// input check
		if (result == null) throw new IllegalParameterException(new String[]{"memberGroupPracticeResult"}, new Object[]{result});

		// Construct query String
		String queryString = "FROM MemberGroupPracticeResult pr WHERE pr.group != :group AND pr.practiceType = :practiceType AND pr.mark <= :mark ";
		if (result.getGrade() == null) {
			queryString += " AND pr.grade IS NULL ";
		} else {
			queryString += " AND pr.grade = :grade ";
		}
		queryString += " ORDER BY pr.mark DESC";
		logger.info("listResultsLower: queryString[" + queryString + "]");

		// query result
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("group", result.getGroup());
		query.setParameter("practiceType", result.getPracticeType());
		if (result.getGrade() != null) query.setParameter("grade", result.getGrade());
		query.setParameter("mark", result.getMark());
		query.setMaxResults(TOP_RESULT_QUANTITY - 1);

		List<MemberGroupPracticeResult> list = query.list();
		if (list != null) {	logger.info("listResultsLower: return list size: " + list.size());}

		for (Object o : list) {
			logger.info("listResultsLower: object[" + o + "]");
		}

		return list;
	}

	/**
	 * Get results which is higher then the inputed result
	 */
	public List<MemberGroupPracticeResult> listResultsHigher(MemberGroupPracticeResult result) {
		logger.info("listResultsHigher: START");

		// input check
		if (result == null) throw new IllegalParameterException(new String[]{"memberGroupPracticeResult"}, new Object[]{result});

		// Construct query String
		String queryString = "FROM MemberGroupPracticeResult pr WHERE pr.group != :group AND pr.practiceType = :practiceType AND pr.mark > :mark ";
		if (result.getGrade() == null) {
			queryString += " AND pr.grade IS NULL ";
		} else {
			queryString += " AND pr.grade = :grade ";
		}
		queryString += " ORDER BY pr.mark";
		logger.info("listResultsHigher: queryString[" + queryString + "]");

		// query result
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(queryString);
		query.setParameter("group", result.getGroup());
		query.setParameter("practiceType", result.getPracticeType());
		if (result.getGrade() != null) query.setParameter("grade", result.getGrade());
		query.setParameter("mark", result.getMark());
		query.setMaxResults(TOP_RESULT_QUANTITY - 1);

		List<MemberGroupPracticeResult> list = query.list();
		if (list != null) {	logger.info("listResultsHigher: return list size: " + list.size());}
		Collections.reverse(list);
		for (Object o : list) {
			logger.info("listResultsHigher: object[" + o + "]");
		}

		return list;
	}

	public void makePersistent(MemberGroupPracticeResult result) {
		sessionFactory.getCurrentSession().saveOrUpdate(result);
	}

	public void makeTransient(MemberGroupPracticeResult result) {
		sessionFactory.getCurrentSession().delete(result);
	}
}
