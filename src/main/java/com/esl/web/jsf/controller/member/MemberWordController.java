package com.esl.web.jsf.controller.member;

import com.esl.dao.IMemberWordDAO;
import com.esl.model.Member;
import com.esl.model.MemberWord;
import com.esl.model.MemberWord.Ordering;
import com.esl.model.PhoneticQuestion;
import com.esl.service.memberword.IMemberWordManageService;
import com.esl.service.memberword.MemberWordManageService;
import com.esl.web.jsf.controller.ESLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.*;

@Controller
@Scope("session")
public class MemberWordController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(MemberWordController.class);
	private static String bundleName = "messages.member.MemberWord";

	private static String manageView = "/member/vocab/manage";

	// use for filter
	private static String SHOW_ALL = "SHOW_ALL";
	private static String SHOW_LEARNT = "SHOW_LEARNT";

	// use for order sequence
	private static String ASC = "asc";
	private static String DESC = "desc";

	//	 Supporting instance
	@Resource private IMemberWordManageService manageService;
	@Resource private IMemberWordDAO memberWordDAO;

	//	 ============== UI display data ================//

	// for manage page
	private List<MemberWord> vocabs;
	private MemberWord.Ordering vocabOrder;
	private String filter = SHOW_ALL;
	private Map<MemberWord, Boolean> selectedVocabs;

	// for save word
	private PhoneticQuestion selectedPhoneticQuestion;
	private Map<PhoneticQuestion, Boolean> savedQuestion = null;

	//============== Functions ================//
	@Transactional
	public String saveWord() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		logger.info("saveWord: START");

		Member member = userSession.getMember();
		logger.info("saveWord: Selected word[" + selectedPhoneticQuestion + "]");
		if (selectedPhoneticQuestion == null || member == null) return null;
		logger.info("saveWord: Selected word[" + selectedPhoneticQuestion.getWord() + "], member[" + member.getUserId() + "]");

		// save the word
		String result = manageService.saveWord(member, selectedPhoneticQuestion);
		logger.info("saveword: saveWord return [" + result + "]");

		// update practice's map
		logger.info("saveWord: Update practice.memberWordSaved map");
		savedQuestion.put(selectedPhoneticQuestion, true);

		// send the result message to user
		String resultString = bundle.getString(result);
		if (MemberWordManageService.WORD_ALREADY_SAVED.equals(result) || MemberWordManageService.WORD_SAVED.equals(result)) {
			resultString = MessageFormat.format(resultString, selectedPhoneticQuestion.getWord());
		}
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resultString, null));

		return null;					// return null as a4j request
	}

	/**
	 * Open manage page
	 */
	@Transactional
	public String launchManage() {
		logger.info("launchManage: START");

		Member member = userSession.getMember();
		vocabs = memberWordDAO.listWords(member);
		logger.info("launchManage: vocabs.size[" + vocabs.size() + "]");

		setSelectVocabsMap();	// reset selected list

		return manageView;
	}

	@Transactional
	public String showAll() {
		logger.info("showAll: START");
		filter = SHOW_ALL;
		return launchManage();
	}

	public String showLearnt() {
		logger.info("showLearnt: START");

		Member member = userSession.getMember();
		vocabs = memberWordDAO.listLearntWords(member);
		logger.info("showLearnt: vocabs.size[" + vocabs.size() + "]");
		filter = SHOW_LEARNT;

		setSelectVocabsMap();	// reset selected list

		return null;
	}

	public String orderByDate() {
		logger.info("orderByDate: START");
		if (vocabOrder == null || !vocabOrder.equals(Ordering.DateAsc)) {
			Collections.sort(vocabs, new MemberWord.DateComparator());
			vocabOrder = Ordering.DateAsc;
		} else {
			Collections.sort(vocabs, Collections.reverseOrder(new MemberWord.DateComparator()));
			vocabOrder = Ordering.DateDesc;
		}
		logger.info("orderByDate: vocabOrder[" + vocabOrder + "]");
		return null;
	}

	public String orderByTrialCount() {
		logger.info("orderByTrialCount: START");
		if (vocabOrder == null || !vocabOrder.equals(Ordering.TrialCountAsc)) {
			Collections.sort(vocabs, new MemberWord.TrialCountComparator());
			vocabOrder = Ordering.TrialCountAsc;
		} else {
			Collections.sort(vocabs, Collections.reverseOrder(new MemberWord.TrialCountComparator()));
			vocabOrder = Ordering.TrialCountDesc;
		}
		logger.info("orderByTrialCount: vocabOrder[" + vocabOrder + "]");
		return null;
	}

	public String orderByCorrectCount() {
		logger.info("orderByCorrectCount: START");
		if (vocabOrder == null || !vocabOrder.equals(Ordering.CorrectCountAsc)) {
			Collections.sort(vocabs, new MemberWord.CorrectCountComparator());
			vocabOrder = Ordering.CorrectCountAsc;
		} else {
			Collections.sort(vocabs, Collections.reverseOrder(new MemberWord.CorrectCountComparator()));
			vocabOrder = Ordering.CorrectCountDesc;
		}
		logger.info("orderByCorrectCount: vocabOrder[" + vocabOrder + "]");
		return null;
	}

	@Transactional
	public String deleteAllMemberWords() {
		logger.info("deleteAllMemberWords: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		int counter = 0;
		if (vocabs != null || vocabs.size() > 0 ) counter = manageService.deleteWords(vocabs);

		//	send the result message to user
		String resultString = bundle.getString("manageDeleted");
		resultString = MessageFormat.format(resultString, counter);
		logger.info("deleteAllMemberWords: returned msg [" + resultString +  "]");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resultString, null));

		return launchManage();
	}

	@Transactional
	public String deleteSelectedMemberWords() {
		logger.info("deleteSelectedMemberWords: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		List<MemberWord> removeList = new ArrayList<MemberWord>();

		// create the removal list
		for (MemberWord w : selectedVocabs.keySet()) {
			if (selectedVocabs.get(w) == true) {
				removeList.add(w);
			}
		}

		logger.info("deleteSelectedMemberWords: removeList.size[" + removeList.size() + "]");
		int counter = 0;
		if (removeList.size() > 0 ) counter = manageService.deleteWords(removeList);

		// send the result message to user
		String resultString = bundle.getString("manageDeleted");
		resultString = MessageFormat.format(resultString, counter);
		logger.info("deleteLearntMemberWords: returned msg [" + resultString +  "]");

		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resultString, null));

		return launchManage();
	}

	@Transactional
	public String deleteLearntMemberWords() {
		logger.info("deleteLearntMemberWords: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());

		int counter = 0;
		if (vocabs != null || vocabs.size() > 0 ) counter = manageService.deleteLearntWords(userSession.getMember());

		//	send the result message to user
		String resultString = bundle.getString("manageDeleted");
		resultString = MessageFormat.format(resultString, counter);
		logger.info("deleteLearntMemberWords: returned msg [" + resultString +  "]");

		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resultString, null));

		return launchManage();
	}

	//	============== Supporting Function ================//
	private void setSelectVocabsMap() {
		selectedVocabs = new HashMap<MemberWord, Boolean>();
		if (vocabs != null) {
			for (MemberWord w: vocabs) {
				selectedVocabs.put(w, false);
			}
		}
	}

	//	============== Getter Function ================//
	public int getVocabsSize() {return vocabs.size();}

	public boolean isShowAll() { return SHOW_ALL.equals(filter);}

	public boolean isShowLearnt() { return SHOW_LEARNT.equals(filter); }

	public boolean isOrderByDate() {
		return (MemberWord.Ordering.DateAsc.equals(vocabOrder) || MemberWord.Ordering.DateDesc.equals(vocabOrder));
	}

	public boolean isOrderByTrial() {
		return (MemberWord.Ordering.TrialCountAsc.equals(vocabOrder) || MemberWord.Ordering.TrialCountDesc.equals(vocabOrder));
	}

	public boolean isOrderByCorrect() {
		return (MemberWord.Ordering.CorrectCountAsc.equals(vocabOrder) || MemberWord.Ordering.CorrectCountDesc.equals(vocabOrder));
	}

	public String getOrderSequence() {
		logger.info("getOrderSequence: vocabOrder[" + vocabOrder + "]");
		if (vocabOrder == null) return "";
		switch (vocabOrder) {
		case CorrectCountAsc:
			return ASC;
		case CorrectCountDesc:
			return DESC;
		case DateAsc:
			return ASC;
		case DateDesc:
			return DESC;
		case TrialCountAsc:
			return ASC;
		case TrialCountDesc:
			return DESC;
		}
		return "";
	}

	//	 ============== Setter / Getter ================//
	public void setManageService(IMemberWordManageService manageService) {this.manageService = manageService;}
	public void setMemberWordDAO(IMemberWordDAO memberWordDAO) {this.memberWordDAO = memberWordDAO;}

	public PhoneticQuestion getSelectedPhoneticQuestion() {return selectedPhoneticQuestion;}
	public void setSelectedPhoneticQuestion(PhoneticQuestion selectedPhoneticQuestion) {this.selectedPhoneticQuestion = selectedPhoneticQuestion;}

	public List<MemberWord> getVocabs() {return vocabs;}
	public void setVocabs(List<MemberWord> vocabs) {this.vocabs = vocabs;}

	public Map<PhoneticQuestion, Boolean> getSavedQuestion() {return savedQuestion;	}
	public void setSavedQuestion(Map<PhoneticQuestion, Boolean> savedQuestion) {this.savedQuestion = savedQuestion;}

	public Map<MemberWord, Boolean> getSelectedVocabs() {return selectedVocabs;}
	public void setSelectedVocabs(Map<MemberWord, Boolean> selectedVocabs) {this.selectedVocabs = selectedVocabs;}

}
