package com.esl.web.jsf.controller.practice;

import com.esl.entity.dictation.Dictation;
import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.service.practice.ISelfDictationService;
import com.esl.service.practice.PhoneticPracticeService;
import com.esl.util.JSFUtil;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.jsf.controller.dictation.ArticleDictationPracticeController;
import com.esl.web.jsf.controller.dictation.DictationEditController;
import com.esl.web.util.LanguageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Controller
@Scope("session")
public class SelfDictationController extends ESLController {
	private static final long serialVersionUID = -7368848157958147632L;

	private static Logger logger = LoggerFactory.getLogger(SelfDictationController.class);
	private final String bundleName = "messages.practice.SelfDictation";
	private final String viewPrefix = "/practice/selfdictation/";
	private final String inputView = viewPrefix + "input";
	private final String practiceView = viewPrefix + "practice";
	private final String resultView = viewPrefix + "result";

	// UI Data
	private List<String> inputVocab = new ArrayList<String>(20);
	private String inputArticle = "";
	private PhoneticPractice practice;
	private String answer;
	private boolean withIPA = false;
	private boolean withRandomCharacters = false;
	private int totalQuestions;
	private Dictation.DictationType lastDictationType = Dictation.DictationType.Vocab;

	// Supporting classes
	@Resource private ISelfDictationService selfDictationService;
	@Resource private IPhoneticPracticeService phoneticPracticeService;
	@Resource private DictationEditController dictationEditController;
	@Resource private ArticleDictationPracticeController articleDictationPracticeController;
	@Value("${SelfDictationService.MaxQuestions}") private int maxQuestions = 20;
	@Value("${Dictation.Article.MaxSize}") private int maxArticleSize = 0;

	// UI Component
	//private HtmlCommandButton practiceCommand;

	// ============== Setter / Getter ================//
	public void setSelfDictationService(ISelfDictationService selfDictationService) {this.selfDictationService = selfDictationService;}
	public void setPhoneticPracticeService(IPhoneticPracticeService phoneticPracticeService) {this.phoneticPracticeService = phoneticPracticeService;}

	public String getAnswer() {return answer;}
	public void setAnswer(String answer) {this.answer = answer;}

	public boolean isWithIPA() { return withIPA; }
	public void setWithIPA(boolean withIPA)  { this.withIPA = withIPA; }

	public boolean isWithRandomCharacters()	{return withRandomCharacters;}
	public void setWithRandomCharacters(boolean withRandomCharacters) {	this.withRandomCharacters = withRandomCharacters;}

	public String getInputArticle() {return inputArticle;}
	public void setInputArticle(String inputArticle) {this.inputArticle = inputArticle;}

	public PhoneticPractice getPractice() {	return practice;}
	public void setPractice(PhoneticPractice practice) {this.practice = practice;}

	public String[] getInputVocab() {
		while (inputVocab.size()<maxQuestions) {
			inputVocab.add("");
		}
		return inputVocab.toArray(new String[]{});
	}
	public void setInputVocab(List<String> inputVocab) {	this.inputVocab = inputVocab;}

	public int getTotalInput() {return maxQuestions;	}

	public int getTotalQuestions() {return totalQuestions; }

	public Dictation.DictationType getLastDictationType() {return lastDictationType;}

	// ============== Constructor ================//
	public SelfDictationController() {
	}


	// ============== Functions ================//

	// Generate the dictation
	@Transactional
	public String start() {
		if (StringUtils.isBlank(inputArticle)) {
			return startByWord();
		} else {
			lastDictationType = Dictation.DictationType.Article;
			if (validateArticleInput())
				return startByArticle();
			else
				return inputView;
		}
	}

	private boolean validateArticleInput() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);

		if (inputArticle.length() > maxArticleSize) {
			logger.info("Article too long");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageFormat.format(bundle.getString("articleTooLong"),maxArticleSize), null));
			return false;
		}
		return true;
	}

	private String startByArticle() {
		Dictation dic = new Dictation();
		dic.setArticle(inputArticle);
		return articleDictationPracticeController.start(dic);
	}

	private String startByWord() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);

		// retrieve vocabs
		inputVocab = getInputVocab((HttpServletRequest)facesContext.getExternalContext().getRequest());

		practice = selfDictationService.generatePractice(null, inputVocab);

		if (practice == null || practice.getQuestions().size() <= 0)
		{
			logger.info("start: NoVocabularyFound: no question can be added");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("NoVocabularyFound"), null));
		}
		else
		{
			// Set unavailable IPA
			for (PhoneticQuestion question : practice.getQuestions()) {
				LanguageUtil.formatIPA(question, locale);
			}

			return practiceView;
		}
		return inputView;
	}

	@Transactional
	public String submitAnswer() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// Check practice have been create or not, if not created, call start
		if (practice == null) {
			logger.warn("submitAnswer: cannot find practice");
			return JSFUtil.redirectToJSF(inputView);
		}

		String result = phoneticPracticeService.checkAnswer(practice, answer);
		logger.info("submitAnswer: phoneticPracticeService.checkAnswer returned code: " + result);

		answer = "";			// Clear answer field

		if (PhoneticPracticeService.INVALID_INPUT.equals(result))
			return null;
		else if (PhoneticPracticeService.SYSTEM_ERROR.equals(result))
		{
			// Need to set errorPage title and description

			return JSFUtil.redirectToJSF(errorView);
		}

		// Logic flow for practice completed
		if (practice.isFinish())
		{
			logger.info("submitAnswer: Finish Practice");
			totalQuestions = practice.getTotalQuestions();
			selfDictationService.completedPractice(practice.getQuestions(), (ServletContext) facesContext.getExternalContext().getContext());
			return JSFUtil.redirectToJSF(resultView);
		}

		// Continue Practice
		return null;
	}

	public String createDictation() throws IOException {
		return dictationEditController.launchWithVocabs(inputVocab);
	}

	public String retry() {
		// Check practice have been create or not, if not created, call start
		if (practice == null) {
			logger.info("retry: cannot find practice");
			return inputView;
		}

		// reset the practice
		practice.setAnswers(null);
		practice.setCorrects(null);
		practice.setMark(0);
		practice.setCurrentQuestion(0);
		logger.info("retry");
		return practiceView;
	}

	// ============== Supporting Functions ================//
	private List<String> getInputVocab(HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		List<String> vocabs = new ArrayList<>();
		for (int i=0; i <= maxQuestions; i++) {
			String[] strArr = params.get("vocab" + i);
			if (strArr != null && strArr.length > 0 &&  !"".equals(strArr[0])) vocabs.add(strArr[0]);
		}
		logger.info("getInputVocab: returned list size[" + vocabs.size() + "]");
		return vocabs;
	}
}
