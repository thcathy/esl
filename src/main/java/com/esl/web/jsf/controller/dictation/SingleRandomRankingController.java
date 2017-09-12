package com.esl.web.jsf.controller.dictation;

import com.esl.entity.practice.MemberScore;
import com.esl.entity.practice.MemberScoreRanking;
import com.esl.service.history.RankingService;
import com.esl.web.jsf.controller.ESLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Controller
@Scope("request")
public class SingleRandomRankingController extends ESLController {
	private final Logger log = LoggerFactory.getLogger(SingleRandomRankingController.class);

	@Resource private RankingService rankingService;
	private MemberScoreRanking ranking;

	@PostConstruct
	public void init() {
		log.debug("get random ranking from service");
		ranking = rankingService.getScheduledRandomRanking();
	}

	public MemberScoreRanking getRandomRanking() {
		return ranking;
	}

	public boolean isAllTimes() {
		if (ranking != null && ranking.getScores() != null && ranking.getScores().size() > 0)
			return ranking.getScores().get(0).getScoreYearMonth() == MemberScore.allTimesMonth();
		else
			return false;
	}
}
