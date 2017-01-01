package com.esl.service.medal;

import com.esl.dao.practice.IMemberPracticeScoreCardDAO;
import com.esl.dao.practice.IPracticeMedalDAO;
import com.esl.entity.practice.MemberPracticeScoreCard;
import com.esl.entity.practice.PracticeMedal;
import com.esl.enumeration.ESLPracticeType;
import com.esl.enumeration.Medal;
import com.esl.exception.IllegalParameterException;
import com.esl.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.List;

@Transactional
@Service("practiceMedalService")
public class PracticeMedalService implements IPracticeMedalService {
	private static Logger logger = LoggerFactory.getLogger(PracticeMedalService.class);


	// supporting class
	@Resource
	private IMemberPracticeScoreCardDAO scoreCardDAO;
	@Resource
	private IPracticeMedalDAO practiceMedalDAO;
	@Resource
	private IMemberPracticeScoreCardService scoreCardService;

	@Override
	public void generateMedal(ESLPracticeType type, Date date) {
		final String logPrefix = "generateMedal:";
		logger.debug("{} START: type [{}], date [{}]", new Object[] {logPrefix, type, date});
		if (type == null || date == null) throw new IllegalParameterException(new String[]{"type", "date"}, new Object[]{type, date});

		date = DateUtil.toFirstDayOfMonth(date);

		// delete any generate medal
		List<PracticeMedal> existMedals = practiceMedalDAO.listPracticeMedals(type, date);
		logger.debug("{} have exist medals [{}], now remove.", logPrefix, existMedals.size());
		for (PracticeMedal m : existMedals) practiceMedalDAO.delete(m);

		List<MemberPracticeScoreCard> scoreCards = scoreCardDAO.listByStanding(type, date);
		logger.debug("{} total score cards return [{}]", logPrefix, scoreCards.size());

		if (scoreCards == null) return;

		createMedals(date, scoreCards);			// create Medals

		scoreCardService.createNextMonthScoreCards(scoreCards);	// create score for next month
	}


	@Override
	@Scheduled(cron="0 50 1 1 * ?")
	public void generateMedalBatch() {
		logger.info("generateMedalBatch: START");
		Date now = new Date(new java.util.Date().getTime());

		generateMedal(ESLPracticeType.VocabPractice, now);
		generateMedal(ESLPracticeType.PhoneticPractice, now);
		generateMedal(ESLPracticeType.IrregularVerbPractice, now);

		logger.info("generateMedalBatch: END");

	}

	/**
	 * Create Gold, Silver and Bronze
	 * @param scoreCards
	 */
	private void createMedals(Date date, List<MemberPracticeScoreCard> scoreCards) {
		final String logPrefix = "createMedals:";
		logger.debug("{} START: cards size [{}] on date [{}]", new Object[] {logPrefix, scoreCards==null?0:scoreCards.size(), date});
		if (scoreCards == null) return;

		for (int i=0; i < 3 && i < scoreCards.size(); i++) {
			PracticeMedal medal = preparePracticeMedal(Medal.getByWeight(i+1), date, scoreCards.get(i));
			practiceMedalDAO.persist(medal);
		}
	}

	/**
	 * Create the medal object for persist
	 */
	private PracticeMedal preparePracticeMedal(Medal medal, Date date, MemberPracticeScoreCard scoreCard) {
		final String logPrefix = "prepareMedal:";
		logger.debug("{} START: create Medal [{}] on [{}] for scoreCard [{}]", new Object[] {logPrefix, medal, date, scoreCard});
		if (medal == null || scoreCard == null || date == null) throw new IllegalParameterException(new String[]{"medal", "scoreCard", "date"}, new Object[]{medal, scoreCard, date});

		PracticeMedal m = new PracticeMedal();
		m.setAwardedDate(date);
		m.setMedal(medal);
		m.setMember(scoreCard.getMember());
		m.setPracticeType(scoreCard.getPracticeType());
		m.setScore(scoreCard.getScore());
		return m;
	}

	/**
	 * Random get a medal and return all medal of the same month and type
	 * @return a list of medals start from Gold
	 */
	public List<PracticeMedal> getRandomTopMedals() {
		final String logPrefix = "getRandomTopMedals:";
		logger.debug("{} START", logPrefix);
		PracticeMedal randomMedal = practiceMedalDAO.getRandomPracticeMedal();
		// TODO random with 3 month only

		logger.debug("{} random medal [{}]", logPrefix, randomMedal);

		return practiceMedalDAO.listPracticeMedals(randomMedal.getPracticeType(), randomMedal.getAwardedDate());
	}


	// ============== Setter / Getter ================//
	public void setMemberPracticeScoreCardDAO(IMemberPracticeScoreCardDAO scoreCardDAO) {this.scoreCardDAO = scoreCardDAO; }
	public void setPracticeMedalDAO(IPracticeMedalDAO practiceMedalDAO) {this.practiceMedalDAO = practiceMedalDAO;}
	public void setMemberPracticeScoreCardService(IMemberPracticeScoreCardService scoreCardService) {	this.scoreCardService = scoreCardService;}




}
