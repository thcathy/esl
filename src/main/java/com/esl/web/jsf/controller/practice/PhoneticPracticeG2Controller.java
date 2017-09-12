package com.esl.web.jsf.controller.practice;

import com.esl.dao.IGradeDAO;
import com.esl.dao.IMemberDAO;
import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.dao.IPracticeResultDAO;
import com.esl.entity.event.UpdatePracticeHistoryEvent;
import com.esl.enumeration.ESLPracticeType;
import com.esl.enumeration.VocabDifficulty;
import com.esl.exception.ESLSystemException;
import com.esl.model.Grade;
import com.esl.model.PhoneticQuestion;
import com.esl.model.PracticeResult;
import com.esl.service.JSFService;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.service.practice.PhoneticQuestionService;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.jsf.controller.member.MemberWordController;
import com.esl.web.model.practice.PhoneticQuestionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import reactor.bus.Event;
import reactor.bus.EventBus;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private PracticeResult currentGradeResult;
	private PracticeResult allGradeResult;
	private Grade currentGrade;
	private boolean isLevelUp = false;
	private boolean topLevel = false;
	private PhoneticQuestion question;
	private List<PhoneticQuestionHistory> history;
	private int totalMark;
	private int totalFullMark;
	private VocabDifficulty selectedDifficulty;

	// Supporting classes
	@Resource private IGradeDAO gradeDAO;
	@Resource private IMemberDAO memberDAO;
	@Resource private IPhoneticPracticeService phoneticPracticeService;
	@Resource private IPracticeResultDAO practiceResultDAO;
	@Resource private IPhoneticQuestionDAO phoneticQuestionDAO;
	@Resource private PhoneticPracticeController phoneticPracticeController;
	@Resource private MemberWordController memberWordController;
	@Resource private PhoneticQuestionService phoneticQuestionService;
	@Resource private JSFService jsfService;
	@Autowired EventBus eventBus;

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
	public void setPhoneticQuestionDAO(IPhoneticQuestionDAO phoneticQuestionDAO) {this.phoneticQuestionDAO = phoneticQuestionDAO; }
	public void setPhoneticPracticeController(PhoneticPracticeController controller) {this.phoneticPracticeController = controller;}
	public void setMemberWordController(MemberWordController memberWordController) {this.memberWordController = memberWordController; }

	public String getAnswer() {	return answer;	}
	public void setAnswer(String answer) {this.answer = answer;}

	public boolean isLevelUp() {return isLevelUp;}
	public void setLevelUp(boolean isLevelUp) {	this.isLevelUp = isLevelUp;}

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

	public VocabDifficulty getSelectedDifficulty() {return selectedDifficulty;}
	public void setSelectedDifficulty(VocabDifficulty selectedDifficulty) {	this.selectedDifficulty = selectedDifficulty;}

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
		if (question == null) {
			return indexView;
		}
		return "";
	}

	// ============== Functions ================//
	public String start() {
		this.selectedDifficulty = phoneticPracticeController.getSelectedDifficulty();
		logger.info("start: selectedDifficulty: {}", selectedDifficulty);
		if (selectedDifficulty == null) return errorView;

		clearController();
		getRandomQuestion();

		return practiceView;
	}

	public String submitAnswer() {
		logger.info("submitAnswer: word[" + question.getWord() + "], answer[" + answer + "]");
		int mark = 0;
		PhoneticQuestionHistory questionG2 = new PhoneticQuestionHistory();
		questionG2.setAnswer(answer);
		questionG2.setQuestion(question);
		if (question.wordEqual(answer)) {
			mark = 1;
			questionG2.setCorrect(true);
		}
		submitUpdateHistoryEventIfNeeded(mark);
		totalMark += mark;
		totalFullMark += 1;
		answer = "";			// Clear answer field
		history.add(0, questionG2);
		if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);		// remove too many history

		getRandomQuestion();
		return null;
	}

	private void submitUpdateHistoryEventIfNeeded(int mark) {
		if (userSession.getMember() == null) return;

		eventBus.notify("addHistory",
				Event.wrap(new UpdatePracticeHistoryEvent(userSession.getMember(),
						ESLPracticeType.PhoneticPractice,
						question,
						mark > 0,
						mark * selectedDifficulty.weight))
		);
	}


	public String submitAndEnd() {
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
		submitUpdateHistoryEventIfNeeded(mark);
		totalMark += mark;
		answer = "";			// Clear answer field
		history.add(0, questionG2);
		if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);		// remove too many history

		return jsfService.redirectToJSF(completePractice());
	}

	// process when completing the practice
	public String completePractice() {
		logger.info("completePractice: START");

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
		List<PhoneticQuestion> questions = phoneticQuestionDAO.getRandomQuestionWithinRank(selectedDifficulty.rank, 1);
		if (questions == null || questions.size() < 1) {
			throw new ESLSystemException("getRandomQuestion: cannot get any question","getRandomQuestion: cannot get any question");
		}

		question = questions.get(0);
		phoneticQuestionService.enrichVocabImage(question);
		logger.info("getRandomQuestion: a random question: word[" + question.getWord() + "]");

		// add full mark in practice result
		//currentGradeResult.setFullMark(currentGradeResult.getFullMark() + 1);
		//allGradeResult.setFullMark(allGradeResult.getFullMark() + 1);
		//practiceResultDAO.makePersistent(currentGradeResult);
		//practiceResultDAO.makePersistent(allGradeResult);
		//logger.info("getRandomQuestion: add full mark for practice result by 1");

		// set member word unsaved in controller map
		memberWordController.getSavedQuestion().put(question, false);
	}
}

