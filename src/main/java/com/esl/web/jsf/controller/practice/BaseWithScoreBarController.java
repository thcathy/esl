package com.esl.web.jsf.controller.practice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.web.jsf.controller.ESLController;
import com.esl.web.model.practice.ScoreBar;

/**
 * A base controller for all practice with scoreBar
 */
public abstract class BaseWithScoreBarController extends ESLController {
	protected static int SCOREBAR_FULLLENGTH = 500;

	private static Logger logger = LoggerFactory.getLogger(BaseWithScoreBarController.class);

	//	 ============== UI display data ================//
	private ScoreBar scoreBar;

	// ============== Constructor ================//
	public BaseWithScoreBarController() {
		scoreBar = new ScoreBar();
		scoreBar.setFullLength(SCOREBAR_FULLLENGTH);
	}

	//	============== Supporting Function ================//
	public void setScoreBar(int current, int max) {
		scoreBar.setCurrentMark(current);
		scoreBar.setMaxMark(max);

		logger.info("setScoreBar: currentMark[" + current + "], maxMark[" + max + "]");
	}

	//	 ============== Setter / Getter ================//
	public ScoreBar getScoreBar() {	return scoreBar;}
	public void setScoreBar(ScoreBar scoreBar) {this.scoreBar = scoreBar;}
}
