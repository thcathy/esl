package com.esl.service.practice;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.IPracticeResultDAO;
import com.esl.exception.IllegalParameterException;
import com.esl.model.*;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.web.model.practice.PracticeResultSummary;

@Transactional
@Service("practiceResultService")
public class PracticeResultService implements IPracticeResultService {
	private static Logger logger = LoggerFactory.getLogger(PracticeResultService.class);

	@Resource private IPracticeResultDAO practiceResultDAO = null;
	@Resource private ITopResultService topResultService = null;

	// ============== Constructor ================//
	public PracticeResultService() {}

	// ============== Setter / Getter ================//
	public void setPracticeResultDAO(IPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;	}
	public void setTopResultService(ITopResultService topResultService) { this.topResultService = topResultService; }

	//============== Functions ================//
	/**
	 * Return all practice result summary
	 */
	public List<PracticeResultSummary> getAllPracticeResultSummary(Member member) {
		logger.info("getAllPracticeResultSummary: START");
		if (member == null) throw new IllegalParameterException(new String[]{"member"}, new Object[]{member});
		logger.info("getAllPracticeResultSummary: get all summary for member[" + member.getUserId() + "]");

		List<PracticeResultSummary> allSummary = new ArrayList<PracticeResultSummary>();
		allSummary.add(getPracticeResultSummary(member, PracticeResult.PHONETICPRACTICE, null));
		allSummary.add(getPracticeResultSummary(member, PracticeResult.PHONETICSYMBOLPRACTICE, Level.Full));

		return allSummary;
	}

	/**
	 * Return a practice result summary by input practice type
	 */
	public PracticeResultSummary getPracticeResultSummary(Member member, String practiceType, Level level) {
		logger.info("getPracticeResultSummary: START");
		if (member == null || practiceType == null || practiceType.equals("")) throw new IllegalParameterException(new String[]{"member","practiceType"}, new Object[]{member,practiceType});
		logger.info("getPracticeResultSummary: get practice type[" + practiceType + "], level[" + level + "] for member[" + member.getUserId() + "]");

		PracticeResultSummary summary = new PracticeResultSummary();

		// Get all required practice results and Top Result
		summary.setOverallPracticeResult(practiceResultDAO.getPracticeResult(member, null, practiceType, level));
		summary.setGradedPracticeResult(practiceResultDAO.getPracticeResult(member, member.getGrade(), practiceType, level));
		summary.setScoreRanking(topResultService.getResultListByMemberGrade(TopResult.OrderType.Score, practiceType, member, null, level));
		summary.setRateRanking(topResultService.getResultListByMemberGrade(TopResult.OrderType.Rate, practiceType, member, null, level));
		summary.setPracticeResults(practiceResultDAO.getGradedPracticeResults(member, practiceType, level));

		return summary;
	}

}

