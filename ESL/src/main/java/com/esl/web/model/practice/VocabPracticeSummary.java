package com.esl.web.model.practice;

import java.io.Serializable;

import com.esl.model.*;

/**
 * Use for member summary page
 */
public class VocabPracticeSummary implements Serializable {	
	private static final long serialVersionUID = -3865848091836047491L;
	
	private Grade existingGrade;
	private Grade favourGrade;
	private int existingScoreRank;
	private int existingRateRank;
	private PracticeResult overallPracticeResult;
	
	public Grade getExistingGrade() {
		return existingGrade;
	}
	public void setExistingGrade(Grade existingGrade) {
		this.existingGrade = existingGrade;
	}
	public Grade getFavourGrade() {
		return favourGrade;
	}
	public void setFavourGrade(Grade favourGrade) {
		this.favourGrade = favourGrade;
	}
	public int getExistingScoreRank() {
		return existingScoreRank;
	}
	public void setExistingScoreRank(int existingScoreRank) {
		this.existingScoreRank = existingScoreRank;
	}
	public int getExistingRateRank() {
		return existingRateRank;
	}
	public void setExistingRateRank(int existingRateRank) {
		this.existingRateRank = existingRateRank;
	}
	public PracticeResult getOverallPracticeResult() {
		return overallPracticeResult;
	}
	public void setOverallPracticeResult(PracticeResult overallPracticeResult) {
		this.overallPracticeResult = overallPracticeResult;
	}
	
	
}
