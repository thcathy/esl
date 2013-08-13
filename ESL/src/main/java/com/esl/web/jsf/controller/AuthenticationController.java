package com.esl.web.jsf.controller;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.dao.IMemberDAO;
import com.esl.model.Member;
import com.esl.service.*;
import com.esl.util.ValidationUtil;
import com.esl.web.jsf.controller.member.SummaryController;

@Controller
@Scope("session")
public class AuthenticationController extends ESLController {
	public static String SESSION_ID_COOKIE_KEY = "loginedSessionId";

	private static Logger logger = Logger.getLogger("ESL");
	private final String bundleName = "messages.Authentication";
	private static final String wrongPasswordView = "/error/wrongpassword";

	// Supporting instance
	@Resource private IMembershipService membershipService;
	@Resource private SummaryController summaryController;
	@Resource private IMailService mailService;
	@Resource private IMemberDAO memberDAO;

	private String inputUserId;
	private String inputPassword;
	private boolean authenticated = false;
	private boolean saveSession = false;

	// ============== Setter / Getter ================//
	public void setMembershipService(IMembershipService membershipService) {this.membershipService = membershipService;}
	public void setMailService(IMailService mailService) {this.mailService = mailService; }
	public void setMemberDAO(IMemberDAO memberDAO) {this.memberDAO = memberDAO;}

	public String getInputUserId() {return inputUserId;	}
	public void setInputUserId(String inputUserId) {this.inputUserId = inputUserId;	}

	public String getInputPassword() {return inputPassword;	}
	public void setInputPassword(String inputPassword) {this.inputPassword = inputPassword;	}

	public boolean isAuthenticated() {return authenticated;	}
	public void setAuthenticated(boolean authenticated) {	this.authenticated = authenticated;	}

	public boolean isSaveSession() {return saveSession;}
	public void setSaveSession(boolean saveSession) {this.saveSession = saveSession;}

	public void setSummaryController(SummaryController summaryController) {	this.summaryController = summaryController;	}

	// ============== Constructor ================//
	public AuthenticationController() {}

	// ============== Functions ================//
	public String login() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		userSession.setLocale(facesContext.getViewRoot().getLocale());

		// validate user input
		if (!validateLoginForm(facesContext, bundle)) {
			errorPage.setTitle(bundle.getString("loginErrorTitle"));
			return errorView;
		}

		inputUserId = inputUserId.toLowerCase();
		Member member = new Member();
		member.setUserId(inputUserId);
		member.setPIN(inputPassword);
		member.setCreatedDate(null);

		String result = membershipService.login(member);
		logger.info("login: returned member: " + membershipService.getMember());
		logger.info("login: returned status code: " + result);

		// Login Succeed
		if (result.equals(MembershipService.LOGIN_SUCCEED))
		{
			authenticated = true;
			inputUserId = "";
			userSession.setMember(membershipService.getMember());

			if (isSaveSession()) {
				// Set session id
				HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
				String sessionId = session.getId();
				userSession.getMember().setLoginedSessionId(sessionId);
				memberDAO.persist(userSession.getMember());

				HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
				Cookie cookie = new Cookie(SESSION_ID_COOKIE_KEY, sessionId);
				cookie.setMaxAge(2592000); // 30 days
				response.addCookie(cookie);
			}
			return summaryController.index();
		}
		else if (result.equals(MembershipService.USER_ID_PASSWORD_NOT_MATCH)) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("UserIDPasswordNotMatch"), null));
			errorPage.setTitle(bundle.getString("loginErrorTitle"));
			return wrongPasswordView;
		}

		// Flow error
		return errorView;
	}

	public String getCookieAuth() {
		final String logPrefix = "getCookieAuth: ";
		logger.info(logPrefix + "START");

		if (userSession.getMember() != null) {
			logger.info("user already logined");
			return "";
		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Cookie cookies[] = ((HttpServletRequest)facesContext.getExternalContext().getRequest()).getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(SESSION_ID_COOKIE_KEY)) {
					String cookieSessionId = c.getValue();
					logger.info(logPrefix + "cookie session id [" + cookieSessionId + "]");
					Member m = memberDAO.getMemberByLoginedSessionID(cookieSessionId);
					logger.info(logPrefix + "found member by id [" + m + "]");
					if (m != null) {
						userSession.setMember(m);
						authenticated = true;
						return "";
					}
				}
			}
			logger.info("No cookie with key [" + SESSION_ID_COOKIE_KEY + "] found!");
		} else {
			logger.info("No cookie found!");
		}
		return "";
	}

	public String logout() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		logger.info("logout: UserId:" + userSession.getMember());
		userSession.getMember().setLoginedSessionId(null);
		memberDAO.persist(userSession.getMember());
		session.invalidate();

		// Set Locale of the previous session
		facesContext.getViewRoot().setLocale(locale);

		return indexView;
	}

	public String forgetPassword() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);

		logger.info("forgetPassword: START");
		Member member = mailService.forgetPassword(inputUserId, locale);
		String message;

		if (member != null) {
			logger.info("forgetPassword: member[" + inputUserId + "] password is sent");
			message = MessageFormat.format(bundle.getString("forgetPINEmailSent"), new Object[] {member.getEmailAddress()});
		} else {
			logger.info("forgetPassword: member[" + inputUserId + "] do not found");
			message = MessageFormat.format(bundle.getString("forgetPINUserNotFound"), new Object[] {inputUserId});
		}

		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
		return messageView;
	}

	// ============== Supporting Functions ================//

	// login form validation
	private boolean validateLoginForm(FacesContext context, ResourceBundle bundle) {
		boolean isValid = true;

		// empty input checking
		if (inputUserId == null || inputPassword == null || inputUserId.equals("") || inputPassword.equals("")) {
			logger.info("validateLoginForm: EmptyInput");
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("EmptyInput") ,null));
			return false;
		}

		if (!ValidationUtil.isAlphaNumeric(inputUserId)) {
			logger.info("validateLoginForm: InvalidUsername");
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("InvalidUsername"), null));
			isValid = false;
		}
		if (ValidationUtil.isContainInvalidCharacters(inputPassword)) {
			logger.info("validateLoginForm: InvalidPassword");
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("InvalidPassword"), null));
			isValid = false;
		}

		return isValid;
	}
}
