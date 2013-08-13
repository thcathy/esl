package com.esl.web.model.dictation;

import java.io.Serializable;

import com.esl.entity.dictation.Dictation;
import com.esl.entity.dictation.DictationHistory;

/**
 * Use for member summary page
 */
public class DictationSummary implements Serializable {
	private static final long serialVersionUID = 6150890243727577112L;

	private int dictationCreated;
	private int totalAttempted;
	private Dictation mostAttemptedDictation;
	private Dictation mostRecommendedDictation;
	private DictationHistory lastHistory;

	public int getDictationCreated() {
		return dictationCreated;
	}
	public void setDictationCreated(int dictationCreated) {
		this.dictationCreated = dictationCreated;
	}
	public int getTotalAttempted() {
		return totalAttempted;
	}
	public void setTotalAttempted(int totalAttempted) {
		this.totalAttempted = totalAttempted;
	}
	public Dictation getMostAttemptedDictation() {
		return mostAttemptedDictation;
	}
	public void setMostAttemptedDictation(Dictation mostAttemptedDictation) {
		this.mostAttemptedDictation = mostAttemptedDictation;
	}
	public Dictation getMostRecommendedDictation() {
		return mostRecommendedDictation;
	}
	public void setMostRecommendedDictation(Dictation mostRecommendedDictation) {
		this.mostRecommendedDictation = mostRecommendedDictation;
	}
	public DictationHistory getLastHistory() {
		return lastHistory;
	}
	public void setLastHistory(DictationHistory lastHistory) {
		this.lastHistory = lastHistory;
	}



}
