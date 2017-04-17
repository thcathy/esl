package com.esl.entity.practice;

import com.esl.entity.IAuditable;
import com.esl.model.Member;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="MEMBER_SCORE")
public class MemberScore implements Serializable, IAuditable {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date createdDate = new java.util.Date();

	@Column(name = "LAST_UPDATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date lastUpdatedDate;

	@ManyToOne
	@JoinColumn(name="MEMBER_ID")
	private Member member;

	@Column(name = "SCORE_YEAR_MONTH")
	private int scoreYearMonth;

	@Column(name = "SCORE")
	private int score;

	// ********************** Constructors ********************** //
	public MemberScore() {}

	public MemberScore(Member member, int scoreYearMonth) {
		this.member = member;
		this.scoreYearMonth = scoreYearMonth;
	}


	// ********************** Accessor Methods ********************** //

	public long getId() {return id;}
	public void setId(long id) {this.id = id;}

	public Member getMember() {return member;}
	public void setMember(Member member) {this.member = member;}

	public int getScoreYearMonth() {return scoreYearMonth;}
	public void setScoreYearMonth(int scoreYearMonth) {this.scoreYearMonth = scoreYearMonth;}

	public int getScore() {return score;}
	public void setScore(int score) {this.score = score;}
	public void addScore(int score) {this.score += score; }

	public java.util.Date getCreatedDate() {return createdDate;}
	public void setCreatedDate(java.util.Date createdDate) {this.createdDate = createdDate;}

	public java.util.Date getLastUpdatedDate() {return lastUpdatedDate;}
	public void setLastUpdatedDate(java.util.Date lastUpdatedDate) {this.lastUpdatedDate = lastUpdatedDate;}


	// ********************** Common Methods ********************** //

	public static int thisMonth() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMM");
		return Integer.parseInt(dateformat.format(new Date()));
	}

	public static int allTimesMonth() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String toString() {
		return String.format("MemberScore (%s) [userId=%s, scoreCardDate=%s, score=%s]", getId(), member.getUserId(),scoreYearMonth, score);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((member == null) ? 0 : member.hashCode());
		result = prime * result + (scoreYearMonth);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberScore other = (MemberScore) obj;
		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		if (scoreYearMonth != other.scoreYearMonth)
			return false;
		return true;
	}
}
