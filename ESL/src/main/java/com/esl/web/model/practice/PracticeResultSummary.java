package com.esl.web.model.practice;

import java.util.List;

import com.esl.model.PracticeResult;
import com.esl.model.TopResult;

public class PracticeResultSummary {
	private PracticeResult gradedPracticeResult;
	private PracticeResult overallPracticeResult;
	private TopResult scoreRanking;
	private TopResult rateRanking;
	
	private List<PracticeResult> practiceResults;
	private String scoreChartRS = "";
	private String countChartRS = "";
	
	// ============== Setter / Getter ================//
	public PracticeResult getGradedPracticeResult() {
		return gradedPracticeResult;
	}
	public void setGradedPracticeResult(PracticeResult gradedPracticeResult) {
		this.gradedPracticeResult = gradedPracticeResult;
	}
	public PracticeResult getOverallPracticeResult() {
		return overallPracticeResult;
	}
	public void setOverallPracticeResult(PracticeResult overallPracticeResult) {
		this.overallPracticeResult = overallPracticeResult;
	}
	public TopResult getScoreRanking() {
		return scoreRanking;
	}
	public void setScoreRanking(TopResult scoreRanking) {
		this.scoreRanking = scoreRanking;
	}
	public TopResult getRateRanking() {
		return rateRanking;
	}
	public void setRateRanking(TopResult rateRanking) {
		this.rateRanking = rateRanking;
	}
	public List<PracticeResult> getPracticeResults() {
		return practiceResults;
	}
	public void setPracticeResults(List<PracticeResult> practiceResults) {
		this.practiceResults = practiceResults;
	}
	public String getScoreChartRS() {
		return scoreChartRS;
	}
	public void setScoreChartRS(String scoreChartRS) {
		this.scoreChartRS = scoreChartRS;
	}
	public String getCountChartRS() {
		return countChartRS;
	}
	public void setCountChartRS(String countChartRS) {
		this.countChartRS = countChartRS;
	}
	
	// ********************** Common Methods ********************** //	
	
}
