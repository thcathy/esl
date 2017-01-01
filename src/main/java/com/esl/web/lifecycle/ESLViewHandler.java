package com.esl.web.lifecycle;

import com.esl.web.jsf.controller.LanguageController;
import com.esl.web.model.UserSession;
import com.sun.faces.application.view.MultiViewHandler;
import org.slf4j.LoggerFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.Map;

public class ESLViewHandler extends MultiViewHandler {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ESLViewHandler.class);


	@Override
	public Locale calculateLocale(FacesContext context)
	{
		Locale locale = null;

		// Use locale from view root if any
		if (context.getViewRoot() != null) {
			locale = context.getViewRoot().getLocale();
		}

		ExternalContext extContext = context.getExternalContext();

		// look into request param if no locale
		if (locale ==  null) {
			Map<String, String>	paramsMap = extContext.getRequestParameterMap();
			for (String key : paramsMap.keySet()) {
				if (LanguageController.LOCALE_PARAM.equals(key)) {
					logger.debug("LOCALE_PARAM [{}]", paramsMap.get(key));
					locale = new Locale(paramsMap.get(key));
				}
			}
		}

		UserSession u = (UserSession) extContext.getSessionMap().get("userSession");
		if (u == null) {
			u = new UserSession();
			extContext.getSessionMap().put("userSession", u);
		}


		// look into session if still no locale
		if (locale == null) locale = u.getLocale();

		// finally use framework calculate locale
		if (locale == null) locale = super.calculateLocale(context);

		// Set locale into session bean
		u.setLocale(locale);

		return locale;
	}
}
