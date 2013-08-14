package com.esl.web.model.practice;

import java.util.List;

import com.esl.entity.practice.PracticeMedal;

public class TopPracticeMedals {
	public enum OrderType { Score, Rate };

	List<PracticeMedal> medals;
	String practiceTitle;
	String monthStr;

	// ============== Setter / Getter ================//
	public List<PracticeMedal> getMedals() {return medals;}
	public String getPracticeTitle() {return practiceTitle;}
	public String getMonthStr() { return monthStr; }

	// ============== Constructors ================//
	public TopPracticeMedals() {}

	public TopPracticeMedals(List<PracticeMedal> medals, String practiceTitle, String monthStr) {
		this.medals = medals;
		this.practiceTitle = practiceTitle;
		this.monthStr = monthStr;
	}


	// ********************** Common Methods ********************** //
	@Override
	public String toString() {
		return String.format("TopPracticeMedals [practiceTitle=%s, monthStr=%s, medals.size=%s]",
				practiceTitle, monthStr, medals==null?"null":medals.size());
	}

}
