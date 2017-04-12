package com.esl.enumeration;

public enum VocabDifficulty {
	Beginner(1, 500), Easy(501, 1500), Normal(1501, 2500), Hard(2501, 3500), VeryHard(2501, 5000);

	int fromRank;
	int toRank;

	VocabDifficulty(int fromRank, int toRank) {
		this.fromRank = fromRank;
		this.toRank = toRank;
	}

	public int getFromRank() { return fromRank; }
	public int getToRank() { return toRank; }
}