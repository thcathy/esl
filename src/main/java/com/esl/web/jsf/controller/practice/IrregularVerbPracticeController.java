package com.esl.web.jsf.controller.practice;

import com.esl.dao.practice.IIrregularVerbDAO;
import com.esl.entity.practice.qa.IrregularVerb;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.IIrregularVerbPracticeService;
import com.esl.web.model.practice.IrregularVerbPracticeHistory;
import com.esl.web.model.practice.IrregularVerbPracticeHistoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@Scope("session")
public class IrregularVerbPracticeController extends BaseWithScoreBarController {
	public static int MAX_HISTORY = 10;
	public static int MARK_PER_QUESTION_WO_PP = 3;
	public static int MARK_PER_QUESTION_WITH_PP = 3;

	private static Logger logger = LoggerFactory.getLogger(IrregularVerbPracticeController.class);
	private static final String bundleName = "messages.practice.IrregularVerbPractice";
	private static final String practiceView = "/practice/irregularverb/practice";
	private static final String resultView = "/practice/irregularverb/result";

	//	 Supporting instance
	@Resource private IIrregularVerbDAO irregularVerbDAO;
	@Resource private IIrregularVerbPracticeService irregularVerbPracticeService;

	// ============== UI display data ================//
	private PhoneticQuestion phoneticQ;
	private IrregularVerb question;
	private IrregularVerb answer;
	private IrregularVerb input;
	private int fullMark;
	private int mark;
	private List<IrregularVerbPracticeHistory> histories;
	private boolean withPastParticiple = true;
	private int markPerQuestion = MARK_PER_QUESTION_WITH_PP;
	@Value("${Practice.ShowPopUp.Count}") private int showPopUpSignUpCount;

	// ============== Constructor ================//
	public IrregularVerbPracticeController() {
		super();
	}

	//============== Functions ================//

	/**
	 * Reset variables to start practice
	 */
	public String start() {
		mark = 0;
		fullMark = 0;
		question = null;
		answer = null;
		phoneticQ = null;
		input = null;
		histories = new ArrayList<IrregularVerbPracticeHistory>();
		setScoreBar(0,1);
		return practice();
	}

	public String practice() {
		final String logPrefix = "practice: ";
		logger.info(logPrefix + "START");

		// check answer if needed
		if (question != null) checkAnswerAndAddHistory();

		// get a new question
		prepareNewQuestion();
		return practiceView;
	}

	//	============== Getter Function ================//

	public int getTotalQuestions() {
		return fullMark / markPerQuestion + 1;
	}

	public boolean isShowSignUpPopUp() {
		return userSession.getMember() == null && showPopUpSignUpCount < (fullMark / markPerQuestion);
	}

	//	============== Supporting Function ================//

	private void prepareNewQuestion() {
		answer = irregularVerbDAO.getRandomVerbs(1).get(0);
		question = randomVerb(answer);
		phoneticQ = irregularVerbPracticeService.getPhoneticQuestionByVerb(answer);
		input = new IrregularVerb("","","","");
	}


	/**
	 * Calculate marks, set score bar and history
	 */
	private void checkAnswerAndAddHistory() {
		final String logPrefix = "getMarkAndHistory: ";
		logger.info("{}START", logPrefix);

		int corrects = 0;
		IrregularVerbPracticeHistory history = new IrregularVerbPracticeHistory();

		IrregularVerbPracticeHistoryUnit presentHistory = new IrregularVerbPracticeHistoryUnit();
		corrects += getMarkAndHistoryPerUnit(question.getPresent(), answer.getPresent(), input.getPresent(), presentHistory);
		IrregularVerbPracticeHistoryUnit presentParticipleHistory = new IrregularVerbPracticeHistoryUnit();
		corrects += getMarkAndHistoryPerUnit(question.getPresentParticiple(), answer.getPresentParticiple(), input.getPresentParticiple(), presentParticipleHistory);
		IrregularVerbPracticeHistoryUnit pastHistory = new IrregularVerbPracticeHistoryUnit();
		corrects += getMarkAndHistoryPerUnit(question.getPast(), answer.getPast(), input.getPast(), pastHistory);
		IrregularVerbPracticeHistoryUnit pastParticipleHistory = new IrregularVerbPracticeHistoryUnit();
		corrects += getMarkAndHistoryPerUnit(question.getPastParticiple(), answer.getPastParticiple(), input.getPastParticiple(), pastParticipleHistory);
		logger.debug("{}Input [{}], Answer [{}], Mark [{}]", new Object[] {logPrefix, input, answer, corrects});

		// update mark stat
		fullMark += markPerQuestion;
		setScoreBar(mark + corrects, fullMark);
		mark += corrects;


		// update histories
		history.setPresent(presentHistory);
		history.setPresentParticiple(presentParticipleHistory);
		history.setPast(pastHistory);
		history.setPastParticiple(pastParticipleHistory);
		addHistory(history);

		// update score card
		if (userSession.getMember() != null && corrects > 0) {
			irregularVerbPracticeService.updateScoreCard(userSession.getMember(), new java.sql.Date((new Date()).getTime()), corrects);
		}
	}

	private void addHistory(IrregularVerbPracticeHistory history) {
		histories.add(0, history);

		if (histories.size() > MAX_HISTORY) {
			histories.remove(histories.size() - 1);
		}
	}

	private int getMarkAndHistoryPerUnit(String question, String answer, String input, IrregularVerbPracticeHistoryUnit history) {
		history.setWord(answer);
		if (question != null && !question.equals("")) {
			history.setType(IrregularVerbPracticeHistoryUnit.Type.Question);
		} else {
			if (answer.toLowerCase().equals(input.toLowerCase())) {
				history.setType(IrregularVerbPracticeHistoryUnit.Type.Correct);
				return 1;
			} else {
				history.setType(IrregularVerbPracticeHistoryUnit.Type.Wrong);
			}
		}
		return 0;
	}

	/**
	 * Return a verb that only set one field randomly
	 */
	private IrregularVerb randomVerb(IrregularVerb input) {
		if (input == null) return null;

		IrregularVerb resultVerb = new IrregularVerb();
		Random r = new Random(new Date().getTime());

		int switchNum;
		if (withPastParticiple) {
			switchNum = r.nextInt(4);
		} else {
			switchNum = r.nextInt(3);
		}

		switch (switchNum) {
		case 0:
			resultVerb.setPresent(input.getPresent());
			break;
		case 1:
			resultVerb.setPresentParticiple(input.getPresentParticiple());
			break;
		case 2:
			resultVerb.setPast(input.getPast());
			break;
		case 3:
			resultVerb.setPastParticiple(input.getPastParticiple());
			break;
		}

		logger.debug("return random verb [{}]", resultVerb);
		return resultVerb;
	}

	//	 ============== Setter / Getter ================//
	public void setIrregularVerbDAO(IIrregularVerbDAO irregularVerbDAO) {this.irregularVerbDAO = irregularVerbDAO;}
	public void setIrregularVerbPracticeService(IIrregularVerbPracticeService irregularVerbPracticeService) {this.irregularVerbPracticeService = irregularVerbPracticeService;}
	public void setShowSignUpPopUpCount(int count) { this.showPopUpSignUpCount = count;}

	public PhoneticQuestion getPhoneticQ() {return phoneticQ;}
	public void setPhoneticQ(PhoneticQuestion phoneticQ) {this.phoneticQ = phoneticQ;}

	public IrregularVerb getQuestion() {return question;}
	public void setQuestion(IrregularVerb question) {	this.question = question;}

	public int getFullMark() {return fullMark;}
	public void setFullMark(int fullMark) {this.fullMark = fullMark;}

	public int getMark() {return mark;}
	public void setMark(int mark) {	this.mark = mark;}

	public void setAnswer(IrregularVerb answer) {this.answer = answer;}
	public IrregularVerb getAnswer() {return answer;}

	public List<IrregularVerbPracticeHistory> getHistories() {return histories;}
	public void setHistories(List<IrregularVerbPracticeHistory> histories) {this.histories = histories;}

	public IrregularVerb getInput() {return input;}
	public void setInput(IrregularVerb input) {	this.input = input;}

	public boolean isWithPastParticiple() {return withPastParticiple;}
	public void setWithPastParticiple(boolean withPastParticiple) {
		this.withPastParticiple = withPastParticiple;
		if (withPastParticiple)
			this.markPerQuestion = MARK_PER_QUESTION_WITH_PP;
		else
			this.markPerQuestion = MARK_PER_QUESTION_WO_PP;
	}
}
