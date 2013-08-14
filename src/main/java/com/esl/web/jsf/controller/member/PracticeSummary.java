package com.esl.web.jsf.controller.member;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.dao.IPhoneticPracticeHistoryDAO;
import com.esl.dao.IPracticeResultDAO;
import com.esl.model.*;
import com.esl.web.jsf.controller.ESLController;

@Controller
@Scope("session")
public class PracticeSummary extends ESLController {
	// Supporting instance
	@Resource private IPracticeResultDAO practiceResultDAO;
	@Resource private IPhoneticPracticeHistoryDAO phoneticPracticeHistoryDAO;

	// UI retrieve variables
	private PracticeResult allPhoneticPracticeResult;
	private PracticeResult bestPhoneticPracticeResult;
	private PracticeResult favourPhoneticPracticeResult;
	private PhoneticPracticeHistory lastPhoneticPracticeHistory;

	// ============== Setter / Getter ================//
	public void setPracticeResultDAO(IPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;	}
	public void setPhoneticPracticeHistoryDAO(IPhoneticPracticeHistoryDAO phoneticPracticeHistoryDAO) {	this.phoneticPracticeHistoryDAO = phoneticPracticeHistoryDAO;}

	public PracticeResult getAllPhoneticPracticeResult() {	return allPhoneticPracticeResult;}
	public void setAllPhoneticPracticeResult(PracticeResult allPhoneticPracticeResult) {this.allPhoneticPracticeResult = allPhoneticPracticeResult;	}

	public PracticeResult getBestPhoneticPracticeResult() {	return bestPhoneticPracticeResult;	}
	public void setBestPhoneticPracticeResult(PracticeResult bestPhoneticPracticeResult) {	this.bestPhoneticPracticeResult = bestPhoneticPracticeResult;}

	public PracticeResult getFavourPhoneticPracticeResult() {return favourPhoneticPracticeResult;}
	public void setFavourPhoneticPracticeResult(PracticeResult favourPhoneticPracticeResult) {	this.favourPhoneticPracticeResult = favourPhoneticPracticeResult;}

	public PhoneticPracticeHistory getLastPhoneticPracticeHistory() {return lastPhoneticPracticeHistory;}
	public void setLastPhoneticPracticeHistory(PhoneticPracticeHistory lastPhoneticPracticeHistory) {this.lastPhoneticPracticeHistory = lastPhoneticPracticeHistory;}

	// ============== Constructor ================//
	public PracticeSummary() {}

	// ============== Functions ================//
	public void retrievePracticeResult(Member member) {
		allPhoneticPracticeResult = practiceResultDAO.getPracticeResult(member, null, PracticeResult.PHONETICPRACTICE);
		favourPhoneticPracticeResult = practiceResultDAO.getFavourPracticeResultByMember(member, PracticeResult.PHONETICPRACTICE);
		//bestPhoneticPracticeResult = practiceResultDAO.getBestPracticeResultByMember(member, PracticeResult.PHONETICPRACTICE);
		//lastPhoneticPracticeHistory = phoneticPracticeHistoryDAO.getLastPracticeHistoryByMember(member);

		Logger.getLogger("ESL").info("retrievePracticeResult: FINISH");
	}

}
