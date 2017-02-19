package com.esl.web.jsf.controller.member;

import com.esl.dao.PracticeResultDAO;
import com.esl.model.Member;
import com.esl.model.PracticeResult;
import com.esl.model.practice.PhoneticSymbols;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.service.dictation.IDictationStatService;
import com.esl.service.practice.IPhoneticPracticeService;
import com.esl.service.practice.IPhoneticSymbolPracticeService;
import com.esl.service.practice.IPracticeResultService;
import com.esl.web.jsf.controller.ESLController;
import com.esl.web.model.dictation.DictationSummary;
import com.esl.web.model.practice.PhoneticPracticeSummary;
import com.esl.web.model.practice.PracticeResultSummary;
import com.esl.web.model.practice.VocabPracticeSummary;
import com.esl.web.util.ChartUtil;
import com.esl.web.util.LanguageUtil;
import com.esl.web.util.SelectItemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import java.util.List;

@Controller
@Scope("session")
public class SummaryController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(SummaryController.class);

	private final String bundleName = "messages.member.Summary";
	private final String memberIndexView = "/member/index";

	// Supporting instance
	@Resource private IPracticeResultService practiceResultService;
	@Resource(name="phoneticPracticeService") private IPhoneticPracticeService vocabPracticeService;
	@Resource(name="phoneticSymbolPracticeService") private IPhoneticSymbolPracticeService phoneticPracticeService;
	@Resource private IDictationStatService dictationStatService;

	// UI display data
	//private List<PracticeResultSummary> allSummary;
	private PracticeResultSummary vocabSummary;
	private PracticeResultSummary phonicsSummary;
	private VocabPracticeSummary vocabPracticeSummary;
	private PhoneticPracticeSummary phonPracticeSummary;
	private DictationSummary dictationSummary;

	//private int summaryIndex;
	private PhoneticSymbols.Level selectedLevel = Level.Full;


	//	 ================= Function Getter =================== //

	/**
	 * Use for jsp, To refresh all UI string to new language
	 */
	@Override
	@Transactional
	public String getInitLanguage() {
		logger.info("getInitLanguage: START");

		// Get all required practice results and Top Result
		vocabSummary = practiceResultService.getPracticeResultSummary(userSession.getMember(), PracticeResult.PHONETICPRACTICE, null);
		phonicsSummary = practiceResultService.getPracticeResultSummary(userSession.getMember(), PracticeResult.PHONETICSYMBOLPRACTICE, selectedLevel);
		vocabPracticeSummary = vocabPracticeService.getVocabPracticeSummary(userSession.getMember());
		phonPracticeSummary = phoneticPracticeService.getPhoneticPracticeSummary(userSession.getMember());
		dictationSummary = dictationStatService.getDictationSummary(userSession.getMember());

		LanguageUtil.formatGradeDescription(userSession.getMember().getGrade(), getLocale());
		LanguageUtil.formatGradeDescription(vocabPracticeSummary.getExistingGrade(), getLocale());
		LanguageUtil.formatGradeDescription(vocabPracticeSummary.getFavourGrade(), getLocale());
		if (vocabPracticeSummary.getOverallPracticeResult()!=null) LanguageUtil.formatGradeDescription(vocabPracticeSummary.getOverallPracticeResult().getGrade(), getLocale());
		if (phonPracticeSummary.getFavourPracticeResult()!=null) LanguageUtil.formatGradeDescription(phonPracticeSummary.getFavourPracticeResult().getGrade(), getLocale());
		if (phonPracticeSummary.getOverallPracticeResult()!=null) LanguageUtil.formatGradeDescription(phonPracticeSummary.getOverallPracticeResult().getGrade(), getLocale());
		if (phonPracticeSummary.getFavourPracticeResult()!=null) {
			String favourPracticeResultLevel = phonPracticeSummary.getFavourPracticeResult().getLevel();
			if (favourPracticeResultLevel!= null && !"".equals(favourPracticeResultLevel))	phonPracticeSummary.getFavourPracticeResult().setLevelTitle(LanguageUtil.getLevelTitle(Level.valueOf(favourPracticeResultLevel), getLocale()));
		}
		if (phonPracticeSummary.getOverallPracticeResult()!=null) {
			String overallPracticeResultLevel = phonPracticeSummary.getOverallPracticeResult().getLevel();
			if (overallPracticeResultLevel!=null && !"".equals(overallPracticeResultLevel)) phonPracticeSummary.getOverallPracticeResult().setLevelTitle(LanguageUtil.getLevelTitle(Level.valueOf(overallPracticeResultLevel),getLocale()));
		}

		// format chart
		ChartUtil.setPracticeSummaryCharts(vocabSummary, getLocale());
		ChartUtil.setPracticeSummaryCharts(phonicsSummary, getLocale());

		// format TopResult
		LanguageUtil.formatTopResult(vocabSummary.getRateRanking(), getLocale());
		LanguageUtil.formatTopResult(vocabSummary.getScoreRanking(), getLocale());
		LanguageUtil.formatTopResult(phonicsSummary.getRateRanking(), getLocale());
		LanguageUtil.formatTopResult(phonicsSummary.getScoreRanking(), getLocale());

		logger.info("getInitLanguage: END");
		return "";
	}

	public List<SelectItem> getLevels() { return SelectItemUtil.getPhoneticSymobolPracticeLevels(); }

	// ============== Constructor ================//
	public SummaryController() {}

	// ============== Functions ================//

	/**
	 * Retrieve variable for member index page
	 */
	public String index() {
		Member member = userSession.getMember();

		// Return error if cannot find member login instance
		if (member == null) {
			logger.warn("index: member is null, return error view");
			return errorView;
		}

		logger.info("index: returned view:" + memberIndexView);
		return memberIndexView;
	}

	/**
	 * Update Phonics Practice static for level change
	 */
	@Transactional
	public void phonicsLevelChangeListener(AjaxBehaviorEvent event) {
		logger.info("phonicsLevelChangeListener: START");
		phonicsSummary = practiceResultService.getPracticeResultSummary(userSession.getMember(), PracticeResult.PHONETICSYMBOLPRACTICE, selectedLevel);
		ChartUtil.setPracticeSummaryCharts(phonicsSummary, getLocale());

		// format TopResult
		LanguageUtil.formatTopResult(phonicsSummary.getRateRanking(), getLocale());
		LanguageUtil.formatTopResult(phonicsSummary.getScoreRanking(), getLocale());
	}

	// ============== Setter / Getter ================//
	public void setPracticeResultService(IPracticeResultService practiceResultService) {this.practiceResultService = practiceResultService;}
	public void setVocabPracticeService(IPhoneticPracticeService vocabPracticeService) {this.vocabPracticeService = vocabPracticeService;}
	public void setPhoneticPracticeService(IPhoneticSymbolPracticeService phoneticPracticeService) {this.phoneticPracticeService = phoneticPracticeService;}
	public void setDictationStatService(IDictationStatService dictationStatService) {this.dictationStatService = dictationStatService;}

	//public List<PracticeResultSummary> getAllSummary() {return allSummary;	}
	//public void setAllSummary(List<PracticeResultSummary> allSummary) {	this.allSummary = allSummary;}
	//public PracticeResultSummary getSummary() {return allSummary.get(summaryIndex); }

	public PhoneticSymbols.Level getSelectedLevel() {return selectedLevel;}
	public void setSelectedLevel(PhoneticSymbols.Level selectedLevel) {	this.selectedLevel = selectedLevel;	}

	public PracticeResultSummary getVocabSummary() {return vocabSummary;}
	public void setVocabSummary(PracticeResultSummary vocabSummary) {this.vocabSummary = vocabSummary;}

	public PracticeResultSummary getPhonicsSummary() {return phonicsSummary;}
	public void setPhonicsSummary(PracticeResultSummary phonicsSummary) {this.phonicsSummary = phonicsSummary;}

	public VocabPracticeSummary getVocabPracticeSummary() {return vocabPracticeSummary;}
	public void setVocabPracticeSummary(VocabPracticeSummary vocabPracticeSummary) {this.vocabPracticeSummary = vocabPracticeSummary;}

	public PhoneticPracticeSummary getPhonPracticeSummary() {return phonPracticeSummary;}
	public void setPhonPracticeSummary(PhoneticPracticeSummary phonPracticeSummary) {this.phonPracticeSummary = phonPracticeSummary;}

	public DictationSummary getDictationSummary() {return dictationSummary;}
	public void setDictationSummary(DictationSummary dictationSummary) {this.dictationSummary = dictationSummary;}

	public int getMinFullMark() {return PracticeResultDAO.MIN_FULL_MARK; }


}
