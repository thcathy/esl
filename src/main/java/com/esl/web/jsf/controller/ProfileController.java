package com.esl.web.jsf.controller;

import com.esl.model.Member;
import com.esl.service.IMembershipService;
import com.esl.service.practice.IPhoneticPracticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ResourceBundle;

@Controller
@Scope("session")
public class ProfileController extends ESLController {
	private static final long serialVersionUID = 5733512605406754494L;

	private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

	private final String bundleName = "messages.Authentication";
	private final String profileView = "/member/profile";

	// Model and Service
	@Resource private IMembershipService membershipSvc;
	@Resource(name="phoneticPracticeService") private IPhoneticPracticeService phoneticPracticeSvc;
	private Member newMember;			// for sign up

	// UI
	@Size(min=6, max=20, message="{incorrectSize}")
	private String confirmedPIN = "";
	
	@Size(min=6, max=20, message="{incorrectSize}")
	private String existPIN = "";		// for chg pw
	
	@Size(min=6, max=20, message="{incorrectSize}")
	private String newPIN = "";			// for chg pw

	private boolean acceptToF;
	private boolean showSignUpForm;

	// ============== Setter / Getter ================//
	public void setMembershipSvc(IMembershipService membershipSvc) {this.membershipSvc = membershipSvc;}
	public void setPhoneticPracticeSvc(IPhoneticPracticeService phoneticPracticeSvc) {this.phoneticPracticeSvc = phoneticPracticeSvc;	}

	public Member getMember() {	return userSession.getMember();	}

	public Member getNewMember() {return newMember;}
	public void setNewMember(Member newMember) {this.newMember = newMember;}

	public String getConfirmedPIN() {return confirmedPIN;}
	public void setConfirmedPIN(String confirmedPIN) {this.confirmedPIN = confirmedPIN;}

	public boolean isAcceptToF() {return acceptToF;}
	public void setAcceptToF(boolean acceptToF) {this.acceptToF = acceptToF;}

	public boolean isShowSignUpForm() {return showSignUpForm;}
	public void setShowSignUpForm(boolean showSignUpForm) {this.showSignUpForm = showSignUpForm;	}

	public String getExistPIN() {return existPIN;}
	public void setExistPIN(String existPIN) {this.existPIN = existPIN;}

	public String getNewPIN() {	return newPIN;}
	public void setNewPIN(String newPIN) {this.newPIN = newPIN;	}

	// ============== Constructor ================//
	public ProfileController() {}

	// ============== Functions ================//
	public String startSignUp() {
		showSignUpForm = true;
		newMember = new Member();
		acceptToF = false;
		return "/public/signup";
	}

	public String signUp() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// Check PIN with confirmed PIN
		if (newMember.getPIN()== null || !newMember.getPIN().equals(confirmedPIN)) {
			logger.info("signUp: input PIN different");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("signupPINNotMatch"), null));
			return null;
		}

		// Check Accepted ToF
		if (!acceptToF) {
			logger.info("signUp: Do not accept ToF");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("signupNotAcceptToF"),null));
			return null;
		}

		// set up input data format
		newMember.setUserId(newMember.getUserId().toLowerCase());
		newMember.setName(newMember.getName().getLastName().trim(),	newMember.getName().getFirstName().trim());
		newMember.setAddress(newMember.getAddress());
		newMember.setPhoneNumber(newMember.getPhoneNumber());
		newMember.setSchool(newMember.getSchool());

		String result = membershipSvc.signUp(newMember);
		logger.info("signUp: membershipService.signUp return code: " + result);

		// Successful sign up
		if (result.equals(IMembershipService.ACCOUNT_CREATED))
		{
			// Create Practice Result for user
			String createResult = phoneticPracticeSvc.createPracticeResult(newMember);
			logger.info("signUp: phoneticPracticeSvc.createPracticeResult return code: " + result);
			if (!IPhoneticPracticeService.COMPLETED.equals(createResult)) return errorView;

			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("signupAccountCreated"), null));
			showSignUpForm = false;
		}
		else if (result.equals(IMembershipService.USER_ID_DUPLICATED))
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("signupUserIdDuplicated"), null));
		else
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("signupError"), null));
		return null;
	}

	/**
	 * Start profile page
	 */
	public String profile() {
		logger.info("profile: START");

		// login checking
		if (userSession.getMember() == null) {
			logger.warn("profile: cannot find member obj");
			return errorView;
		}
		return profileView;
	}

	/**
	 * Save the new account profile
	 */
	public String updateProfile() {
		logger.info("updateProfile: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		Member member = userSession.getMember();

		// set up input data format
		member.setName(member.getName().getLastName().trim(),member.getName().getFirstName().trim());
		member.setAddress(member.getAddress().trim());
		member.setPhoneNumber(member.getPhoneNumber().trim());
		member.setSchool(member.getSchool().trim());

		String result = membershipSvc.updateProfile(member);
		logger.info("updateProfile: membershipService.updateProfile return code: " + result);

		if (result.equals(IMembershipService.PROFILE_UPDATED))
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("profileUpdated"),null));
		else {
			return errorView;
		}
		logger.info("updateProfile: END");
		return null;
	}

	/**
	 * Change password from profile page
	 */
	public String changePIN() {
		logger.info("changePIN: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// Check PIN with confirmed PIN
		if (!newPIN.equals(confirmedPIN)) {
			logger.info("changePIN: input PIN different");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("signupPINNotMatch"), null));
			return null;
		}

		// Check existing PIN
		if (!userSession.getMember().getPIN().equals(existPIN)) {
			logger.info("changePIN: wrong exist PIN");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("profileWrongPIN"), null));
			return null;
		}

		// Perform Update
		userSession.getMember().setPIN(newPIN);
		String result = membershipSvc.updateProfile(userSession.getMember());
		logger.info("changePIN: membershipService.updateProfile return code: " + result);

		if (result.equals(IMembershipService.PROFILE_UPDATED))
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("profilePINUpdated"),null));
		else {
			return errorView;
		}
		logger.info("changePIN: END");
		return null;
	}

	// ================== Supporting Function ==================== //
}
