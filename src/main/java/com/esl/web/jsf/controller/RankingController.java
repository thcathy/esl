package com.esl.web.jsf.controller;

import com.esl.dao.IGradeDAO;
import com.esl.entity.practice.PracticeMedal;
import com.esl.model.Grade;
import com.esl.model.Member;
import com.esl.model.PracticeResult;
import com.esl.model.TopResult;
import com.esl.model.practice.PhoneticSymbols;
import com.esl.model.practice.PhoneticSymbols.Level;
import com.esl.service.practice.ITopResultService;
import com.esl.web.util.LanguageUtil;
import com.esl.web.util.SelectItemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Scope("session")
public class RankingController extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(RankingController.class);
	private static String bundleName = "messages.TopResult";
	private static String rankingView = "/practice/topresult/ranking";
	private static SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-mm");

	// UI Data
	private long selectedGradeId = -1;
	private Grade selectedGrade;
	private PhoneticSymbols.Level selectedLevel = Level.Full;
	private String selectedPracticeType = PracticeResult.PHONETICPRACTICE;
	private String selectedDate = monthFormat.format(new Date());

	private TopResult topScore;
	private TopResult topRate;
	private TopResult userScore;
	private TopResult userRate;
	private List<PracticeMedal> medals;

	// UI Component


	// Supporting classes
	@Resource private IGradeDAO gradeDAO;
	@Resource private ITopResultService topResultService;

	// ============== Setter / Getter ================//
	public void setGradeDAO(IGradeDAO gradeDAO) {this.gradeDAO = gradeDAO;}
	public void setTopResultService(ITopResultService topResultService) {this.topResultService = topResultService;	}

	public String getSelectedPracticeType() {	return selectedPracticeType;}
	public void setSelectedPracticeType(String selectedPracticeType) {	this.selectedPracticeType = selectedPracticeType;}

	public long getSelectedGradeId() {return selectedGradeId;}
	public void setSelectedGradeId(long selectedGradeId) {
		this.selectedGradeId = selectedGradeId;
		if (selectedGradeId < 0) this.selectedGrade = null;
		else this.selectedGrade = gradeDAO.getGradeById(selectedGradeId);
	}

	public PhoneticSymbols.Level getSelectedLevel() {
		if (!PracticeResult.PHONETICSYMBOLPRACTICE.equals(selectedPracticeType))
			return null;
		return selectedLevel;
	}
	public void setSelectedLevel(PhoneticSymbols.Level selectedLevel) {	this.selectedLevel = selectedLevel;}
	//public void setSelectedLevel(String selectedLevelStr) { this.selectedLevel = Level.valueOf(selectedLevelStr); }

	public String getSelectedDate() { return this.selectedDate;}
	public void setSelectedDate(String selectedDate) { this.selectedDate = selectedDate; }

	public List<PracticeMedal> getMedals() { return medals; }

	public TopResult getTopRate() {	return topRate;}
	public TopResult getTopScore() {return topScore;}
	public TopResult getUserRate() {return userRate;}
	public TopResult getUserScore() {return userScore;}

	// ============== Getter Functions ================//
	@Override
	public String getInitLanguage() {
		logger.info("getInitLanguage: START");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale= facesContext.getViewRoot().getLocale();

		LanguageUtil.formatTopResult(topScore, locale);
		LanguageUtil.formatTopResult(topRate, locale);
		LanguageUtil.formatTopResult(userScore, locale);
		LanguageUtil.formatTopResult(userRate, locale);

		return "";
	}

	// Return all grades available
	public List<SelectItem> getAvailableGrades() {
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, getLocale());

		List<Grade> grades = gradeDAO.getAll();
		List<SelectItem> items = new ArrayList<SelectItem>(grades.size()+1);

		// Add default "overall"
		items.add(new SelectItem(-1, bundle.getString("grade.all")));

		for (Grade grade : grades) {
			LanguageUtil.formatGradeDescription(grade, getLocale());
			SelectItem item = new SelectItem(grade.getId(), grade.getDescription());
			items.add(item);
		}
		logger.info("getAvailableGrades: returned items size: " + items.size());
		return items;
	}

	// Return all practice available
	public List<SelectItem> getPracticeList() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, getLocale());

		items.add(new SelectItem(PracticeResult.PHONETICPRACTICE, bundle.getString(PracticeResult.PHONETICPRACTICE)));
		items.add(new SelectItem(PracticeResult.PHONETICSYMBOLPRACTICE, bundle.getString(PracticeResult.PHONETICSYMBOLPRACTICE)));
		return items;
	}

	public List<SelectItem> getLevels() { return SelectItemUtil.getPhoneticSymobolPracticeLevels(); }

	public boolean isShowLevels() {
		if (selectedPracticeType == null || PracticeResult.PHONETICSYMBOLPRACTICE.equals(selectedPracticeType))
			return true;
		else
			return false;
	}

	public String getPkDate() {
		return new Date().toString();
	}

	// ============== Public Functions ================//
	public String start() {
		logger.info("start: START");

		topScore = topResultService.getTopResultByGrade(TopResult.OrderType.Score, selectedPracticeType, selectedGrade, getSelectedLevel());
		topRate = topResultService.getTopResultByGrade(TopResult.OrderType.Rate, selectedPracticeType,selectedGrade, getSelectedLevel());

		logger.info("start: topScore[" + topScore + "]");
		logger.info("start: topRate[" + topRate + "]");

		Member member = userSession.getMember();
		if (userSession.getMember() != null) {
			userScore = topResultService.getResultListByMemberGrade(TopResult.OrderType.Score, selectedPracticeType, member, selectedGrade, getSelectedLevel());
			userRate = topResultService.getResultListByMemberGrade(TopResult.OrderType.Rate, selectedPracticeType, member, selectedGrade, getSelectedLevel());
		} else {
			userScore = null;
			userRate = null;
		}
		logger.info("start: userScore[" + userScore + "]");
		logger.info("start: userRate[" + userRate + "]");

		return rankingView;
	}

	public String a4jSubmit() {
		return start();
	}

	// ============== Private Functions ===============//

}
