package com.esl.web.jsf.controller;

import com.esl.dao.IGradeDAO;
import com.esl.entity.practice.PracticeMedal;
import com.esl.model.PracticeResult;
import com.esl.model.TopResult;
import com.esl.model.TopResult.OrderType;
import com.esl.service.medal.IPracticeMedalService;
import com.esl.service.practice.ITopResultService;
import com.esl.web.model.practice.Standing;
import com.esl.web.model.practice.TopPracticeMedals;
import com.esl.web.util.LanguageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
@Scope("session")
public class RandomTopResult extends ESLController {
	private static Logger logger = LoggerFactory.getLogger(RandomTopResult.class);
	private final String bundleName = "messages.TopResult";

	public enum TopType { Result, Medal };

	// UI Data
	private TopResult randomResult;
	private TopPracticeMedals randomMedals;
	private TopType existType;

	// UI Component

	// Supporting classes
	@Resource private IGradeDAO gradeDAO;
	@Resource private ITopResultService topResultService;
	@Resource private IPracticeMedalService practiceMedalService;

	// ============== Setter / Getter ================//
	public void setGradeDAO(IGradeDAO gradeDAO) {this.gradeDAO = gradeDAO;}
	public void setTopResultService(ITopResultService topResultService) {this.topResultService = topResultService;	}
	public void setPracticeMedalService(IPracticeMedalService practiceMedalService) {this.practiceMedalService = practiceMedalService;}

	public TopResult getRandomTopResult() {	return randomResult; }
	public TopPracticeMedals getRandomMedals() { return randomMedals; }
	public String getExistType() {return  (existType == null) ? "" : existType.toString(); }

	// ============== Getter Functions ================//
	public String getNewRandomTopResult() {
		final String logPrefix = "getNewRandomTopResult:";
		existType =  TopType.values()[new Random().nextInt(TopType.values().length)];
		logger.debug("{} START, random a type [{}]", logPrefix, existType);

		if (TopType.Result.equals(existType)) {
			randomResult = topResultService.getRandomTopResults();
			LanguageUtil.formatTopResult(randomResult, getLocale());
			logger.debug("getNewRandomTopResult: retrieved TopResult: " + randomResult);
		} else if (TopType.Medal.equals(existType)) {
			List<PracticeMedal> medals = practiceMedalService.getRandomTopMedals();
			randomMedals = LanguageUtil.getTopPracticeMedals(medals, getLocale());
			logger.debug("{} random randomMedals [{}]", logPrefix, randomMedals);
		}

		return "";
	}


	public List<Standing> getStandings() {
		if (randomResult == null) {
			logger.info("getStandings: do not have a random TopResult");
			return null;
		}

		PracticeResult[] results = randomResult.getTopResults().toArray(new PracticeResult[0]);
		List<Standing> standings = new ArrayList<Standing>();

		// Set Number format for rate
		NumberFormat formatRate = NumberFormat.getInstance();
		formatRate.setMinimumFractionDigits(0);
		formatRate.setMaximumFractionDigits(0);

		for (int i=0; i<results.length; i++) {
			PracticeResult pr = results[i];
			Standing standing = new Standing();
			standing.setMember(pr.getMember());
			standing.setStanding(i+1);
			if (randomResult.getOrderType() == OrderType.Rate)
			{
				standing.setValue(formatRate.format(pr.getRate()*100) + "%");
			}
			else
			{
				standing.setValue(Integer.toString(pr.getMark()) + "pt");
			}
			standings.add(standing);
		}
		logger.info("getStandings: return standings size:" + standings.size());
		return standings;
	}


	// ============== Public Functions ================//

	// ============== Private Functions ===============//

}
