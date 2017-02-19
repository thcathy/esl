package com.esl.web.jsf.controller.dictation;

import com.esl.dao.dictation.IDictationDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.web.jsf.controller.ESLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import java.util.ResourceBundle;

@Controller
@Scope("request")
public class OpenDictationController extends ESLController {
	private static final long serialVersionUID = -6870604419654595053L;
	private static Logger logger = LoggerFactory.getLogger(OpenDictationController.class);
	private static final String bundleName = "messages.member.Dictation";

	//	 Supporting instance
	@Resource private DictationPracticeController dictationPracticeController;
	@Resource private DictationManageController dictationManageController;
	@Resource private IDictationDAO dictationDAO;

	//	 ============== UI display data ================//

	private long selectedDictationId;

	// ============== Constructor ================//
	public OpenDictationController() {}

	//============== Functions ================//
	public String openDictationForPractice() {
		final String logPrefix = "openDictationForPractice: ";
		logger.info(logPrefix + "START");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		Dictation dictation = dictationDAO.get(selectedDictationId);
		if (dictation == null) {
			logger.info(logPrefix + "dictation [" + selectedDictationId + "] not found");

			errorPage.setTitle(bundle.getString("dictationNotFoundTitle"));
			errorPage.setDescription(bundle.getString("dictationNotFoundDesc"));
			return errorView;
		} else {
			dictationPracticeController.setDictation(dictation);
			String str =  dictationPracticeController.authDictation();
			if (str == null) {
				errorPage.setTitle(bundle.getString("notAllowOpen"));
				return errorView;
			}
			else
				return str;
		}
	}
	
	public String openDictationForView() {
		final String logPrefix = "openDictationForPractice: ";
		logger.info(logPrefix + "START");
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		Dictation dictation = dictationDAO.get(selectedDictationId);
		if (dictation == null) {
			logger.info(logPrefix + "dictation [" + selectedDictationId + "] not found");

			errorPage.setTitle(bundle.getString("dictationNotFoundTitle"));
			errorPage.setDescription(bundle.getString("dictationNotFoundDesc"));
			return errorView;
		} else {
			dictationManageController.setSelectedDictation(dictation);
			String str =  dictationManageController.launchDictation();
			if (str == null) {
				errorPage.setTitle(bundle.getString("notAllowOpen"));
				return errorView;
			}
			else
				return str;
		}
	}

	@Transactional
	public String randomDictation() {
		final String logPrefix = "randomDictation: ";
		logger.info(logPrefix + "START");

		Dictation dictation = dictationDAO.randomAccessibleDictation(userSession.getMember());
		if (dictation == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

			logger.info(logPrefix + "No dictation return");
			errorPage.setTitle(bundle.getString("dictationNotFoundTitle"));
			errorPage.setDescription(bundle.getString("dictationNotFoundDesc"));
			return errorView;
		} else {
			dictationPracticeController.setDictation(dictation);
			return dictationPracticeController.launchStart();
		}
	}

	//	============== Getter Function ================//


	//	============== Supporting Function ================//


	//	 ============== Setter / Getter ================//
	public void setDictationPracticeController(DictationPracticeController controller) {this.dictationPracticeController = controller; }
	public void setDictationDAO(IDictationDAO dictationDAO) {this.dictationDAO = dictationDAO;}
	public void setDictationManageController(DictationManageController controller) {this.dictationManageController = dictationManageController;}

	public long getSelectedDictationId() {return selectedDictationId;}
	public void setSelectedDictationId(long selectedDictationId) {this.selectedDictationId = selectedDictationId;}
}
