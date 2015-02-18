package com.esl.web.jsf.controller.dictation;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.dao.dictation.IDictationDAO;
import com.esl.entity.dictation.*;
import com.esl.model.Member;
import com.esl.service.dictation.IDictationManageService;
import com.esl.web.jsf.controller.ESLController;

@Controller
@Scope("session")
public class DictationManageController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(DictationManageController.class);
	private static final String bundleName = "messages.member.Dictation";
	private static final String mainView = "/member/dictation/main";
	private static final String dictationView = "/member/dictation/dictation";
	private static final String passwordInputView = "/member/dictation/passwordinput";
	private static final String selfDictationInputView = "/practice/selfdictation/input";


	public static int maxLastPracticed = 10;
	public static int maxHistories = 20;

	//	 Supporting instance
	@Resource private IDictationManageService manageService;
	@Resource private IDictationDAO dictationDAO;

	@Value("${Dictation.MaxLastPracticed}") public void setMaxLastPracticed(int maxLastPracticed) { DictationManageController.maxLastPracticed = maxLastPracticed;}
	@Value("${Dictation.MaxHistories}") public void setMaxHistories(int maxHistories) {DictationManageController.maxHistories = maxHistories;}


	//	 ============== UI display data ================//

	// for main page
	private List<Dictation> selfCreated;
	private List<MemberDictationHistory> lastPracticed;
	private Dictation selectedDictation;
	private List<DictationHistory> dictationHistories;

	// for password input page
	private String inputPassword;

	//============== Functions ================//

	/**
	 * Open main page
	 */
	public String launchMain() {
		final String logTitle = "launchMain: ";
		logger.info(logTitle + "START");

		// Check user login or not
		if (userSession.getMember() == null) {
			logger.info(logTitle + "Member is null, direct to self dictation input page");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("signUp"), null));

			return selfDictationInputView;
		}

		Member member = userSession.getMember();
		selfCreated = manageService.getDictationsByMember(member);
		lastPracticed = manageService.getDictationsHistoriesByMember(member, maxLastPracticed);

		for (Dictation d : selfCreated) {
			logger.info(logTitle + "dictation[" + d.getTitle() + "] have vocab[" + d.getVocabs().size() + "]");
		}

		return mainView;
	}

	/**
	 * Logic when click menu bar
	 */
	public String launchMyDictation() {
		if (userSession.getMember() == null) return selfDictationInputView;
		else return launchMain();
	}

	/**
	 * Open single dictation view
	 */
	public String launchDictation() {
		final String logTitle = "launchDictation: ";
		logger.info(logTitle + "START");
		logger.info(logTitle + "show dictation[" + selectedDictation.getId() + "]");

		dictationDAO.attachSession(selectedDictation);
		if (manageService.allowView(selectedDictation, userSession.getMember())) {
			logger.info(logTitle + "allow to view");
			dictationHistories = manageService.getDictationsHistoriesByDictation(selectedDictation, maxHistories);
			logger.info(logTitle + "Histories returned [" + dictationHistories.size() + "]");
			return dictationView;
		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("notAllowOpen"), null));

		return null;
	}

	/**
	 * Remove a dictation
	 */
	public String removeDictation() {
		final String logPrefix = "removeDictation: ";
		logger.info(logPrefix + "START");
		logger.info(logPrefix + "remove dictation(" + selectedDictation.getId() + ")");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		dictationDAO.attachSession(selectedDictation);

		// check edit right
		if (!manageService.allowEdit(selectedDictation, userSession.getMember())) {
			logger.info(logPrefix + "not allow edit");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("notAllowDelete"), null));
			return null;
		}
		dictationDAO.remove(selectedDictation);
		String successStr = MessageFormat.format(bundle.getString("dictationDeleted"), selectedDictation.getTitle());
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, successStr, null));
		selectedDictation = null;
		return mainView;
	}

	public String checkPassword() {
		final String logTitle = "checkPassword: ";
		logger.info(logTitle + "START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		if (inputPassword.equals(selectedDictation.getPassword())) {
			logger.info(logTitle + "password correct");
			return dictationView;
		} else {
			logger.info(logTitle + "input password[" + inputPassword + "] incorrect to dictation id[" + selectedDictation.getId() + "]");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("incorrectPwd"), null));
			return null;
		}
	}

	public String launchInputPassword() {
		inputPassword = "";
		return passwordInputView;
	}

	//	============== Getter Function ================//
	public String getInitMain() {
		final String logTitle = "getInitMain: ";
		logger.info(logTitle + "START");
		return "";
	}

	//	============== Supporting Function ================//


	//	 ============== Setter / Getter ================//

	public List<Dictation> getSelfCreated() {return selfCreated;}
	public void setSelfCreated(List<Dictation> selfCreated) {this.selfCreated = selfCreated;}

	public List<MemberDictationHistory> getLastPracticed() {return lastPracticed;}
	public void setLastPracticed(List<MemberDictationHistory> lastPracticed) {this.lastPracticed = lastPracticed;}

	public Dictation getSelectedDictation() {return selectedDictation;}
	public void setSelectedDictation(Dictation selectedDictation) {	this.selectedDictation = selectedDictation;}

	public String getInputPassword() {return inputPassword;}
	public void setInputPassword(String inputPassword) {this.inputPassword = inputPassword;}

	public List<DictationHistory> getDictationHistories() {return dictationHistories;}
	public void setDictationHistories(List<DictationHistory> dictationHistories) {this.dictationHistories = dictationHistories;}

}
