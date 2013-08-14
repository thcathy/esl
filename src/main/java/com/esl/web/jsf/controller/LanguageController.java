package com.esl.web.jsf.controller;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("session")
public class LanguageController extends ESLController implements Serializable {
	public static String LOCALE_PARAM = "locale";

	// ============== Setter / Getter ================//

	public String getImagesPath() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		return request.getContextPath() + "/images/" + facesContext.getViewRoot().getLocale().toString();
	}

	// ============== Functions ================//
	public String toZH() {
		Logger.getLogger("ESL").info("toZH");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getViewRoot().setLocale(new Locale("zh"));
		userSession.setLocale(new Locale("zh"));
		return "";
	}

	public String toEN() {
		Logger.getLogger("ESL").info("toEN");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getViewRoot().setLocale(new Locale("en"));
		userSession.setLocale(new Locale("en"));
		return "";
	}

	public String toZH_CN() {
		Logger.getLogger("ESL").info("toZH_CN");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getViewRoot().setLocale(new Locale("zh-cn"));
		userSession.setLocale(new Locale("zh-cn"));
		return "";
	}
}