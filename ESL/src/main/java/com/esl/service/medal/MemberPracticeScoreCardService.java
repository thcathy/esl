package com.esl.service.medal;

import java.sql.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.practice.IMemberPracticeScoreCardDAO;
import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.exception.IllegalParameterException;
import com.esl.util.DateUtil;

@Service("memberPracticeScoreCardService")
public class MemberPracticeScoreCardService implements IMemberPracticeScoreCardService {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	// supporting class
	@Resource private IMemberPracticeScoreCardDAO scoreCardDAO;

	@Override
	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public int createNextMonthScoreCards(List<MemberPracticeScoreCard> lastMonthScoreCards) {
		final String logPrefix = "createNextMonthScoreCards:";
		logger.debug("{} START", logPrefix);
		if (lastMonthScoreCards == null) throw new IllegalParameterException(new String[]{"lastMonthScoreCards"}, new Object[]{lastMonthScoreCards});
		logger.debug("{} lastMonthScoreCards.size[{}]", new Object[] {logPrefix, lastMonthScoreCards.size()});

		Date thisMonth = DateUtil.toFirstDayOfMonth(new Date(new java.util.Date().getTime()));
		logger.debug("{} create for thisMonth [{}]", logPrefix, thisMonth);

		for (int i=0; i < lastMonthScoreCards.size(); i++) {
			MemberPracticeScoreCard lastScoreCard = lastMonthScoreCards.get(i);
			MemberPracticeScoreCard newScoreCard = new MemberPracticeScoreCard(lastScoreCard.getMember(), thisMonth, lastScoreCard.getPracticeType());
			newScoreCard.setLastMonthStanding(i + 1);
			newScoreCard = scoreCardDAO.saveOrUpdateStanding(newScoreCard);
			logger.trace("{} created Score Card [{}]", logPrefix, newScoreCard);
		}

		return lastMonthScoreCards.size();
	}


	// ============== Setter / Getter ================//
	public void setMemberPracticeScoreCardDAO(IMemberPracticeScoreCardDAO scoreCardDAO) {this.scoreCardDAO = scoreCardDAO; }





}
