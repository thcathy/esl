package com.esl.web.jsf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("session")
public class ApplicationController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

	@Value("${static.server.host}")
	private String staticHost;

	public String getStaticHost() {
		return staticHost;
	}

	public void setStaticHost(String staticHost) {
		this.staticHost = staticHost;
	}
}
