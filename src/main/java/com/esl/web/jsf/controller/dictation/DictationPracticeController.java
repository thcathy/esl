package com.esl.web.jsf.controller.dictation;

import com.esl.dao.IMemberDAO;
import com.esl.dao.dictation.IDictationDAO;
import com.esl.dao.dictation.IDictationHistoryDAO;
import com.esl.dao.dictation.IMemberDictationHistoryDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationHistory;
import com.esl.entity.dictation.MemberDictationHistory;
import com.esl.model.Member;
import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;
import com.esl.service.dictation.IDictationManageService;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.service.practice.ISelfDictationService;
import com.esl.service.practice.PhoneticPracticeService;
import com.esl.util.JSFUtil;
import com.esl.util.ValidationUtil;
import com.esl.web.jsf.controller.CheckPasswordController;
import com.esl.web.jsf.controller.UserCreatedPracticeController;
import com.esl.web.model.practice.ScoreBar;
import com.esl.web.util.LanguageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;

@SuppressWarnings("serial")
@Controller
@Scope("session")
public class DictationPracticeController extends UserCreatedPracticeController<Dictation> {
	public static int SCOREBAR_FULLLENGTH = 500;
	public static int BASE_RATING = 3;

	private static Logger logger = LoggerFactory.getLogger(DictationPracticeController.class);
	private static final String bundleName = "messages.member.Dictation";
	private static final String startDictationURL = "/member/dictation/open.jsf?id=";
	private static final String startView = "/member/dictation/start";
	private static final String practiceView = "/member/dictation/practice";
	private static final String resultView = "/member/dictation/result";

	//	 Supporting instance
	@Resource private IDictationManageService manageService;
	@Resource private CheckPasswordController checkPasswordController;
	@Resource private IDictationDAO dictationDAO;
	@Resource private IMemberDAO memberDAO;
	@Resource private IMemberDictationHistoryDAO memberDictationHistoryDAO;
	@Resource private IDictationHistoryDAO dictationHistoryDAO;
	@Resource private ISelfDictationService selfDictationService;
	@Resource private IPhoneticPracticeService phoneticPracticeService;

	//	 ============== UI display data ================//
	private Dictation dictation;
	private long selectedDictationId;
	private boolean withIPA = false;
	private boolean withRandomCharacters = false;
	private String answer;
	private MemberDictationHistory memberDictationHistory;
	private DictationHistory dictationHistory;
	private PhoneticPractice practice;
	private ScoreBar scoreBar;
	private boolean showHistoryInputForm;
	private boolean recommended;


	// ============== Constructor ================//
	public DictationPracticeController() {
		scoreBar = new ScoreBar();
		scoreBar.setFullLength(SCOREBAR_FULLLENGTH);
		recommended = false;
	}

	//============== Functions ================//
	public String initCheck() {
		if (dictation == null) {
			return "/practice/selfdictation/input";
		}
		return "";
	}

	@Transactional
	public String authDictation() {
		final String logPrefix = "authDictation: ";
		logger.info(logPrefix + "START");

		if (dictation == null) return errorView;
		Member member = userSession.getMember();


		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		if (manageService.allowView(dictation, member)) {
			logger.info(logPrefix + "allow open dictation");
			return launchStart();
		} else if (dictation.isRequirePassword()) {
			String reqPwdMsg = bundle.getString("reqPwd");
			return checkPasswordController.launchInput(this, "launchStart", dictation, reqPwdMsg);
		} else {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("notAllowOpen"), null));
			return null;
		}
	}

	@Transactional
	public String launchStart() {
		final String logPrefix = "launchStart: ";
		logger.info(logPrefix + "START");

		if (dictation == null) return errorView;
		dictationDAO.attachSession(dictation);
		loadMemberDictationHistory();

		return startView;
	}

	private void loadMemberDictationHistory() {
		if (userSession.getMember() != null)
			memberDictationHistory = memberDictationHistoryDAO.loadByDictationMember(userSession.getMember(), dictation);

		if (memberDictationHistory != null) {
			logger.info("Preload vocab histories: size {}", memberDictationHistory.getVocabHistories().size());
		}
	}

	@Transactional
	public String start() {
		final String logPrefix = "start: ";
		logger.info(logPrefix + "START");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);

		dictationDAO.attachSession(dictation);
		practice = selfDictationService.generatePractice(dictation.getVocabs());

		if (practice == null || practice.getQuestions().size() <= 0) {
			logger.info(logPrefix + "no question generated");
			return errorView;
		}
		else
		{
			// Set unavailable IPA
			for (PhoneticQuestion question : practice.getQuestions()) {
				LanguageUtil.formatIPA(question, locale);
			}

			setScoreBar(0, 0); 			// set scoreBar
			return practiceView;
		}
	}

	@Transactional
	public String submitAnswer() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// Check practice have been create or not, if not created, call start
		if (practice == null) {
			logger.warn("submitAnswer: cannot find practice");
			return JSFUtil.redirect(errorView);
		}
		String result = phoneticPracticeService.checkAnswer(practice, answer);
		logger.info("submitAnswer: phoneticPracticeService.checkAnswer returned code: " + result);

		// update score bar
		if (IPhoneticPracticeService.CORRECT_ANSWER.equals(result))
			setScoreBar(practice.getMark()-1, practice.getMark());
		else
			setScoreBar(practice.getMark(), practice.getMark());

		answer = "";			// Clear answer field

		if (PhoneticPracticeService.INVALID_INPUT.equals(result)) return null;
		else if (PhoneticPracticeService.SYSTEM_ERROR.equals(result)) return JSFUtil.redirect(errorView);

		// Logic flow for practice completed
		if (practice.isFinish())
		{
			logger.info("submitAnswer: Finish Practice");
			return JSFUtil.redirect(finishDictation());
		}

		return null; // Continue Practice
	}

	@Transactional
	public String submitDictationHistory() {
		final String logPrefix = "submitDictationHistory: ";
		logger.info(logPrefix + "START");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		if (!"".equals(dictationHistory.getPracticerName()) && ValidationUtil.isMatch(dictationHistory.getPracticerName(),"[<>\"\']*")) {
			logger.info(logPrefix + "contain invalid characters");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("containInvalidChar"), null));
			return null;
		}
		if (!"".equals(dictationHistory.getPracticerSchool()) && ValidationUtil.isMatch(dictationHistory.getPracticerSchool(),"[<>\"\']*")) {
			logger.info(logPrefix + "contain invalid characters");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("containInvalidChar"), null));
			return null;
		}

		dictationHistory.setDictation(dictationDAO.merge(dictationHistory.getDictation()));
		dictationHistoryDAO.persist(dictationHistory);
		showHistoryInputForm = false;
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("recordSaved"), null));
		return null;
	}

	@Transactional
	public String finishDictation() {
		dictationDAO.attachSession(dictation);
		memberDAO.attachSession(userSession.getMember());
		memberDictationHistory = selfDictationService.updateMemberDictationHistory(dictation, userSession.getMember(), practice);
		dictationHistory = selfDictationService.createDictationHistory(dictation, userSession.getMember(), practice);
		if (memberDictationHistory == null) showHistoryInputForm = true;
		setScoreBar(0, practice.getMark());
		recommended = false;
		return resultView;
	}

//	public String rate() {
//		final String logPrefix = "rate: ";
//		logger.info(logPrefix + "START");
//
//		if (dictation == null) return errorView;
//		if (ratingValue < 0) ratingValue = BASE_RATING;
//		logger.info(logPrefix + "rate [" + ratingValue + "] to dictation[" + dictation.getId() + "]");
//		manageService.rateDictation(dictation, ratingValue);
//
//		return resultView;
//	}

	/**
	 * Used for Ajax call to add 1 recommendation to the dictation
	 */
	@Transactional
	public String recommendDictation() {
		final String logPrefix = "recommendDictation: ";
		logger.info(logPrefix + "START");

		if (dictation == null) return errorView;
		dictation = (Dictation) dictationDAO.attachSession(dictation);
		dictation.setTotalRecommended(dictation.getTotalRecommended() + 1);
		dictationDAO.persist(dictation);
		recommended = true;

		return null;
	}


	//	============== Getter Function ================//
	public String getStartDictationURL() {	return startDictationURL;}

	//	============== Supporting Function ================//
	private void setScoreBar(int startIdx, int endIdx) {
		int startLength = (int) ((double)startIdx / (double)practice.getTotalQuestions() * SCOREBAR_FULLLENGTH);
		int endLength = (int) ((double)endIdx / (double)practice.getTotalQuestions() * SCOREBAR_FULLLENGTH);
		if (startLength < 0) startLength = 0;

		logger.info("setScoreBar: startLength[" + startLength + "], endLength[" + endLength + "]");
	}

	//	 ============== Setter / Getter ================//
	public void setManageService(IDictationManageService manageService) {this.manageService = manageService;}
	public void setCheckPasswordController(CheckPasswordController checkPasswordController) {this.checkPasswordController = checkPasswordController;}
	public void setMemberDictationHistoryDAO(IMemberDictationHistoryDAO memberDictationHistoryDAO) {this.memberDictationHistoryDAO = memberDictationHistoryDAO;	}
	public void setSelfDictationService(ISelfDictationService selfDictationService) {this.selfDictationService = selfDictationService;}
	public void setPhoneticPracticeService(IPhoneticPracticeService phoneticPracticeService) {this.phoneticPracticeService = phoneticPracticeService;}
	public void setDictationHistoryDAO(IDictationHistoryDAO dictationHistoryDAO) {this.dictationHistoryDAO = dictationHistoryDAO;}
	public void setMemberDAO(IMemberDAO memberDAO) {this.memberDAO = memberDAO;}
	public void setDictationDAO(IDictationDAO dictationDAO) {
		this.dictationDAO = dictationDAO;
		setEslDao(dictationDAO);
	}

	public Dictation getDictation() {return dictation;}
	public void setDictation(Dictation dictation) {	this.dictation = dictation;	}

	public boolean isWithIPA() { return withIPA; }
	public void setWithIPA(boolean withIPA)  { this.withIPA = withIPA; }

	public boolean isWithRandomCharacters()	{return withRandomCharacters;}
	public void setWithRandomCharacters(boolean withRandomCharacters) {	this.withRandomCharacters = withRandomCharacters;}

	public ScoreBar getScoreBar() {	return scoreBar;}
	public void setScoreBar(ScoreBar scoreBar) {this.scoreBar = scoreBar;}

	public long getSelectedDictationId() {return selectedDictationId;}
	public void setSelectedDictationId(long selectedDictationId) {this.selectedDictationId = selectedDictationId;}

	public MemberDictationHistory getMemberDictationHistory() {return memberDictationHistory;}
	public void setMemberDictationHistory(MemberDictationHistory memberDictationHistory) {this.memberDictationHistory = memberDictationHistory;}

	public String getAnswer() {return answer;}
	public void setAnswer(String answer) {this.answer = answer;}

	public PhoneticPractice getPractice() {	return practice;}
	public void setPractice(PhoneticPractice practice) {this.practice = practice;}

	public DictationHistory getDictationHistory() {return dictationHistory;}
	public void setDictationHistory(DictationHistory dictationHistory) {this.dictationHistory = dictationHistory;}

	public boolean isShowHistoryInputForm() {return showHistoryInputForm;}
	public void setShowHistoryInputForm(boolean showHistoryInputForm) {	this.showHistoryInputForm = showHistoryInputForm;}

	public boolean isRecommended() {return recommended;}
	public void setRecommended(boolean isRecommended) {this.recommended = isRecommended;}

	@Override
	public Dictation getUserCreatedPractice() {
		return this.dictation;
	}

}
