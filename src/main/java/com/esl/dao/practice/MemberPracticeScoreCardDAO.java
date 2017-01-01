package com.esl.dao.practice;

import java.sql.Date;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.ESLDao;
import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.enumeration.ESLPracticeType;
import com.esl.exception.IllegalParameterException;
import com.esl.model.Member;
import com.esl.util.DateUtil;

@Transactional
@Repository("memberPracticeScoreCardDAO")
public class MemberPracticeScoreCardDAO extends ESLDao<MemberPracticeScoreCard> implements IMemberPracticeScoreCardDAO {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(MemberPracticeScoreCardDAO.class);

	public MemberPracticeScoreCard getScoreCard(Member member, ESLPracticeType practiceType, Date scoreCardDate) {
		final String logPrefix = "getScoreCard: ";
		logger.info("{}START", logPrefix);
		if (member == null || practiceType==null || scoreCardDate==null) throw new IllegalParameterException(new String[]{"member, practiceType, scoreCardDate"}, new Object[]{member, practiceType, scoreCardDate});

		logger.debug("{}input userId[{}], practiceType[{}], scoreCardDate[{}]", new Object[] {logPrefix, member.getUserId(), practiceType, scoreCardDate});

		scoreCardDate = DateUtil.toFirstDayOfMonth(scoreCardDate);
		String queryStr = "FROM MemberPracticeScoreCard card WHERE card.member = :member AND card.practiceType = :practiceType AND card.scoreCardDate = :scoreCardDate";
		Query query = sessionFactory.getCurrentSession().createQuery(queryStr).setParameter("member", member).setParameter("practiceType", practiceType).setParameter("scoreCardDate", scoreCardDate);
		MemberPracticeScoreCard scoreCard = (MemberPracticeScoreCard) query.uniqueResult();

		if (scoreCard != null) {
			logger.debug("{}Score Card found", logPrefix);
			return scoreCard;
		} else {
			logger.debug("{}No score card, create a new one", logPrefix);
			scoreCard = new MemberPracticeScoreCard(member,scoreCardDate, practiceType);
			persist(scoreCard);
			return scoreCard;
		}
	}

	@SuppressWarnings("unchecked")
	public List<MemberPracticeScoreCard> listByStanding(ESLPracticeType practiceType, Date scoreCardDate) {
		final String logPrefix = "listByStanding:";
		logger.debug("{} START", logPrefix);
		if (practiceType==null || scoreCardDate==null) throw new IllegalParameterException(new String[]{"practiceType, scoreCardDate"}, new Object[]{practiceType, scoreCardDate});
		logger.debug("{} practiceType[{}], scoreCardDate[{}]", new Object[] {logPrefix, practiceType, scoreCardDate});

		scoreCardDate = DateUtil.toFirstDayOfMonth(scoreCardDate);
		String queryStr = "FROM MemberPracticeScoreCard AS card WHERE card.practiceType = :practiceType AND card.scoreCardDate = :scoreCardDate ORDER BY card.score DESC, card.lastUpdatedDate, card.lastMonthStanding";
		Query query = sessionFactory.getCurrentSession().createQuery(queryStr).setParameter("practiceType", practiceType).setParameter("scoreCardDate", scoreCardDate);

		return query.list();

	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void persistAndCommit(MemberPracticeScoreCard scoreCard) {
		persist(scoreCard);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public MemberPracticeScoreCard saveOrUpdateStanding(MemberPracticeScoreCard scoreCard) {
		final String logPrefix = "saveOrUpdateStanding:";
		logger.debug("{} START", logPrefix);
		if (scoreCard == null) throw new IllegalParameterException(new String[]{"scoreCard"}, new Object[]{scoreCard});
		logger.debug("{} input scoreCard [{}]", scoreCard);

		MemberPracticeScoreCard dbScoreCard = getScoreCard(scoreCard.getMember(), scoreCard.getPracticeType(), scoreCard.getScoreCardDate());
		dbScoreCard.setLastMonthStanding(scoreCard.getLastMonthStanding());
		persist(dbScoreCard);
		return dbScoreCard;
	}
}
