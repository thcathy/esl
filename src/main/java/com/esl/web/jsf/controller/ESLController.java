package com.esl.web.jsf.controller;

import com.esl.web.model.ErrorPage;
import com.esl.web.model.UserSession;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Locale;

/*
 * Root Controller containing variable across all controllers
 */
public abstract class ESLController implements Serializable {
	protected final String indexView = "/index";
	protected final String errorView = "/error";
	protected final String messageView = "/public/message";

	// Session variable
	@Resource protected UserSession userSession;
	@Resource protected ErrorPage errorPage;

	// ============== Setter / Getter ================//
	public UserSession getUserSession() {return userSession;}
	public void setUserSession(UserSession userSession) {this.userSession = userSession;}

	public ErrorPage getErrorPage() {return errorPage;}
	public void setErrorPage(ErrorPage errorPage) {	this.errorPage = errorPage;	}

	// ============== Getter Function ===================//
	/**
	 * Use for jsp, To refresh all UI string to new language
	 */
	public String getInitLanguage() { return ""; }

	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public Locale getLocale() {
		return (userSession.getLocale()==null)? getFacesContext().getViewRoot().getLocale() : userSession.getLocale();
	}

	// ============== Constructor ================//
	public ESLController() {}
}
