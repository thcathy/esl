package com.esl.web.jsf.controller.practice;

import com.esl.entity.practice.GrammarPractice;
import com.esl.service.practice.IGrammarPracticeService;
import com.esl.util.practice.GrammarPracticeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Controller
@Scope("session")
public class GrammarPracticeController extends BaseWithScoreBarController {
	private static final long serialVersionUID = 1L;
	public static int MAX_PASSAGE_CHARACTER = 2000;
	private static String QUESTION_HTML_TAG = "<span id=\"question{?}\" class=\"grammarQuestion\">&nbsp;&nbsp;({?})&nbsp;&nbsp;</span>";
	private static String QUESTION_PATTERN = "&nbsp;&nbsp;\\(\\d+\\)&nbsp;&nbsp;";
	private static String ANSWER_PATTERN = "{number}){answer}";

	private static Logger logger = LoggerFactory.getLogger(GrammarPracticeController.class);
	private static final String bundleName = "messages.practice.Grammar";
	private static final String quickStartView = "/practice/grammar/quick";
	private static final String practiceView = "/practice/grammar/practice";
	private static final String resultView = "/practice/grammar/result";

	//	 Supporting instance
	@Resource private IGrammarPracticeService service;

	// ============== UI display data ================//

	private String inputPassage;
	private String inputPracticeType = "PracticeType.Preposition";
	private GrammarPractice.PracticeType practiceType = GrammarPractice.PracticeType.Preposition;
	private GrammarPractice.QuestionFormat questionFormat = GrammarPractice.QuestionFormat.FillInTheBlank;
	private List<String> inputAnswers;
	private int mark;
	private List<Boolean> results;
	private String htmlPassageWithAnswer;

	private GrammarPractice practice;

	// ============== Constructor ================//
	public GrammarPracticeController() {
		super();
	}

	//============== Functions ================//

	public String quickStart() {
		practice = service.generatePracticeByPassage(inputPassage, practiceType, questionFormat, QUESTION_HTML_TAG, QUESTION_PATTERN, ANSWER_PATTERN);

		if (practice.getQuestionPositions().size() < 1) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Locale locale = facesContext.getViewRoot().getLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
			facesContext.addMessage("inputForm:passage", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("passageNoQuestion"), null));
			return quickStartView;
		}

		setupInputAnswers();

		logger.info(inputPassage);

		return practiceView;
	}

	public String submitAnswer() {
		results = new ArrayList<Boolean>(inputAnswers.size());
		mark = service.checkAnswer(inputAnswers, results, practice);
		htmlPassageWithAnswer = GrammarPracticeGenerator.getHTMLPassageWithAnswer(practice);

		return resultView;
	}

	//	============== Getter Function ================//


	//	============== Supporting Function ================//
	private void setupInputAnswers() {
		inputAnswers = new ArrayList<String>(practice.getQuestionPositions().size());
		for (int i=0; i < practice.getQuestionPositions().size(); i++) inputAnswers.add("");
	}


	//	 ============== Setter / Getter ================//
	public String getInputPassage() {return inputPassage;}
	public void setInputPassage(String inputPassage) {this.inputPassage = inputPassage;}

	public List<String> getInputAnswers() {return inputAnswers;}
	public void setInputAnswers(List<String> inputAnswers) {this.inputAnswers = inputAnswers;}

	public GrammarPractice getPractice() {return practice;}
	public void setPractice(GrammarPractice practice) {this.practice = practice;}

	public List<Boolean> getResults() {	return results;}
	public void setResults(List<Boolean> results) {	this.results = results;}

	public int getMark() {return mark;}
	public void setMark(int mark) {this.mark = mark;}

	public String getHtmlPassageWithAnswer() {return htmlPassageWithAnswer;}
	public void setHtmlPassageWithAnswer(String htmlPassageWithAnswer) {	this.htmlPassageWithAnswer = htmlPassageWithAnswer;}

	public String getInputPracticeType() {	return inputPracticeType;}
	public void setInputPracticeType(String inputPracticeType) {
		this.inputPracticeType = inputPracticeType;
		String[] value = inputPracticeType.split("\\.");
		if (value.length == 2) practiceType = GrammarPractice.PracticeType.valueOf(value[1]);
	}
	public GrammarPractice.PracticeType getPracticeType() { return practiceType;}

	public String getPracticeTypeString() {
		if (practiceType == null) return "";

		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return ResourceBundle.getBundle(bundleName, locale).getString(practiceType.toString());
	}
}
