package com.esl.web.model.practice;

public class IrregularVerbPracticeHistoryUnit {
	public enum Type {
		Correct, Wrong, Question
	}

	private String word;
	private Type type;

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
}
