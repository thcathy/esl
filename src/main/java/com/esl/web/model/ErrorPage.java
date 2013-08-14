package com.esl.web.model;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("session")
public class ErrorPage implements Serializable {
	private static Logger logger = Logger.getLogger("ESL");
	private final String bundleName = "messages.GeneralMessages";

	private String title = "";
	private String description = "";

	public ErrorPage() {}

	public String getTitle() {
		if ("".equals(title)) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());
			return bundle.getString("generalErrorTitle");
		} else {
			String rtnTitle = title;
			title = "";
			return rtnTitle;
		}
	}
	public void setTitle(String title) {this.title = title;	}

	public String getDescription() {
		if ("".equals(description)) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());
			return bundle.getString("generalErrorDescription");
		} else {
			String rtnDescription = description;
			description = "";
			return rtnDescription;
		}
	}
	public void setDescription(String description) {this.description = description;	}

}
