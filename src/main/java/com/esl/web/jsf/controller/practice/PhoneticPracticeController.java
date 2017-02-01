package com.esl.web.jsf.controller.practice;

import com.esl.dao.IGradeDAO;
import com.esl.dao.IMemberDAO;
import com.esl.dao.IPracticeResultDAO;
import com.esl.model.*;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.service.practice.ITopResultService;
import com.esl.service.practice.PhoneticPracticeService;
import com.esl.util.JSFUtil;
import com.esl.web.jsf.controller.AuthenticationController;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.jsf.controller.member.MemberWordController;
import com.esl.web.model.UserSession;
import com.esl.web.util.LanguageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.*;

@Controller
@Scope("session")
public class PhoneticPracticeController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(PhoneticPracticeController.class);
	private final String bundleName = "messages.practice.PhoneticPractice";
	private String viewPrefix = "/practice/phoneticpractice/";
	private String indexView = viewPrefix + "index";
	private String practiceView = viewPrefix + "practice";
	private String resultView = viewPrefix + "result";

	// UI Data
	private String selectedGrade = "";
	private String answer = "";
	private TopResult scoreRanking;
	private TopResult rateRanking;
	private PracticeResult currentGradeResult;
	private boolean isLevelUp = false;

	// Supporting classes
	@Resource private IMemberDAO memberDAO;
	@Resource private IPhoneticPracticeService phoneticPracticeService;
	@Resource private IPracticeResultDAO practiceResultDAO;
	@Resource private ITopResultService topResultService;
	@Resource private IGradeDAO gradeDAO;

	private PhoneticPractice practice;
	@Resource private AuthenticationController authenticationController = null;
	@Resource private MemberWordController memberWordController = null;

	// UI Component
	private HtmlCommandButton practiceCommand;

	// ============== Constructor ================//
	public PhoneticPracticeController() {}

	// ============== Setter / Getter ================//
	public void setMemberDAO(IMemberDAO memberDAO) {this.memberDAO = memberDAO;}
	public void setPhoneticPracticeService(IPhoneticPracticeService phoneticPracticeService) {this.phoneticPracticeService = phoneticPracticeService;}
	public void setAuthenticationController(AuthenticationController authenticationController) {this.authenticationController = authenticationController;}
	public void setPracticeResultDAO(IPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;	}
	public void setTopResultService(ITopResultService topResultService) {this.topResultService = topResultService; }
	public void setMemberWordController(MemberWordController memberWordController) {this.memberWordController = memberWordController; }
	public void setGradeDAO(IGradeDAO gradeDAO) {this.gradeDAO = gradeDAO;}

	@Override
	public UserSession getUserSession() {return userSession;}
	@Override
	public void setUserSession(UserSession userSession) {this.userSession = userSession;}

	public String getSelectedGrade() {	return selectedGrade;}
	public void setSelectedGrade(String selectedGrade) {this.selectedGrade = selectedGrade;	}

	public HtmlCommandButton getPracticeCommand() {	return practiceCommand;	}
	public void setPracticeCommand(HtmlCommandButton practiceCommand) {	this.practiceCommand = practiceCommand;	}

	public String getAnswer() {	return answer;	}
	public void setAnswer(String answer) {this.answer = answer;}

	public boolean isLevelUp() {return isLevelUp;}
	public void setLevelUp(boolean isLevelUp) {	this.isLevelUp = isLevelUp;}

	public PhoneticPractice getPractice() {	return practice;}
	public void setPractice(PhoneticPractice practice) {this.practice = practice;}

	public TopResult getRateRanking() {	return rateRanking;}
	public void setRateRanking(TopResult rateRanking) {	this.rateRanking = rateRanking;	}

	public TopResult getScoreRanking() {return scoreRanking;}
	public void setScoreRanking(TopResult scoreRanking) {this.scoreRanking = scoreRanking;	}

	public PracticeResult getCurrentGradeResult() {	return currentGradeResult;	}
	public void setCurrentGradeResult(PracticeResult currentGradeResult) {	this.currentGradeResult = currentGradeResult;}

	//	 ============== Getter Functions ================//

	// Return grades available to the user
	public List<SelectItem> getAvailableGrades() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		String userId = "";

		if (authenticationController.isAuthenticated()) userId = userSession.getMember().getUserId();
		List<Grade> allGrades = gradeDAO.getAll();
		List<Grade> availableGrades = phoneticPracticeService.getUserAvailableGrades(userId);
		List<SelectItem> items = new ArrayList<SelectItem>(allGrades.size());

		for (Grade grade : allGrades) {
			LanguageUtil.formatGradeDescription(grade, locale);
			SelectItem item = new SelectItem(grade.getTitle(), grade.getDescription());
			if (!availableGrades.contains(grade)) item.setDisabled(true);
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
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		logger.info("getInitResultLanguage: Format obj for :" + locale);

		LanguageUtil.formatGradeDescription(practice.getGrade(), locale).getDescription();
		if (userSession.getMember() != null) LanguageUtil.formatGradeDescription(userSession.getMember().getGrade(), locale);
		return "";
	}

	// ============== Functions ================//
	public String start() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);

		// Check for resubmit, i.e. already have practice object
		if (practice != null && !practice.isFinish()) {
			logger.info("start: PRACTICE_NOT_COMPLETED");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("practiceCompleteLastPractice"), null));
			return practiceView;
		}
		logger.info("start: selectedGrade: " + selectedGrade);
		practice = phoneticPracticeService.generatePractice(userSession.getMember(), selectedGrade);

		if (practice != null) {
			memberWordController.setSavedQuestion(phoneticPracticeService.getUnSavedMap(practice));
			return practiceView;
		}

		// General Error
		logger.info("start: SYSTEM_ERROR");
		return "/error";
	}

	public String submitAnswer() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String locale = facesContext.getViewRoot().getLocale().toString();

		// Check practice have been create or not, if not created, call start
		if (practice == null) {
			logger.info("submitAnswer: cannot find practice");
			return JSFUtil.redirect(start());
		}

		PhoneticQuestion question = practice.getCurrentQuestionObject();
		String result = phoneticPracticeService.checkAnswer(practice, answer);
		logger.info("submitAnswer: phoneticPracticeService.checkAnswer returned code: " + result);
		// update score bar
		if (IPhoneticPracticeService.CORRECT_ANSWER.equals(result)) {
			// update socre card
			if (userSession.getMember() != null)
				phoneticPracticeService.updateScoreCard(userSession.getMember(), new java.sql.Date((new Date()).getTime()), true, question);
		}

		answer = "";			// Clear answer field

		if (PhoneticPracticeService.INVALID_INPUT.equals(result))
			return null;
		else if (PhoneticPracticeService.SYSTEM_ERROR.equals(result))
			return JSFUtil.redirect(errorView);

		// Logic flow for practice completed
		if (practice.isFinish())
		{
			result = completedPractice();
			logger.info("submitAnswer: completedPractice returned code: " + result);
			if (PhoneticPracticeService.SAVE_HISTORY_COMPLETED.equals(result)) {
				return JSFUtil.redirect(resultView);
			}
			return JSFUtil.redirect(errorView);
		}

		// Continue Practice
		return null;
	}

	// ============== Supporting Functions ================//

	// process when completing the practice
	private String completedPractice() {
		Member member = userSession.getMember();
		String result = phoneticPracticeService.saveHistory(practice);
		Grade grade = practice.getGrade();

		if (member != null) {
			// retrieve ranking of the practiced grade
			scoreRanking = topResultService.getResultListByMemberGrade(TopResult.OrderType.Score, PracticeResult.PHONETICPRACTICE, member, grade);
			rateRanking = topResultService.getResultListByMemberGrade(TopResult.OrderType.Rate, PracticeResult.PHONETICPRACTICE, member, grade);

			// Check Level Up
			currentGradeResult = practiceResultDAO.getPracticeResult(member, grade, PracticeResult.PHONETICPRACTICE);
			String checkLevelUp = phoneticPracticeService.checkLevelUp(member, practice, currentGradeResult);
			logger.info("completedPractice: phoneticPracticeService.checkLevelUp returned code: " + checkLevelUp);

			if (PhoneticPracticeService.LEVEL_UP.equals(checkLevelUp))
				isLevelUp = true;
			else
				isLevelUp = false;
		}

		return result;
	}

}

