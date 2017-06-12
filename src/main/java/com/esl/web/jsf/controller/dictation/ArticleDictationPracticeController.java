package com.esl.web.jsf.controller.dictation;

import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationPractice;
import com.esl.entity.dictation.SentenceHistory;
import com.esl.service.JSFService;
import com.esl.service.dictation.ArticleDictationService;
import com.esl.web.jsf.controller.UserCreatedPracticeController;
import com.esl.web.model.practice.ScoreBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import reactor.bus.EventBus;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Controller
@Scope("session")
public class ArticleDictationPracticeController extends UserCreatedPracticeController<Dictation> {
	public static int SCOREBAR_FULLLENGTH = 500;

	private static Logger logger = LoggerFactory.getLogger(DictationPracticeController.class);
	public static final String inputView = "/practice/selfdictation/input";
	public static final String practiceView = "/practice/selfdictation/articlepractice";
	public static final String resultView = "/practice/selfdictation/articlepracticeresult";

	//	 Supporting instance
	@Resource EventBus eventBus;
	@Resource ArticleDictationService articleDictationService;
	@Resource JSFService jsfService;
	@Resource DictationEditController dictationEditController;

	//	 ============== UI display data ================//
	private DictationPractice dictation;
	private String answer;
	private List<SentenceHistory> history;
	private int currentSentence;
	private ScoreBar scoreBar;
	private double speakingSpeed = 0.7;

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

	public String start(Dictation input) {
		answer = "";
		currentSentence = 0;

		dictation = new DictationPractice(input, articleDictationService.deriveArticleToSentences(input));
		history = new ArrayList<>(dictation.getSentences().size());

		return practiceView;
	}

	public String retryDictation() {
		return start(dictation.getDictation());
	}

	public String saveDictation() throws IOException {
		return dictationEditController.launchWithArticle(dictation.dictation.getArticle());
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

		if (currentSentence >= dictation.sentences.size()) {
			return jsfService.redirectToJSF(resultView);
		}

		return null;
	}

	public int getCorrectPercentage() {
		long total = history.stream()
				.mapToLong(h -> h.isCorrect.size())
				.sum();
		long correct = history.stream()
						.flatMap(h -> h.isCorrect.stream())
						.filter(x -> x)
						.count();
		logger.info("correct {} / total {}", correct, total);
		return (int) ((double)correct / total * 100);
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

	public double getSpeakingSpeed() {return speakingSpeed;}
	public void setSpeakingSpeed(double speakingSpeed) {this.speakingSpeed = speakingSpeed;	}
}
