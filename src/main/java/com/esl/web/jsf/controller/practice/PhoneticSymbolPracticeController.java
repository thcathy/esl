package com.esl.web.jsf.controller.practice;

import com.esl.dao.IGradeDAO;
import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.dao.IPracticeResultDAO;
import com.esl.exception.ESLSystemException;
import com.esl.model.*;
import com.esl.model.practice.PhoneticSymbols;
import com.esl.service.practice.IPhoneticSymbolPracticeService;
import com.esl.service.practice.ITopResultService;
import com.esl.service.practice.PhoneticQuestionService;
import com.esl.util.JSFUtil;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.model.practice.PhoneticQuestionHistory;
import com.esl.web.util.LanguageUtil;
import com.esl.web.util.SelectItemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.*;

@Controller
@Scope("session")
public class PhoneticSymbolPracticeController extends ESLController {
	public static int MAX_HISTORY = 10;

	private static Logger logger = LoggerFactory.getLogger(PhoneticSymbolPracticeController.class);
	private final String bundleName = "messages.practice.PhoneticSymbolPractice";
	private String inputView = "/practice/phoneticsymbolpractice/input";
	private String practiceView = "/practice/phoneticsymbolpractice/practice";
	private String resultView = "/practice/phoneticsymbolpractice/result";

	// UI Data

	// for input page
	private List<SelectItem> levels;
	private PhoneticSymbols.Level selectedLevel;
	private String selectedGrade;

	// for practice page
	private String answer = "";
	private PracticeResult currentGradeResult;
	private PracticeResult allGradeResult;
	private Grade currentGrade;
	private Map<String, Boolean> selectionPhonics;

	private boolean isLevelUp = false;
	private PhoneticQuestion question;
	private List<PhoneticQuestionHistory> history;
	private int totalMark;
	private int totalFullMark;

	private TopResult scoreRanking;
	private TopResult rateRanking;

	// Supporting classes
	@Resource private IGradeDAO gradeDAO;
	@Resource private IPhoneticSymbolPracticeService phoneticSymbolPracticeService;
	@Resource private IPracticeResultDAO practiceResultDAO;
	@Resource private ITopResultService topResultService;
	@Resource private IPhoneticQuestionDAO phoneticQuestionDAO;
	@Resource private PhoneticQuestionService phoneticQuestionService;

	@Value("${PhoneticPracticeG2.MaxHistory}")
	public void setMaxHistory(int max) {this.MAX_HISTORY = max; }

	// ============== Constructor ================//
	public PhoneticSymbolPracticeController() {
		totalFullMark = 0;
		history = new ArrayList<PhoneticQuestionHistory>();		
	}

	// ============== Functions ================//
	public String launchInput() {
		return inputView;
	}

	public String initCheck() {
		if (question == null)
			return inputView;
		else
			return "";
	}

	@Transactional
	public String start() {
		logger.info("start: selectedGrade: " + selectedGrade);

		// clear all existing objects
		clearController();

		// get selected grade
		currentGrade = gradeDAO.getGradeByTitle(selectedGrade);
		if (currentGrade == null) return errorView;

		// get practice result
		if (userSession.getMember() != null) {
			logger.info("start: Member[" + userSession.getMember().getUserId() + "] start practice");
			currentGradeResult = practiceResultDAO.getPracticeResult(userSession.getMember(), currentGrade, PracticeResult.PHONETICSYMBOLPRACTICE, selectedLevel);
			allGradeResult = practiceResultDAO.getPracticeResult(userSession.getMember(), null, PracticeResult.PHONETICSYMBOLPRACTICE, selectedLevel);
			if (currentGradeResult == null) {
				// create a new result if not exist
				logger.warn("start: practice result not found, create a new phonetic symbol practice result.");
				currentGradeResult = new PracticeResult(userSession.getMember(), currentGrade, PracticeResult.PHONETICSYMBOLPRACTICE, selectedLevel.toString());
				practiceResultDAO.makePersistent(currentGradeResult);
			}
			if (allGradeResult == null) {
				logger.warn("start: practice result not found, create a new phonetic symbol practice result.");
				allGradeResult = new PracticeResult(userSession.getMember(), null, PracticeResult.PHONETICSYMBOLPRACTICE, selectedLevel.toString());
				practiceResultDAO.makePersistent(allGradeResult);
			}
		}

		getRandomQuestion();		// get a random question

		return practiceView;
	}

	@Transactional
	public String submitAnswer() {
		logger.info("submitAnswer: START");

		// Check practice have been create or not, if not created, call start
		if (currentGrade == null) {
			logger.info("submitAnswer: cannot find current grade");
			return JSFUtil.redirect(start());
		}

		boolean isCorrect = phoneticSymbolPracticeService.checkAnswer(question, answer);		// Check answer
		PhoneticQuestionHistory h = prepareHistory(isCorrect);
		history.add(0, h);
		if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);		// remove too many history

		int mark = 0;
		if (isCorrect)  {
			mark++;
			if (userSession.getMember() != null)
				phoneticSymbolPracticeService.updateScoreCard(userSession.getMember(),
						new java.sql.Date((new Date()).getTime()), true, question, selectedLevel);
		}
		totalMark += mark;
		answer = "";			// Clear answer field

		updatePracticeResult(isCorrect);

		getRandomQuestion();

		return null;
	}

	// process when completing the practice
	@Transactional
	public String completePractice() {
		logger.info("completePractice: START");

		if (userSession.isLogined()) {
			// retrieve ranking of the practiced grade
			Member member = userSession.getMember();
			scoreRanking = topResultService.getResultListByMemberGrade(TopResult.OrderType.Score, PracticeResult.PHONETICSYMBOLPRACTICE, member, currentGrade, selectedLevel);
			rateRanking = topResultService.getResultListByMemberGrade(TopResult.OrderType.Rate, PracticeResult.PHONETICSYMBOLPRACTICE, member, currentGrade, selectedLevel);
		}

		// reduce one mark for the undo question
		totalFullMark--;

		return JSFUtil.redirect(resultView);
	}

	//	 ============== Getter Functions ================//

	/**
	 * 	 Return grades available to the user
	 */
	public List<SelectItem> getAvailableGrades() {
		// return all grade if logined, otherwise only the first grade
		List<Grade> grades = gradeDAO.getAll();

		List<SelectItem> items = new ArrayList<SelectItem>(grades.size());
		for (int i=0; i< grades.size(); i++) {
			LanguageUtil.formatGradeDescription(grades.get(i), getLocale());
			SelectItem item = new SelectItem(grades.get(i).getTitle(), grades.get(i).getDescription());
			if (!userSession.isLogined() && i > 0) item.setDisabled(true);
			items.add(item);
		}
		logger.info("getAvailableGrades: returned items size: " + items.size());
		return items;
	}

	/**
	 * Use for jsp, To refresh all UI string to new language in result.jsp
	 */
	public String getInitResultLanguage() {
		logger.info("getInitResultLanguage: START");

		LanguageUtil.formatGradeDescription(currentGrade, getLocale()).getDescription();

		if (userSession.getMember() != null) LanguageUtil.formatGradeDescription(userSession.getMember().getGrade(), getLocale());
		return "";
	}

	public List<SelectItem> getLevels() { return SelectItemUtil.getPhoneticSymobolPracticeLevels(); }

	// ============== Supporting Functions ================//
	private void clearController() {
		answer = "";
		history.clear();
		isLevelUp = false;
		totalFullMark = 0;
		totalMark = 0;
		//memberWordController.setSavedQuestion(new HashMap<PhoneticQuestion, Boolean>());
	}

	private void getRandomQuestion() {
		List<PhoneticQuestion> questions = phoneticQuestionDAO.getRandomQuestionsByGrade(currentGrade, 1, true);
		if (questions == null || questions.size() < 1) {
			throw new ESLSystemException("getRandomQuestion: cannot get any question","getRandomQuestion: cannot get any question");
		}

		question = questions.get(0);
		logger.info("getRandomQuestion: a random question: word[" + question.getWord() + "]");

		phoneticSymbolPracticeService.findIPAAndPronoun(question);
		phoneticQuestionService.enrichVocabImageFromDB(question);

		// get list of phonics
		Set<String> phonics = phoneticSymbolPracticeService.getPhonicsListByLevel(selectedLevel, question.getIPA());
		selectionPhonics = new HashMap<>();
		for (String p : phonics) {
			selectionPhonics.put(p, Boolean.TRUE);
		}
		logger.info("start: selectionPhonics.size[" + selectionPhonics.size() + "]");

		// add full mark in practice result
		/*
		currentGradeResult.setFullMark(currentGradeResult.getFullMark() + 1);
		allGradeResult.setFullMark(allGradeResult.getFullMark() + 1);
		practiceResultDAO.makePersistent(currentGradeResult);
		practiceResultDAO.makePersistent(allGradeResult);
		logger.info("getRandomQuestion: add full mark for practice result by 1");
		 */

		// set member word unsaved in controller map
		//memberWordController.getSavedQuestion().put(question, false);

		totalFullMark++;
	}

	private void setLevels() {
		logger.info("setLevels: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		levels = new ArrayList<SelectItem>();

		for (PhoneticSymbols.Level l : PhoneticSymbols.Level.values()) {
			String desc = bundle.getString("level" + l.toString());
			SelectItem item = new SelectItem(l, desc);
			logger.info("setLevels: create new SelectItem [" + item + "]");
			levels.add(item);
		}
	}

	private PhoneticQuestionHistory prepareHistory(boolean isCorrect) {
		PhoneticQuestionHistory history = new PhoneticQuestionHistory();
		history.setAnswer(answer);
		history.setQuestion(question);
		if (isCorrect) {
			history.setCorrect(true);
		}
		return history;
	}

	private void updatePracticeResult(boolean isCorrect) {
		logger.info("updatePracticeResult: START");

		if (!userSession.isLogined()) return;

		int mark = 0;
		if (isCorrect) mark += 1;
		practiceResultDAO.makePersistent(currentGradeResult);
		practiceResultDAO.makePersistent(allGradeResult);
		currentGradeResult.addResult(mark, 1);
		allGradeResult.addResult(mark, 1);
		//practiceResultDAO.makePersistent(currentGradeResult);
		//practiceResultDAO.makePersistent(allGradeResult);
	}

	//	 ============== Setter / Getter ================//

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

	public PhoneticSymbols.Level getSelectedLevel() {return selectedLevel;}
	public void setSelectedLevel(PhoneticSymbols.Level selectedLevel) {	this.selectedLevel = selectedLevel;}

	public String getSelectedGrade() {	return selectedGrade;}
	public void setSelectedGrade(String selectedGrade) {this.selectedGrade = selectedGrade;	}

	public Map<String, Boolean> getSelectionPhonics() {return selectionPhonics;	}
	public void setSelectionPhonics( Map<String, Boolean> selectionPhonics) {this.selectionPhonics = selectionPhonics;}
	public int getTotalSelectionPhonics() {return selectionPhonics.size(); }

}

