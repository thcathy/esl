package com.esl.web.jsf.controller.practice;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.dao.IMemberWordDAO;
import com.esl.model.MemberWord;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.util.JSFUtil;
import com.esl.web.jsf.controller.member.MemberWordController;
import com.esl.web.model.practice.PhoneticQuestionHistory;

@Controller
@Scope("session")
public class MyVocabPracticeController extends PhoneticPracticeG2Controller {
	private static final long serialVersionUID = 8187935108461189362L;
	private static Logger logger = Logger.getLogger("ESL");
	private final String bundleName = "messages.member.MemberWord";
	private static final String practiceView = "/member/vocab/practice";
	private static final String manageView = "/member/vocab/manage";

	private Set<MemberWord> practicedWord;

	// UI Data
	private String answer = "";
	private MemberWord memberWord;
	private List<PhoneticQuestionHistory> history;
	private int totalMark;
	private int totalFullMark;

	// Supporting classes
	@Resource private IMemberWordDAO memberWordDAO;
	@Resource private MemberWordController memberWordController;
	@Resource private IPhoneticPracticeService phoneticPracticeService;

	// ============== Constructor ================//
	public MyVocabPracticeController() {
		totalFullMark = 0;
		history = new ArrayList<PhoneticQuestionHistory>();
	}


	//	 ============== Getter Functions ================//


	// ============== Functions ================//
	@Override
	public String start() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);

		// clear all existing objects
		clearController();

		// get a random question
		getRandomQuestion();
		if (memberWord == null) {
			logger.info("start: No memberWord found for member[" + userSession.getMember().getUserId() + "]");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("manageNoVocab"), null));
			return manageView;
		}

		return practiceView;
	}

	@Override
	public String submitAnswer() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);

		// Check practice have been create or not, if not created, call start
		if (memberWord == null) {
			logger.info("submitAnswer: cannot find question");
			return JSFUtil.redirect(errorView);
		}

		// Check answer
		logger.info("submitAnswer: word[" + memberWord.getWord().getWord() + "], answer[" + answer + "]");
		int mark = 0;
		PhoneticQuestionHistory questionG2 = new PhoneticQuestionHistory();
		questionG2.setAnswer(answer);
		questionG2.setQuestion(memberWord.getWord());
		if (answer.trim().toLowerCase().equals(memberWord.getWord().getWord().toLowerCase())) {
			mark = 1;
			questionG2.setCorrect(true);
		}
		// update memberWord
		memberWord.addTrial(mark);
		memberWordDAO.persist(memberWord);

		totalMark += mark;
		answer = "";			// Clear answer field
		history.add(0, questionG2);
		if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);		// remove too many history

		getRandomQuestion();

		// check any new question return from db, if not, back to manageView
		if (memberWord == null) {
			logger.info("start: No memberWord left for member[" + userSession.getMember().getUserId() + "]");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("practiceAllWordPracticed"), null));

			String resultString = bundle.getString("practiceCompleteMessage");
			resultString = MessageFormat.format(resultString, totalMark);
			logger.info("completePractice: returned msg [" + resultString +  "]");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resultString, null));

			return JSFUtil.redirect(manageView);
		}

		return null;
	}

	// process when completing the practice
	@Override
	public String completePractice() {
		logger.info("completePractice: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// add complete practice message to next page
		String resultString = bundle.getString("practiceCompleteMessage");
		resultString = MessageFormat.format(resultString, totalMark);
		logger.info("completePractice: returned msg [" + resultString +  "]");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resultString, null));

		clearController();

		// go to member word manage page
		return JSFUtil.redirect(memberWordController.launchManage());
	}

	// ============== Supporting Functions ================//
	private void clearController() {
		answer = "";
		history.clear();
		totalFullMark = 0;
		totalMark = 0;
		practicedWord = new HashSet<MemberWord>();
		memberWordController.setSavedQuestion(new HashMap<PhoneticQuestion, Boolean>());
	}

	private void getRandomQuestion() {
		List<MemberWord> memberWords = memberWordDAO.listRandomWords(userSession.getMember(), 1, practicedWord);

		if (memberWords == null || memberWords.size() < 1) {
			memberWord = null;
			return;
		}
		memberWord = memberWords.get(0);
		phoneticPracticeService.findIPAAndPronoun(memberWord.getWord());
		practicedWord.add(memberWord);
		logger.info("getRandomQuestion: a random memberWord: word[" + memberWord.getWord() + "]");

		// add full mark in practice result
		totalFullMark++;
	}	

	//	 ============== Setter / Getter ================//
	@Override
	public void setMaxHistory(int max) {this.MAX_HISTORY = max; }
	@Override
	public void setMemberWordController(MemberWordController memberWordController) {this.memberWordController = memberWordController; }
	public void setMemberWordDAO(IMemberWordDAO memberWordDAO) {this.memberWordDAO = memberWordDAO; }
	@Override
	public void setPhoneticPracticeService(IPhoneticPracticeService phoneticPracticeService) {this.phoneticPracticeService = phoneticPracticeService;}

	@Override
	public String getAnswer() {	return answer;	}
	@Override
	public void setAnswer(String answer) {this.answer = answer;}

	public MemberWord getMemberWord() {	return memberWord;}
	public void setMemberWord(MemberWord memberWord) {this.memberWord = memberWord;	}

	@Override
	public int getTotalMark() {	return totalMark;}
	@Override
	public void setTotalMark(int totalMark) {this.totalMark = totalMark;}

	@Override
	public int getTotalFullMark() {	return totalFullMark;}
	@Override
	public void setTotalFullMark(int totalFullMark) {this.totalFullMark = totalFullMark;}

	@Override
	public List<PhoneticQuestionHistory> getHistory() {	return history;	}
	@Override
	public void setHistory(List<PhoneticQuestionHistory> history) {	this.history = history;	}
	@Override
	public int getHistorySize() { return history.size(); }

}

