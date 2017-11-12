package com.esl.web.jsf.controller.dictation;

import com.esl.dao.IMemberDAO;
import com.esl.dao.dictation.IDictationDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.exception.BusinessValidationException;
import com.esl.model.group.MemberGroup;
import com.esl.service.JSFService;
import com.esl.service.dictation.IDictationManageService;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.util.DictationUtil;
import com.esl.web.util.SelectItemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Scope("session")
public class DictationEditController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(DictationEditController.class);
	private static String bundleName = "messages.member.Dictation";
	private static String editView = "/member/dictation/edit";
	private static String successView = "/member/dictation/edit2";
	private static final String selfDictationInputView = "/practice/selfdictation/input";

	//	 Supporting instance
	@Resource private IDictationManageService manageService;
	@Resource private IMemberDAO memberDAO;
	@Resource private IDictationDAO dictationDAO;
	@Resource private JSFService jsfService;

	//	 ============== UI display data ================//
	private Dictation editDictation;
	private List<SelectItem> accessibleMemberGroups;
	private List<String> selectedGroups = new ArrayList<String>();
	private String vocabs;
	private boolean requirePassword = false;
	private String password;
	private String confirmedPassword;
	private String type = "Vocab";
	private boolean showImage = true;
	@Value("${Dictation.Article.MaxSize}") private int maxArticleSize = 0;

	//============== Functions ================//

	public String initCheck() {
		if (userSession == null || userSession.getMember() == null) {
			return launchCreate();
		}
		return "";
	}

	public String launchWithVocabs(List<String> inputVocab) throws IOException {
		logger.info("create dictation with vocabs");

		vocabs = DictationUtil.concatVocabs(inputVocab);
		editDictation = new Dictation();
		prepareDisplayObjects();
		vocabs = DictationUtil.concatVocabs(inputVocab);
		type = "Vocab";
		if (userSession.getMember() == null) {
			logger.info("redirectToJSF to login page");
			return jsfService.redirectTo("/login?redirect=/member/dictation/edit.jsf");
		} else {
			return editView;
		}
	}

	public String launchWithArticle(String article) throws IOException {
		logger.info("create dictation with article");

		editDictation = new Dictation();
		prepareDisplayObjects();
		vocabs = article;
		type = "Article";
		if (userSession.getMember() == null) {
			logger.info("redirectToJSF to login page");
			return jsfService.redirectTo("/login?redirect=/member/dictation/edit.jsf");
		} else {
			return editView;
		}
	}

	/**
	 * Open create new dictation page
	 */
	public String launchCreate() {
		final String logTitle = "launchCreate: ";
		logger.info(logTitle + "START");

		if (userSession.getMember() == null) {
			logger.info(logTitle + "Member is null, direct to self dictation input page");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("signUp"), null));
			return selfDictationInputView;
		}

		editDictation = new Dictation();
		prepareDisplayObjects();
		return editView;
	}

	/**
	 * Open edit dictation page
	 */
	public String launchEdit() {
		final String logTitle = "launchEdit: ";
		logger.info(logTitle + "START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		if (editDictation == null) return errorView;
		if (!manageService.allowEdit(editDictation, userSession.getMember())) {
			logger.info(logTitle + "[" + userSession.getMember() + "] cannot edit dictation created by [" + editDictation.getCreator().getUserId() + "]");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("notAllowEdit"), null));
			return null;
		}
		editDictation = dictationDAO.get(editDictation.getId());
		prepareDisplayObjects();
		return editView;
	}

	/**
	 * Create / edit form submit
	 */
	@Transactional
	public String submit() {
		final String logTitle = "submit: ";
		logger.info(logTitle + "START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		// Validate input
		if (vocabs != null) {
			if (Dictation.DictationType.Vocab.toString().equals(type)) {
				Pattern p = Pattern.compile("^([a-zA-Z ]++[\\-,]?)+");
				Matcher matcher = p.matcher(vocabs);
				if (!matcher.matches()) {
					logger.info(logTitle + "input vocabs invalid:" + vocabs);
					facesContext.addMessage("editDictation:vocabs", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("vocabsInvalidInput"), null));
					return null;
				}
			} else {
				if (vocabs.length() > maxArticleSize) {
					logger.info("Article too long");
					facesContext.addMessage("editDictation:vocabs", new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageFormat.format(bundle.getString("articleTooLong"),maxArticleSize), null));
					return null;
				}
			}
		}

		// Check PIN with confirmed PIN
		if (requirePassword) {
			if (confirmedPassword == null || password == null || !confirmedPassword.equals(password)) {
				logger.info(logTitle + "input PIN different");
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("pwdNotMatch"), null));
				return null;
			}
		}
		editDictation = dictationDAO.merge(editDictation);
		dictationDAO.refreshDictation(editDictation);
		setAccessibleGroups();

		// set password
		if (requirePassword) {
			if (password != null && !password.equals("")) editDictation.setPassword(password);
		}
		else
			editDictation.setPassword("");

		editDictation.setCreator(userSession.getMember());
		editDictation.setLastModifyDate(new Date());
		editDictation.setShowImage(showImage);

		try {

			setVocabOrArticle(editDictation);

			manageService.saveDictation(editDictation);
			logger.info(logTitle + "Total vocab added [" + editDictation.getVocabs().size() + "]");
			logger.info(logTitle + "Total accesible group [" + editDictation.getAccessibleGroups().size() + "]");
			return successView;
		} catch (BusinessValidationException e) {
			logger.info(logTitle + "BV Exception:" + e.getMessage());
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(e.getErrorCode()), null));
			return null;
		}
	}

	private void setVocabOrArticle(Dictation dictation) {
		if (Dictation.DictationType.Vocab.toString().equals(type))
			manageService.setVocabs(dictation, vocabs);
		else
			dictation.setArticle(vocabs);
	}

	//	============== Getter Function ================//

	// Return all grades available
	public List<SelectItem> getAvailableAgeGroups() {
		return SelectItemUtil.getAvailableAgeGroups();
	}

	/**
	 * Getter return max allowed words
	 */
	public int getMaxVocabs() { return manageService.getMaxVocabs();}

	/**
	 * Getter return the separator of words
	 */
	public String getSeparator() {
		return Dictation.SEPARATOR;
	}

	//	============== Supporting Function ================//
	private void prepareDisplayObjects() {
		if (userSession.getMember() != null) {
			memberDAO.attachSession(userSession.getMember());
			accessibleMemberGroups = SelectItemUtil.getAvailableMemberGroups(userSession.getMember().getGroups());
		}
		selectedGroups.clear();
		for (MemberGroup g : editDictation.getAccessibleGroups()) {
			selectedGroups.add(g.getId().toString());
		}

		type = editDictation.getType().toString();
		showImage = editDictation.isShowImage();
		requirePassword = editDictation.isRequirePassword();
		if ("Vocab".equals(type))
			vocabs = editDictation.getVocabsString();
		else
			vocabs = editDictation.getArticle();
	}

	private void setAccessibleGroups() {
		final String logTitle = "setAccessibleGroups: ";
		logger.info(logTitle + "START");

		List<MemberGroup> groups = userSession.getMember().getGroups();
		editDictation.getAccessibleGroups().clear();
		for (String s : selectedGroups) {
			logger.info(logTitle + "get string [" + s + "]");
			for (MemberGroup g : groups) {
				if (g.getId().toString().equals(s)) {
					editDictation.addAccessibleGroup(g);
				}
			}
		}
	}

	//	 ============== Setter / Getter ================//
	public void setManageService(IDictationManageService manageService) {this.manageService = manageService;}
	public void setMemberDAO(IMemberDAO memberDAO) {this.memberDAO = memberDAO;}
	public void setDictationDAO(IDictationDAO dictationDAO) {this.dictationDAO = dictationDAO;}

	public Dictation getEditDictation() {return editDictation;}
	public void setEditDictation(Dictation editDictation) {this.editDictation = editDictation;}

	public List<SelectItem> getAccessibleMemberGroups() { return accessibleMemberGroups; }
	public void setAccessibleMemberGroups(List<SelectItem> accessibleMemberGroups) {this.accessibleMemberGroups = accessibleMemberGroups;}

	public String getVocabs() {	return vocabs;}
	public void setVocabs(String vocabs) {	this.vocabs = vocabs;}

	public List<String> getSelectedGroups() {return selectedGroups;}
	public void setSelectedGroups(List<String> selectedGroups) {this.selectedGroups = selectedGroups;}

	public String getConfirmedPassword() {return confirmedPassword;}
	public void setConfirmedPassword(String confirmedPassword) {this.confirmedPassword = confirmedPassword;}

	public boolean isRequirePassword() {return requirePassword;}
	public void setRequirePassword(boolean requirePassword) {this.requirePassword = requirePassword;}

	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}

	public String getType() {return type;}
	public void setType(String type) {this.type = type;	}

	public boolean isShowImage() {return showImage;}
	public void setShowImage(boolean showImage) {this.showImage = showImage;}
}
