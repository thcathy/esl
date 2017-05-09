package com.esl.entity.practice;

import java.util.List;

public class MemberScoreRanking {
	List<MemberScore> scores;
	MemberScore base;
	boolean isTop;

	// ============== Setter / Getter ================//
	public List<MemberScore> getScores() {return scores;}

	public MemberScore getBase() {return base;}

	public boolean isTop() {return isTop;}

	// ============== Constructors ================//
	public MemberScoreRanking() {}

	public MemberScoreRanking(MemberScore base, List<MemberScore> scores, boolean isTop) {
		this.scores = scores;
		this.base = base;
		this.isTop = isTop;
	}

	// ********************** Common Methods ********************** //

}
