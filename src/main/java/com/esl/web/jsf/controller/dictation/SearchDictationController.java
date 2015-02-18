package com.esl.web.jsf.controller.dictation;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.esl.dao.dictation.IDictationDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.service.dictation.IDictationStatService;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.model.SearchDictationInputForm;

@Controller
@Scope("session")
public class SearchDictationController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(SearchDictationController.class);
	private static final String bundleName = "messages.member.Dictation";
	private static final String resultView = "/member/dictation/searchresult";

	public static int maxSearchResults;

	//	 Supporting instance
	@Resource private IDictationDAO dictationDAO;
	@Resource private IDictationStatService dictationStatService;
	@Value("${Dictation.MaxSearchResults}") public void setMaxSearchResults(int maxSearchResults) {SearchDictationController.maxSearchResults = maxSearchResults;}

	//	 ============== UI display data ================//
	private SearchDictationInputForm inputForm;
	private List<Dictation> results;

	// ============== Constructor ================//
	public SearchDictationController() {
		inputForm = new SearchDictationInputForm();
	}

	//============== Functions ================//
	public String search() {
		final String logPrefix = "searchDictation: ";
		logger.info(logPrefix + "START");

		dictationDAO.attachSession(userSession.getMember());
		inputForm.setCurrentUser(userSession.getMember());
		results = dictationStatService.searchDictation(inputForm, maxSearchResults);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());
		if (results == null || results.size() == 0) {
			logger.info(logPrefix + "No dictation found");
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("noDictationFound"), null));
			return null;
		}
		if (results.size() >= maxSearchResults) {
			logger.info(logPrefix + "Over max results, size[" + results.size() + "]");
			String msg = MessageFormat.format(bundle.getString("totalResultOverMax"), maxSearchResults);
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
		}
		return resultView;
	}



	//	============== Getter Function ================//


	//	============== Supporting Function ================//


	//	 ============== Setter / Getter ================//
	public void setDictationDAO(IDictationDAO dictationDAO) {this.dictationDAO = dictationDAO;}
	public void setDictationStatService(IDictationStatService dictationStatService) {this.dictationStatService = dictationStatService;}

	public SearchDictationInputForm getInputForm() {return inputForm;}
	public void setInputForm(SearchDictationInputForm inputForm) {this.inputForm = inputForm;}

	public List<Dictation> getResults() {return results;}
	public void setResults(List<Dictation> results) {this.results = results;}

}
