package com.esl.web.jsf.controller;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.web.model.PasswordRequire;

@Controller
@Scope("session")
public class CheckPasswordController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(CheckPasswordController.class);
	private static int MAX_RETRY = 1;

	private final String bundleName = "messages.GeneralMessages";
	private final String inputView = "/public/inputpassword";


	private String nextMethod;
	private ESLController caller;
	private PasswordRequire entity;
	private String inputMsg;
	private int retryTimes;

	// UI
	private String inputPassword = "";

	@Value("${CheckPassword.MaxRetry}") public void setMaxRetry(int maxRetry) {this.MAX_RETRY = maxRetry; }


	// ============== Constructor ================//
	public CheckPasswordController() {}

	// ============== Functions ================//
	public String launchInput(ESLController caller, String nextMethod, PasswordRequire checkObj, String inputMsg) {
		final String logPrefix = "launchInput: ";
		logger.info(logPrefix + "START");
		if (caller == null || nextMethod == null || checkObj == null || checkObj.getPassword() == null) {
			logger.info(logPrefix + "Not enough object set");
			return errorView;
		}
		this.caller = caller;
		this.nextMethod = nextMethod;
		entity = checkObj;
		this.inputMsg = inputMsg;
		logger.info(logPrefix + "Called to [" + caller.getClass().getName() + "." + nextMethod  + "]");
		retryTimes = 0;
		return inputView;
	}

	public String checkPassword() {
		final String logPrefix = "checkPassword: ";
		logger.info(logPrefix + "START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// Check PIN with confirmed PIN
		if (!entity.getPassword().equals(inputPassword)) {
			retryTimes++;
			if (retryTimes > MAX_RETRY) {
				logger.info(logPrefix + "over retry limit [" + MAX_RETRY + "]");
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("overMaxRetry"), null));
			} else {
				logger.info(logPrefix + "input PIN different");
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("passwordIncorrect"), null));
			}

			return null;
		}

		logger.info(logPrefix + "Called to [" + caller.getClass().getName() + "." + nextMethod  + "]");
		java.lang.reflect.Method method;
		try {
			method = caller.getClass().getMethod(nextMethod, null);
			return (String) method.invoke(caller, null);
		} catch (Exception e) {
			logger.warn("Error", e);
			return errorView;
		}
	}

	// ================== Supporting Function ==================== //

	// ============== Setter / Getter ================//

	public String getInputPassword() {return inputPassword;}
	public void setInputPassword(String inputPassword) {this.inputPassword = inputPassword;}


	public String getInputMsg() {return inputMsg;}
	public void setInputMsg(String inputMsg) {this.inputMsg = inputMsg;}

	public PasswordRequire getEntity() {return entity;}
	public void setEntity(PasswordRequire entity) {this.entity = entity;}



}
