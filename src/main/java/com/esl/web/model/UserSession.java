package com.esl.web.model;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.model.Member;

@Controller
@Scope("session")
public class UserSession implements Serializable {
	private static final long serialVersionUID = -5591254458719504860L;

	private Member member;
	private boolean showGoogleImage = true;
	private Locale locale = null;
	private Boolean useIE9 = null;

	public UserSession() {}

	public Member getMember() {return member;}
	public void setMember(Member member) {this.member = member;}

	public Locale getLocale() {return locale;}
	public void setLocale(Locale locale) {this.locale = locale;}

	public boolean isShowGoogleImage() {return showGoogleImage;}
	public void setShowGoogleImage(boolean showGoogleImage) {this.showGoogleImage = showGoogleImage;}

	public boolean isLogined() {
		return (member != null);
	}

	public String getLocaleString() {
		return (locale==null) ? FacesContext.getCurrentInstance().getViewRoot().getLocale().toString() : locale.toString();
	}

	public String stopShowGoogleImage() {
		showGoogleImage = false;
		return "";
	}

	public String resumeShowGoogleImage() {
		showGoogleImage = true;
		return "";
	}

	public boolean isXyz() {
		if (useIE9 == null) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String s = request.getHeader("user-agent");
			System.out.println(s);
			if (s!= null && s.indexOf("MSIE 9.0") > -1) {
				useIE9 = Boolean.TRUE;
			} else {
				useIE9 = Boolean.FALSE;
			}
		}
		System.out.println(useIE9);
		return useIE9.booleanValue();
	}
	public void setXyz(boolean xyz) {}

}
