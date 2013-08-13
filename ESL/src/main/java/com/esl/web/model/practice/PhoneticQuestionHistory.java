package com.esl.web.model.practice;

import com.esl.model.PhoneticQuestion;

public class PhoneticQuestionHistory {
	private PhoneticQuestion question;
	private String answer;
	private boolean correct;

	public PhoneticQuestion getQuestion() {
		return question;
	}
	public void setQuestion(PhoneticQuestion question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public boolean isCorrect() {
		return correct;
	}
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

}
