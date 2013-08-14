package com.esl.web.jsf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.dao.IESLDao;
import com.esl.entity.dictation.UserCreatedPractice;


@SuppressWarnings("serial")
public abstract class UserCreatedPracticeController<T extends UserCreatedPractice> extends ESLController {

	private static Logger logger = LoggerFactory.getLogger("ESL");
	private boolean isRecommended = false;

	private IESLDao<T> eslDao;


	public UserCreatedPracticeController() {
		super();
	}

	public boolean isRecommended() {return isRecommended;}
	public void setRecommended(boolean isRecommended) {this.isRecommended = isRecommended;}

	//============== Functions ================//
	public void recommendPractice() {
		T practice = getUserCreatedPractice();

		// check condition
		if (practice == null || isRecommended) return;

		logger.debug("Persist Recommended {} [{}]", practice.getClass() , practice.getId());
		getUserCreatedPractice().setTotalRecommended(practice.getTotalRecommended() + 1);
		eslDao.persist(practice);

		logger.debug("Recommended Dictation [{}], total [{}]", practice.getId(), practice.getTotalRecommended());
		isRecommended = true;
	}

	//	 ============== Setter / Getter ================//
	public void setEslDao(IESLDao<T> eslDao) {this.eslDao = eslDao;}

	public abstract T getUserCreatedPractice();
}