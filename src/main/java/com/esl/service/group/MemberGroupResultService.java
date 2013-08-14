package com.esl.service.group;

import java.util.*;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.PracticeResultDAO;
import com.esl.dao.group.IGroupPracticeResultDAO;
import com.esl.model.group.MemberGroupPracticeResult;
import com.esl.util.BeanUtil;

@Service("memberGroupResultService")
@Transactional
public class MemberGroupResultService implements IMemberGroupResultService {
	private static Logger logger = Logger.getLogger("ESL");

	// supporting class
	@Resource(name="groupPracticeResultDAO") private IGroupPracticeResultDAO practiceResultDAO;

	//	 ============== Setter / Getter ================//
	public void setPracticeResultDAO(IGroupPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;}

	//	 ============== Constructor ================//
	public MemberGroupResultService() {}

	//	 ============== Functions ================//

	/**
	 * Schedule job to generate member_group_practice_result table
	 */
	public void generatePracticeResult() {
		logger.info("generatePracticeResult: START");
		practiceResultDAO.removeAllGroupResult();
		practiceResultDAO.importGroupResult();
		logger.info("generatePracticeResult: END");
	}

	/**
	 * return the list of result lower and higher the input result
	 */
	public List<MemberGroupPracticeResult> listResults(MemberGroupPracticeResult result) {
		logger.info("listResults: START");
		// input check
		if (result == null) {
			return new ArrayList<MemberGroupPracticeResult>();
		}

		List<MemberGroupPracticeResult> lowerResults = practiceResultDAO.listResultsLower(result);
		List<MemberGroupPracticeResult> higherResults = practiceResultDAO.listResultsHigher(result);
		if (lowerResults!=null) logger.info("getResultListByMemberGrade: Lower results size:" + lowerResults.size());
		if (higherResults!= null) logger.info("getResultListByMemberGrade: Higher results size:" + higherResults.size());

		// Get results list
		return BeanUtil.orderedList(PracticeResultDAO.TOP_RESULT_QUANTITY, result, lowerResults, higherResults);
	}

	/**
	 * return a map contain the result position with group id as key
	 */
	public Map<Long, Integer> getPositionMap(List<MemberGroupPracticeResult> list) {
		logger.info("getPositionMap: START");
		Map<Long, Integer> m = new HashMap<Long, Integer>();

		for (MemberGroupPracticeResult result : list) {
			m.put(result.getGroup().getId(), practiceResultDAO.getRank(result));
		}
		logger.info("getPositionMap: returned map size[" + m.size() + "]");
		return m;
	}
}
