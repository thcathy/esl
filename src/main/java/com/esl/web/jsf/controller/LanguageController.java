package com.esl.web.jsf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Locale;

@Controller
@Scope("session")
public class LanguageController extends ESLController implements Serializable {
	public static Logger log = LoggerFactory.getLogger(LanguageController.class);
	public static String LOCALE_PARAM = "locale";

	// ============== Setter / Getter ================//

	public String getImagesPath() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		return request.getContextPath() + "/images/" + facesContext.getViewRoot().getLocale().toString();
	}

	// ============== Functions ================//
	public String toZH() {
		log.info("toZH");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getViewRoot().setLocale(new Locale("zh"));
		userSession.setLocale(new Locale("zh"));
		return "";
	}

	public String toEN() {
		log.info("toEN");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getViewRoot().setLocale(new Locale("en"));
		userSession.setLocale(new Locale("en"));
		return "";
	}

	public String toZH_CN() {
		log.info("toZH_CN");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getViewRoot().setLocale(new Locale("zh-cn"));
		userSession.setLocale(new Locale("zh-cn"));
		return "";
	}
}