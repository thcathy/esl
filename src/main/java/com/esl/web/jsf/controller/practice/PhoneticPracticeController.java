package com.esl.web.jsf.controller.practice;

import com.esl.dao.IMemberDAO;
import com.esl.dao.IPracticeResultDAO;
import com.esl.entity.event.UpdatePracticeHistoryEvent;
import com.esl.entity.practice.MemberScore;
import com.esl.entity.practice.MemberScoreRanking;
import com.esl.enumeration.ESLPracticeType;
import com.esl.enumeration.VocabDifficulty;
import com.esl.model.Member;
import com.esl.model.PhoneticPractice;
import com.esl.model.PhoneticQuestion;
import com.esl.service.history.RankingService;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.service.practice.PhoneticPracticeService;
import com.esl.service.practice.PhoneticQuestionService;
import com.esl.util.JSFUtil;
import com.esl.web.jsf.controller.AuthenticationController;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.jsf.controller.member.MemberWordController;
import com.esl.web.model.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.bus.Event;
import reactor.bus.EventBus;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.esl.service.practice.IPhoneticPracticeService.SAVE_HISTORY_COMPLETED;

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
	//private String selectedGrade = "";
	private String answer = "";
	//private PracticeResult currentGradeResult;
	private boolean isLevelUp = false;
	private List<VocabDifficulty> allDifficulty = Arrays.asList(VocabDifficulty.values());
	private VocabDifficulty selectedDifficulty;
	private MemberScoreRanking thisMonthRanking;
	private MemberScoreRanking allTimesRanking;


	// Supporting classes
	@Resource private IMemberDAO memberDAO;
	@Resource private IPhoneticPracticeService phoneticPracticeService;
	@Resource private IPracticeResultDAO practiceResultDAO;
	//@Resource private IGradeDAO gradeDAO;

	private PhoneticPractice practice;
	@Resource private AuthenticationController authenticationController = null;
	@Resource private MemberWordController memberWordController = null;
	@Autowired EventBus eventBus;
	@Autowired PhoneticQuestionService phoneticQuestionService;
	@Autowired RankingService rankingService;

	// ============== Constructor ================//
	public PhoneticPracticeController() {}

	// ============== Setter / Getter ================//
	public void setMemberDAO(IMemberDAO memberDAO) {this.memberDAO = memberDAO;}
	public void setPhoneticPracticeService(IPhoneticPracticeService phoneticPracticeService) {this.phoneticPracticeService = phoneticPracticeService;}
	public void setAuthenticationController(AuthenticationController authenticationController) {this.authenticationController = authenticationController;}
	public void setPracticeResultDAO(IPracticeResultDAO practiceResultDAO) {this.practiceResultDAO = practiceResultDAO;	}
	public void setMemberWordController(MemberWordController memberWordController) {this.memberWordController = memberWordController; }
	//public void setGradeDAO(IGradeDAO gradeDAO) {this.gradeDAO = gradeDAO;}

	@Override
	public UserSession getUserSession() {return userSession;}
	@Override
	public void setUserSession(UserSession userSession) {this.userSession = userSession;}

	//public String getSelectedGrade() {	return selectedGrade;}
	//public void setSelectedGrade(String selectedGrade) {this.selectedGrade = selectedGrade;	}

	public String getAnswer() {	return answer;	}
	public void setAnswer(String answer) {this.answer = answer;}

	public boolean isLevelUp() {return isLevelUp;}
	public void setLevelUp(boolean isLevelUp) {	this.isLevelUp = isLevelUp;}

	public PhoneticPractice getPractice() {	return practice;}
	public void setPractice(PhoneticPractice practice) {this.practice = practice;}

	//public PracticeResult getCurrentGradeResult() {	return currentGradeResult;	}
	//public void setCurrentGradeResult(PracticeResult currentGradeResult) {	this.currentGradeResult = currentGradeResult;}

	public List<VocabDifficulty> getAllDifficulty() { return allDifficulty; }
	public void setAllDifficulty(List<VocabDifficulty> allDifficulty) { this.allDifficulty = allDifficulty;	}

	public VocabDifficulty getSelectedDifficulty() { return selectedDifficulty; }
	public void setSelectedDifficulty(VocabDifficulty selectedDifficulty) {	this.selectedDifficulty = selectedDifficulty; }

	public int getStarEarned() {
		return selectedDifficulty.weight * practice.getMark();
	}

	public MemberScoreRanking getThisMonthRanking() {return thisMonthRanking;}

	public MemberScoreRanking getAllTimesRanking() {return allTimesRanking;}

	// ============== Functions ================//
	public String initCheck() {
		if (practice == null) {
			return indexView;
		}
		return "";
	}

	@Transactional
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
	//	logger.info("start: selectedGrade: " + selectedGrade);
		logger.info("start: selectedDifficulty: {}", selectedDifficulty);
		practice = phoneticPracticeService.generatePractice(selectedDifficulty);

		if (practice != null) {
			memberWordController.setSavedQuestion(phoneticPracticeService.getUnSavedMap(practice));
			return practiceView;
		}

		// General Error
		logger.info("start: SYSTEM_ERROR");
		return "/error";
	}

	@Transactional
	public String submitAnswer() {
		// Check practice have been create or not, if not created, call start
		if (practice == null) {
			logger.info("submitAnswer: cannot find practice");
			return JSFUtil.redirectToJSF(start());
		}

		PhoneticQuestion question = practice.getCurrentQuestionObject();
		String result = phoneticPracticeService.checkAnswer(practice, answer);
		logger.info("submitAnswer: phoneticPracticeService.checkAnswer returned code: " + result);

		submitUpdateHistoryEventIfNeeded(result, question);

		answer = "";			// Clear answer field

		if (PhoneticPracticeService.INVALID_INPUT.equals(result))
			return null;
		else if (PhoneticPracticeService.SYSTEM_ERROR.equals(result))
			return JSFUtil.redirectToJSF(errorView);

		// Logic flow for practice completed
		if (practice.isFinish())
		{
			result = completedPractice();
			logger.info("submitAnswer: completedPractice returned code: " + result);
			if (SAVE_HISTORY_COMPLETED.equals(result)) {
				return JSFUtil.redirectToJSF(resultView);
			}
			return JSFUtil.redirectToJSF(errorView);
		}

		// Continue Practice
		return null;
	}

	public long getTotalQuestion() {
		return phoneticQuestionService.getTotalQuestion();
	}

	private void submitUpdateHistoryEventIfNeeded(String result, PhoneticQuestion question) {
		if (userSession.getMember() == null) return;

		eventBus.notify("addHistory",
				Event.wrap(new UpdatePracticeHistoryEvent(userSession.getMember(),
						ESLPracticeType.PhoneticPractice,
						question,
						IPhoneticPracticeService.CORRECT_ANSWER.equals(result),
						IPhoneticPracticeService.CORRECT_ANSWER.equals(result) ? selectedDifficulty.weight : 0))
		);
	}

	// ============== Supporting Functions ================//

	// process when completing the practice
	private String completedPractice() {
		Member member = userSession.getMember();
		//String result = phoneticPracticeService.saveHistory(practice);
		//Grade grade = practice.getGrade();

		if (member != null) {
			CompletableFuture<MemberScoreRanking> thisMonth = rankingService.myScoreRanking(member, MemberScore.thisMonth());
			CompletableFuture<MemberScoreRanking> allTimes = rankingService.myScoreRanking(member, MemberScore.allTimesMonth());
			thisMonthRanking = thisMonth.join();
			allTimesRanking = allTimes.join();

			// Check Level Up
			//currentGradeResult = practiceResultDAO.getPracticeResult(member, grade, PracticeResult.PHONETICPRACTICE);
			//String checkLevelUp = phoneticPracticeService.checkLevelUp(member, practice, currentGradeResult);
			//logger.info("completedPractice: phoneticPracticeService.checkLevelUp returned code: " + checkLevelUp);

			//if (PhoneticPracticeService.LEVEL_UP.equals(checkLevelUp))
			//	isLevelUp = true;
			//else
			//	isLevelUp = false;
		}

		return SAVE_HISTORY_COMPLETED;
	}

}

