package com.esl.web.model;

import com.esl.model.Member;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Locale;

@Controller
@Scope("session")
public class UserSession implements Serializable {
	private static final long serialVersionUID = -5591254458719504860L;

	private Member member;
	private boolean showGoogleImage = true;
	private Locale locale = null;
	private Boolean useIE9 = null;

	public UserSession() {}

	public Member getMember() {
		try {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
					.getExternalContext().getSession(false);
			return (Member) session.getAttribute("MEMBER");
		} catch (Exception e) {
			return this.member;
		}
	}
	public void setMember(Member member) {
		try {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
					.getExternalContext().getSession(false);
			session.setAttribute("MEMBER", member);
		} catch (Exception e) {
			this.member = member;
		}
	}

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

	public String getAuth0Locale() {
		if (locale != null) {
			if (locale.toString().contains("zh"))
				return "zh-tw";
		}
		return "en";
	}

	public String stopShowGoogleImage() {
		showGoogleImage = false;
		return "";
	}

	public String resumeShowGoogleImage() {
		showGoogleImage = true;
		return "";
	}

}
