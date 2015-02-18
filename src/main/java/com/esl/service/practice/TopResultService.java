package com.esl.service.practice;

import java.util.*;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.*;
import com.esl.model.*;
import com.esl.model.TopResult.OrderType;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.util.SpringUtil;

@Service("topResultService")
@Transactional
public class TopResultService implements ITopResultService {
	private static Logger logger = LoggerFactory.getLogger(TopResultService.class);

	@Resource private IPracticeResultDAO practiceResultDAO = null;
	@Resource private IGradeDAO gradeDAO = null;


	// ============== Constructor ================//
	public TopResultService() {}

	// ============== Setter / Getter ================//
	public void setPracticeResultDAO(IPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;	}
	public void setGradeDAO(IGradeDAO gradeDAO) {this.gradeDAO = gradeDAO; }

	//============== Functions ================//
	public TopResult getRandomTopResults()
	{
		logger.info("getRandomTopResults: START");
		Random r = new Random();
		List<PracticeResult> results = null;
		Grade grade = null;
		Level level = null;
		String practiceType = "";
		OrderType orderType;

		do {
			level = null;
			grade = null;

			int randomType = r.nextInt(OrderType.values().length);
			orderType = OrderType.values()[randomType];
			logger.info("getRandomTopResults: Randomed orderType:" + orderType);

			// Random practice Type
			switch (r.nextInt(2)) {
			case 0:
				practiceType = PracticeResult.PHONETICPRACTICE; break;
			case 1:
				practiceType = PracticeResult.PHONETICSYMBOLPRACTICE; break;
			}
			logger.info("getRandomTopResults: Randomed practiceType:" + practiceType);

			// Random a level for  PracticeResult.PHONETICSYMBOLPRACTICE
			if (PracticeResult.PHONETICSYMBOLPRACTICE.equals(practiceType))
				level = Level.values()[r.nextInt(Level.values().length)];
			logger.info("getRandomTopResults: Randomed level[" + level + "]");

			// Random a available grade
			List<Grade> grades = practiceResultDAO.getAvailableGrades(practiceType, level);
			int randomGrade = r.nextInt(grades.size() + 1);
			if (randomGrade < grades.size())
				grade = grades.get(randomGrade);
			logger.info("getRandomTopResults: Randomed grade:" + grade);

			switch (orderType) {
			case Rate:
				results = practiceResultDAO.getTopRatings(grade, practiceType, level); break;
			case Score:
				results = practiceResultDAO.getTopScores(grade, practiceType, level); break;
			}
		} while (results == null || results.size() < 1);		//loop if results is null or size = 0

		// init Linkage for all results
		/*
		for (PracticeResult pr : results) {
			practiceResultDAO.initLinkage(pr);
		}*/

		TopResult result = new TopResult(orderType);
		result.setGrade(grade);
		result.setPracticeType(practiceType);
		result.getTopResults().addAll(results);
		result.setFirstPosition(1);
		result.setLevel(level);

		return result;
	}

	/**
	 * Return the top result by orderType and practiceType for all grade
	 */
	public TopResult getTopResult(TopResult.OrderType orderType, String practiceType) {
		logger.info("getTopResult: START");
		return getTopResultByGrade(orderType, practiceType, null);
	}

	public TopResult getTopResultByGrade(TopResult.OrderType orderType, String practiceType, Grade grade) {
		return getTopResultByGrade(orderType, practiceType, grade, null);
	}

	/**
	 * Return the top result by orderType and practiceType and grade and level
	 */
	public TopResult getTopResultByGrade(TopResult.OrderType orderType, String practiceType, Grade grade, Level level) {
		logger.info("getTopResultByGrade: START");
		List<PracticeResult> results = null;

		switch (orderType) {
		case Rate:
			results = practiceResultDAO.getTopRatings(grade, practiceType, level); break;
		case Score:
			results = practiceResultDAO.getTopScores(grade, practiceType, level); break;
		}

		// init Linkage for all results
		for (PracticeResult pr : results) {
			practiceResultDAO.initLinkage(pr);
			logger.info(pr.toString());
		}

		TopResult result = new TopResult(orderType);
		result.setGrade(grade);
		result.setPracticeType(practiceType);
		result.getTopResults().addAll(results);
		result.setLevel(level);
		if (results != null && results.size() > 0) {
			result.setFirstPosition(practiceResultDAO.getPosition(orderType, result.getTopResults().first()));
		} else {
			result.setTopResults(null);
		}
		logger.info("getTopResultByGrade: return TopResult[" + result + "]");
		return result;
	}

	public TopResult getResultListByMember(TopResult.OrderType orderType, String practiceType, Member member) {
		logger.info("getResultListByMember: START");
		return getResultListByMemberGrade(orderType, practiceType, member, null);
	}

	public TopResult getResultListByMemberGrade(TopResult.OrderType orderType, String practiceType, Member member, Grade grade) {
		return getResultListByMemberGrade(orderType, practiceType, member, grade, null);
	}

	/**
	 * Get Result based on the Member
	 */
	public TopResult getResultListByMemberGrade(TopResult.OrderType orderType, String practiceType, Member member, Grade grade, Level level) {
		logger.info("getResultListByMemberGrade: START");

		// get results from DB
		PracticeResult memberResult = practiceResultDAO.getPracticeResult(member, grade, practiceType, level);
		logger.info("getResultListByMemberGrade: Retrieved Member result[" + memberResult + "]");

		// member Result checking
		if (memberResult == null) {
			logger.info("getResultListByMemberGrade: cannot retrieve member [" + member.getUserId() + "] result");
			TopResult blankResult = new TopResult(orderType);
			blankResult.setGrade(grade);
			blankResult.setPracticeType(practiceType);
			blankResult.setTopResults(null);
			blankResult.setLevel(level);
			return blankResult;
		} else if (TopResult.OrderType.Rate.equals(orderType) && memberResult.getFullMark() < PracticeResultDAO.MIN_FULL_MARK) {
			logger.info("getResultListByMemberGrade: member full mark less that require [" + memberResult + "]");
			TopResult blankResult = new TopResult(orderType);
			blankResult.setGrade(grade);
			blankResult.setPracticeType(practiceType);
			blankResult.setTopResults(null);
			blankResult.setLevel(level);
			return blankResult;
		}

		List<PracticeResult> lowerResults = practiceResultDAO.getResultLower(orderType, memberResult);
		List<PracticeResult> higherResults = practiceResultDAO.getResultHigher(orderType, memberResult);
		if (lowerResults!=null) logger.info("getResultListByMemberGrade: Lower results size:" + lowerResults.size());
		if (higherResults!= null) logger.info("getResultListByMemberGrade: Higher results size:" + higherResults.size());

		// Get results list
		List<PracticeResult> results = getListByMember(memberResult, lowerResults, higherResults);

		//	init Linkage for all results
		for (PracticeResult pr : results) {	practiceResultDAO.initLinkage(pr);}

		// Create Top Result
		TopResult result = new TopResult(orderType);
		result.setGrade(grade);
		result.setPracticeType(practiceType);
		result.getTopResults().addAll(results);
		result.setLevel(level);
		if (results != null && results.size() > 0) {
			result.setFirstPosition(practiceResultDAO.getPosition(orderType, result.getTopResults().first()));
		} else {
			result.setTopResults(null);
		}

		logger.info("getTopResultByGrade: return TopResult[" + result + "]");
		return result;
	}

	// ============== Supporting Functions ================//
	private List<PracticeResult> getListByMember(PracticeResult pr, List<PracticeResult> lowerList, List<PracticeResult> higherList)
	{
		int minSize = (int) Math.floor(PracticeResultDAO.TOP_RESULT_QUANTITY / 2);
		List<PracticeResult> subResults = new ArrayList<PracticeResult>();

		// input checking and logging
		if (pr == null) {
			logger.warn("getListByMember: input Result is null");
			return null;
		}
		if (lowerList == null) lowerList = new ArrayList<PracticeResult>();
		if (higherList == null) higherList = new ArrayList<PracticeResult>();
		int lowerSize = lowerList.size();
		int higherSize = higherList.size();

		if (logger.isInfoEnabled()) {
			logger.info("getListByMember: input Result[" + pr + "]");
			logger.info("getListByMember: lowerList.size:" + lowerSize);
			logger.info("getListByMember: higherList.size:" + higherSize);
			logger.info("getListByMember: minSize:" + minSize);
		}

		subResults.add(pr);
		// do not have enough results for both list
		if (lowerSize < minSize && higherSize < minSize) {
			logger.info("getListByMember: both lists are less than [" + minSize + "]");
			subResults.addAll(lowerList);
			subResults.addAll(higherList);
		}
		// lower is less than minreq
		else if (lowerSize < minSize) {
			logger.info("getListByMember: lower list is less than [" + minSize + "]");
			subResults.addAll(lowerList);

			int index = PracticeResultDAO.TOP_RESULT_QUANTITY - subResults.size();
			if (index > higherSize) index = higherSize;
			logger.info("getListByMember: get highList index[" + index + "]");
			subResults.addAll(higherList.subList(0, index));
		}
		// higher is less than minreq
		else if (higherSize < minSize) {
			logger.info("getListByMember: higher list is less than [" + minSize + "]");
			subResults.addAll(higherList);

			int index = PracticeResultDAO.TOP_RESULT_QUANTITY - subResults.size();
			if (index > lowerSize) index = lowerSize;
			logger.info("getListByMember: get lowerList index[" + index + "]");
			subResults.addAll(lowerList.subList(0, index));
		}
		// Normal cases
		else {
			logger.info("getListByMember: both list >= [" + minSize + "]");
			subResults.addAll(lowerList.subList(0, minSize));
			subResults.addAll(higherList.subList(0, minSize));
		}

		logger.info("getListByMember: retrun sub list size:" + subResults.size());
		return subResults;
	}

}