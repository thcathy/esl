package com.esl.web.model.practice;

import com.esl.model.Member;

public class Standing {	
	private Member member;
	private int standing;
	private String value;
	
	// ============== Setter / Getter ================//
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public int getStanding() {
		return standing;
	}
	public void setStanding(int standing) {
		this.standing = standing;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	// ********************** Common Methods ********************** //	
	public String toString() {
		return  "Standing ('" + getMember().getUserId() + "') [ " +
				"standing: '" + getStanding() + "' " +	
				"value: '" + getValue() + "' " +								
				" ]";
	}
}
