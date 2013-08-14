package com.esl.web.model.practice;

import java.io.Serializable;

import com.esl.model.*;

/**
 * Use for member summary page
 */
public class PhoneticPracticeSummary implements Serializable {	
	private static final long serialVersionUID = 575090827474660695L;
	
	private PracticeResult overallPracticeResult;
	private PracticeResult favourPracticeResult;
	
	public PracticeResult getOverallPracticeResult() {
		return overallPracticeResult;
	}
	public void setOverallPracticeResult(PracticeResult overallPracticeResult) {
		this.overallPracticeResult = overallPracticeResult;
	}
	public PracticeResult getFavourPracticeResult() {
		return favourPracticeResult;
	}
	public void setFavourPracticeResult(PracticeResult favourPracticeResult) {
		this.favourPracticeResult = favourPracticeResult;
	}
	
}
