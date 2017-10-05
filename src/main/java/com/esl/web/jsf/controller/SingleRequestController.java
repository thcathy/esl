package com.esl.web.jsf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;

@Controller
@Scope("request")
public class SingleRequestController extends ESLController {
	private static Logger log = LoggerFactory.getLogger(SingleRequestController.class);

	public SingleRequestController() {}

	public boolean isWebView() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String userAgent = facesContext.getExternalContext().getRequestHeaderMap().get("User-Agent");
		log.debug("Request user agent: {}", userAgent);

		return userAgent.contains("esl/");
	}

}
