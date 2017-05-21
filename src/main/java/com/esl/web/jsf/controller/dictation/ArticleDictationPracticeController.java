package com.esl.web.jsf.controller.dictation;

import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationPractice;
import com.esl.entity.dictation.SentenceHistory;
import com.esl.service.dictation.ArticleDictationService;
import com.esl.web.jsf.controller.UserCreatedPracticeController;
import com.esl.web.model.practice.ScoreBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import reactor.bus.EventBus;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Controller
@Scope("session")
public class ArticleDictationPracticeController extends UserCreatedPracticeController<Dictation> {
	public static int SCOREBAR_FULLLENGTH = 500;
	public static int BASE_RATING = 3;

	private static Logger logger = LoggerFactory.getLogger(DictationPracticeController.class);
	private static final String inputView = "/practice/selfdictation/input";
	private static final String practiceView = "/practice/selfdictation/articlepractice";

	//	 Supporting instance
	@Resource EventBus eventBus;
	@Resource ArticleDictationService articleDictationService;

	//	 ============== UI display data ================//
	private DictationPractice dictation;
	private String answer;
	private List<SentenceHistory> history;
	private int currentSentence;
	private ScoreBar scoreBar;

	// ============== Constructor ================//
	public ArticleDictationPracticeController() {
		scoreBar = new ScoreBar();
		scoreBar.setFullLength(SCOREBAR_FULLLENGTH);
	}

	@Override
	public Dictation getUserCreatedPractice() {
		return dictation.getDictation();
	}

	//============== Functions ================//
	public String initCheck() {
		if (dictation == null) return inputView;
		return "";
	}

	public String start(String inputArticle) {
		answer = "";
		currentSentence = 0;

		Dictation dic = new Dictation();
		dic.setArticle(inputArticle);
		dictation = new DictationPractice(dic);
		history = new ArrayList<>(dictation.getSentences().size());

		return practiceView;
	}

	public String submitAnswer() {
		logger.info("submitAnswer: question [{}]", dictation.sentences.get(currentSentence));
		logger.info("submitAnswer: answer   [{}]", answer);

		history.add(0,
				articleDictationService.compare(
						dictation.sentences.get(currentSentence), answer)
		);

		currentSentence++;
		answer = "";
		return null;
	}

	//	============== Supporting Function ================//
	private void setScoreBar(int startIdx, int endIdx) {
//		int startLength = (int) ((double)startIdx / (double)practice.getTotalQuestions() * SCOREBAR_FULLLENGTH);
//		int endLength = (int) ((double)endIdx / (double)practice.getTotalQuestions() * SCOREBAR_FULLLENGTH);
//		if (startLength < 0) startLength = 0;

//		logger.info("setScoreBar: startLength[" + startLength + "], endLength[" + endLength + "]");
	}

	//	 ============== Setter / Getter ================//
	public DictationPractice getDictation() {return dictation;}
	public void setDictation(DictationPractice dictation) {	this.dictation = dictation; }

	public int getCurrentSentence() {return currentSentence;}
	public void setCurrentSentence(int currentSentence) {this.currentSentence = currentSentence;}

	public String getAnswer() {return answer;}
	public void setAnswer(String answer) {this.answer = answer;}

	public List<SentenceHistory> getHistory() {return history;}
	public void setHistory(List<SentenceHistory> history) {this.history = history;}
}
