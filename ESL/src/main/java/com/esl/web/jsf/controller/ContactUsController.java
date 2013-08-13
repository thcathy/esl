package com.esl.web.jsf.controller;

import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.service.IMailService;
import com.esl.web.model.ContactUsForm;

@Controller
@Scope("request")
public class ContactUsController extends ESLController {
	private final String bundleName = "messages.GeneralMessages";

	@Resource private ContactUsForm contactUsForm;
	@Resource private IMailService mailService;
	private UIForm uiContactUsForm;

	// ============== Constructor ================//
	public ContactUsController() {}

	// ============== Setter / Getter ================//
	public ContactUsForm getContactUsForm() {return contactUsForm;	}
	public void setContactUsForm(ContactUsForm contactUsForm) {this.contactUsForm = contactUsForm;	}

	public IMailService getMailService() {return mailService;	}
	public void setMailService(IMailService mailService) {this.mailService = mailService;	}

	public UIForm getUiContactUsForm() {return uiContactUsForm;	}
	public void setUiContactUsForm(UIForm uiContactUsForm) {this.uiContactUsForm = uiContactUsForm;	}


	// ============== Functions ================//
	public String contactUs() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		String result = mailService.contactUs(contactUsForm);
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString(result), null));
		uiContactUsForm.setRendered(false);
		return "";
	}
}
