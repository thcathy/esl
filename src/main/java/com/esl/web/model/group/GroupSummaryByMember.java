package com.esl.web.model.group;

import com.esl.model.PracticeResult;
import com.esl.model.group.MemberGroup;

public class GroupSummaryByMember {
	// UI retrieve variables
	private MemberGroup group;
	private boolean haveNewMsg;
	private PracticeResult phonPractResult;
	private String practiceType;
	private int phonScoreRank;
	private int phonRateRank;
	private int phonTeamRank;
	
	//	 ============== Setter / Getter ================//
	public MemberGroup getGroup() {	return group;}
	public void setGroup(MemberGroup group) {this.group = group;}
	
	public boolean isHaveNewMsg() {	return haveNewMsg;}
	public void setHaveNewMsg(boolean haveNewMsg) {	this.haveNewMsg = haveNewMsg;}
	
	public PracticeResult getPhonPractResult() {return phonPractResult;}
	public void setPhonPractResult(PracticeResult phonPractResult) {this.phonPractResult = phonPractResult;}
	
	public int getPhonRateRank() {return phonRateRank;}
	public void setPhonRateRank(int phonRateRank) {this.phonRateRank = phonRateRank;}
	
	public int getPhonScoreRank() {	return phonScoreRank;}
	public void setPhonScoreRank(int phonScoreRank) {this.phonScoreRank = phonScoreRank;}
	
	public String getPracticeType() {return practiceType;}
	public void setPracticeType(String practiceType) {this.practiceType = practiceType;}
	
	public int getPhonTeamRank() {return phonTeamRank;}
	public void setPhonTeamRank(int phonTeamRank) {	this.phonTeamRank = phonTeamRank;}
	
	public int getTotalMember() {
		return group.getMembers().size();
	}
	
	//	 ============== Constructor ================//
	public GroupSummaryByMember() {}
	
	//	 ********************** Common Methods ********************** //
	public boolean equals(Object o) {			
		return this == o;
	}
		
	public String toString() {
		return  "GroupSummaryByMember: " +
				"Group Title[" + getGroup().getTitle() + "] " +
				"haveNewMsg[" + isHaveNewMsg() + "] " +
				"Phon Pract Rate Rank[" + getPhonRateRank() + "] " +
				"Phon Pract Score Rank[" + getPhonScoreRank() + "] ";					
	}
}
