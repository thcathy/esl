package com.esl.web.jsf.controller.practice;

import com.esl.dao.IGradeDAO;
import com.esl.dao.IMemberDAO;
import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.dao.IPracticeResultDAO;
import com.esl.exception.ESLSystemException;
import com.esl.model.*;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.service.practice.ITopResultService;
import com.esl.service.practice.PhoneticQuestionService;
import com.esl.util.JSFUtil;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.jsf.controller.member.MemberWordController;
import com.esl.web.model.practice.PhoneticQuestionHistory;
import com.esl.web.util.LanguageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import java.util.*;

@Controller
@Scope("session")
public class PhoneticPracticeG2Controller extends ESLController {
	private static final long serialVersionUID = -7163560838834679113L;
	public static int MAX_HISTORY = 10;

	private static Logger logger = LoggerFactory.getLogger(PhoneticPracticeG2Controller.class);
	private final String bundleName = "messages.practice.PhoneticPractice";
	private String practiceView = "/practice/phoneticpracticeG2/practice";
	private String resultView = "/practice/phoneticpracticeG2/result";

	// UI Data
	private String answer = "";
	private TopResult scoreRanking;
	private TopResult rateRanking;
	private PracticeResult currentGradeResult;
	private PracticeResult allGradeResult;
	private Grade currentGrade;
	private boolean isLevelUp = false;
	private boolean topLevel = false;
	private PhoneticQuestion question;
	private List<PhoneticQuestionHistory> history;
	private int totalMark;
	private int totalFullMark;

	// Supporting classes
	@Resource private IGradeDAO gradeDAO;
	@Resource private IMemberDAO memberDAO;
	@Resource private IPhoneticPracticeService phoneticPracticeService;
	@Resource private IPracticeResultDAO practiceResultDAO;
	@Resource private ITopResultService topResultService;
	@Resource private IPhoneticQuestionDAO phoneticQuestionDAO;
	@Resource private PhoneticPracticeController phoneticPracticeController;
	@Resource private MemberWordController memberWordController;
	@Resource private PhoneticQuestionService phoneticQuestionService;

	// ============== Constructor ================//
	public PhoneticPracticeG2Controller() {
		totalFullMark = 1;
		history = new ArrayList<PhoneticQuestionHistory>();
	}

	// ============== Setter / Getter ================//
	@Value("${PhoneticPracticeG2.MaxHistory}") public void setMaxHistory(int max) {this.MAX_HISTORY = max; }

	public void setGradeDAO(IGradeDAO gradeDAO) {this.gradeDAO = gradeDAO;}
	public void setMemberDAO(IMemberDAO memberDAO) {this.memberDAO = memberDAO; }
	public void setPhoneticPracticeService(IPhoneticPracticeService phoneticPracticeService) {this.phoneticPracticeService = phoneticPracticeService;}
	public void setPracticeResultDAO(IPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;	}
	public void setTopResultService(ITopResultService topResultService) {this.topResultService = topResultService; }
	public void setPhoneticQuestionDAO(IPhoneticQuestionDAO phoneticQuestionDAO) {this.phoneticQuestionDAO = phoneticQuestionDAO; }
	public void setPhoneticPracticeController(PhoneticPracticeController controller) {this.phoneticPracticeController = controller;}
	public void setMemberWordController(MemberWordController memberWordController) {this.memberWordController = memberWordController; }

	public String getAnswer() {	return answer;	}
	public void setAnswer(String answer) {this.answer = answer;}

	public boolean isLevelUp() {return isLevelUp;}
	public void setLevelUp(boolean isLevelUp) {	this.isLevelUp = isLevelUp;}

	public TopResult getRateRanking() {	return rateRanking;}
	public void setRateRanking(TopResult rateRanking) {	this.rateRanking = rateRanking;	}

	public TopResult getScoreRanking() {return scoreRanking;}
	public void setScoreRanking(TopResult scoreRanking) {this.scoreRanking = scoreRanking;	}

	public PracticeResult getCurrentGradeResult() {	return currentGradeResult;	}
	public void setCurrentGradeResult(PracticeResult currentGradeResult) {	this.currentGradeResult = currentGradeResult;}

	public PhoneticQuestion getQuestion() {	return question;}
	public void setQuestion(PhoneticQuestion question) {this.question = question;}

	public Grade getCurrentGrade() {return currentGrade;}
	public void setCurrentGrade(Grade currentGrade) {this.currentGrade = currentGrade;}

	public int getTotalMark() {	return totalMark;}
	public void setTotalMark(int totalMark) {this.totalMark = totalMark;}

	public int getTotalFullMark() {	return totalFullMark;}
	public void setTotalFullMark(int totalFullMark) {this.totalFullMark = totalFullMark;}

	public List<PhoneticQuestionHistory> getHistory() {	return history;	}
	public void setHistory(List<PhoneticQuestionHistory> history) {	this.history = history;	}
	public int getHistorySize() { return history.size(); }

	public boolean isTopLevel() {return topLevel;}
	public void setTopLevel(boolean topLevel) {this.topLevel = topLevel;}

	public void setScoreBarCurrentMark(int foo) {}
	public int getScoreBarCurrentMark() {
		if (topLevel) {
			return currentGradeResult.getMark();
		} else {
			return totalMark;
		}
	}

	public void setScoreBarMaxMark(int foo) {}
	public int getScoreBarMaxMark() {
		if (topLevel) {
			return currentGrade.getPhoneticPracticeLvUpRequire();
		} else {
			return totalFullMark;
		}
	}

	//	 ============== Getter Functions ================//
	/**
	 * Use for jsp, To refresh all UI string to new language in result.jsp
	 */
	public String init() {
		try {
			logger.info("getInitResultLanguage: START");
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			logger.info("getInitResultLanguage: Format obj for :" + locale);

			LanguageUtil.formatGradeDescription(currentGrade, locale).getDescription();
			if (userSession.getMember() != null)
				LanguageUtil.formatGradeDescription(userSession.getMember().getGrade(), locale);
		} catch (Exception e) {
			logger.warn("cannot init phonetic practice G2 result page", e);
			return indexView;
		}
		return "";
	}

	// ============== Functions ================//
	public String start() {
		logger.info("start: selectedGrade: " + phoneticPracticeController.getSelectedGrade());

		// clear all existing objects
		clearController();

		// get selected grade
		currentGrade = gradeDAO.getGradeByTitle(phoneticPracticeController.getSelectedGrade());
		if (currentGrade == null) return errorView;

		// get practice result
		currentGradeResult = practiceResultDAO.getPracticeResult(userSession.getMember(), currentGrade, PracticeResult.PHONETICPRACTICE);
		allGradeResult = practiceResultDAO.getPracticeResult(userSession.getMember(), null, PracticeResult.PHONETICPRACTICE);
		if (currentGradeResult == null) {
			// create a new result if not exist
			logger.warn("start: practice result not found, create a new one.");
			currentGradeResult = new PracticeResult(userSession.getMember(), currentGrade, PracticeResult.PHONETICPRACTICE);
			practiceResultDAO.makePersistent(currentGradeResult);
		}
		topLevel = currentGrade.isNotTopGrade() && currentGrade.equals(userSession.getMember().getGrade());

		// get a random question
		getRandomQuestion();

		return practiceView;
	}

	public String submitAnswer() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		// Check practice have been create or not, if not created, call start
		if (currentGrade == null) {
			logger.info("submitAnswer: cannot find current grade");
			return JSFUtil.redirect(start());
		}

		// Check answer
		logger.info("submitAnswer: word[" + question.getWord() + "], answer[" + answer + "]");
		int mark = 0;
		PhoneticQuestionHistory questionG2 = new PhoneticQuestionHistory();
		questionG2.setAnswer(answer);
		questionG2.setQuestion(question);
		if (question.wordEqual(answer)) {
			mark = 1;
			questionG2.setCorrect(true);
		}
		totalMark += mark;
		totalFullMark += 1;
		answer = "";			// Clear answer field
		history.add(0, questionG2);
		if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);		// remove too many history

		// update practice result
		logger.info("submitAnswer: update practice result");
		currentGradeResult.setMark(currentGradeResult.getMark() + mark);
		allGradeResult.setMark(allGradeResult.getMark() + mark);
		practiceResultDAO.makePersistent(currentGradeResult);
		practiceResultDAO.makePersistent(allGradeResult);

		// update scoreCard
		if (userSession.getMember() != null && mark > 0) {
			phoneticPracticeService.updateScoreCard(userSession.getMember(), new java.sql.Date((new Date()).getTime()), true, question);
		}

		// Check isLevelup
		if (topLevel && currentGrade.equals(userSession.getMember().getGrade()) && currentGradeResult.getMark() >= currentGrade.getPhoneticPracticeLvUpRequire()) {
			Grade upperGrade = gradeDAO.getGradeByLevel(currentGrade.getLevel() + 1);
			userSession.setMember(memberDAO.getMemberById(userSession.getMember().getId()));
			logger.info("submitAnswer: LEVEL_UP: new grade:" + upperGrade);
			if (upperGrade != null) {
				userSession.getMember().setGrade(upperGrade);
				memberDAO.makePersistent(userSession.getMember());
				isLevelUp = true;
				logger.info("submitAnswer: Member[" + userSession.getMember().getUserId() + "] level up to grade[" + userSession.getMember().getGrade() + "]");
			}
			isLevelUp = true;
			return JSFUtil.redirect(completePractice());
		}

		getRandomQuestion();

		return null;
	}


	public String submitAndEnd() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		// Check answer
		logger.info("submitAnswer: word[" + question.getWord() + "], answer[" + answer + "]");
		int mark = 0;
		PhoneticQuestionHistory questionG2 = new PhoneticQuestionHistory();
		questionG2.setAnswer(answer);
		questionG2.setQuestion(question);
		if (question.wordEqual(answer)) {
			mark = 1;
			questionG2.setCorrect(true);
		}
		totalMark += mark;
		answer = "";			// Clear answer field
		history.add(0, questionG2);
		if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);		// remove too many history

		// update practice result
		logger.info("submitAnswer: update practice result");
		currentGradeResult.setMark(currentGradeResult.getMark() + mark);
		allGradeResult.setMark(allGradeResult.getMark() + mark);
		practiceResultDAO.makePersistent(currentGradeResult);
		practiceResultDAO.makePersistent(allGradeResult);

		// update scoreCard
		if (userSession.getMember() != null && mark > 0) {
			phoneticPracticeService.updateScoreCard(userSession.getMember(), new java.sql.Date((new Date()).getTime()), true, question);
		}

		// Check isLevelup
		if (topLevel && currentGrade.equals(userSession.getMember().getGrade()) && currentGradeResult.getMark() >= currentGrade.getPhoneticPracticeLvUpRequire()) {
			Grade upperGrade = gradeDAO.getGradeByLevel(currentGrade.getLevel() + 1);
			userSession.setMember(memberDAO.getMemberById(userSession.getMember().getId()));
			logger.info("submitAnswer: LEVEL_UP: new grade:" + upperGrade);
			if (upperGrade != null) {
				userSession.getMember().setGrade(upperGrade);
				memberDAO.makePersistent(userSession.getMember());
				isLevelUp = true;
				logger.info("submitAnswer: Member[" + userSession.getMember().getUserId() + "] level up to grade[" + userSession.getMember().getGrade() + "]");
			}
			isLevelUp = true;
		}
		return JSFUtil.redirect(completePractice());
	}

	// process when completing the practice
	public String completePractice() {
		logger.info("completePractice: START");

		Member member = userSession.getMember();
		// retrieve ranking of the practiced grade
		scoreRanking = topResultService.getResultListByMemberGrade(TopResult.OrderType.Score, PracticeResult.PHONETICPRACTICE, member, currentGrade);
		rateRanking = topResultService.getResultListByMemberGrade(TopResult.OrderType.Rate, PracticeResult.PHONETICPRACTICE, member, currentGrade);

		return resultView;
	}

	// ============== Supporting Functions ================//
	public void clearController() {
		answer = "";
		history.clear();
		isLevelUp = false;
		totalFullMark = 1;
		totalMark = 0;
		memberWordController.setSavedQuestion(new HashMap<PhoneticQuestion, Boolean>());
	}

	public void getRandomQuestion() {
		List<PhoneticQuestion> questions = phoneticQuestionDAO.getRandomQuestionsByGrade(currentGrade, 1, true);
		if (questions == null || questions.size() < 1) {
			throw new ESLSystemException("getRandomQuestion: cannot get any question","getRandomQuestion: cannot get any question");
		}

		question = questions.get(0);
		phoneticQuestionService.enrichVocabImageFromDB(question);
		logger.info("getRandomQuestion: a random question: word[" + question.getWord() + "]");

		// add full mark in practice result
		currentGradeResult.setFullMark(currentGradeResult.getFullMark() + 1);
		allGradeResult.setFullMark(allGradeResult.getFullMark() + 1);
		practiceResultDAO.makePersistent(currentGradeResult);
		practiceResultDAO.makePersistent(allGradeResult);
		logger.info("getRandomQuestion: add full mark for practice result by 1");

		// set member word unsaved in controller map
		memberWordController.getSavedQuestion().put(question, false);
	}
}

