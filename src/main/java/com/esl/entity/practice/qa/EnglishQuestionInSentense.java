package com.esl.entity.practice.qa;

import java.util.List;

import javax.persistence.*;

@Entity
@DiscriminatorValue("Q_IN_SENTENCE")
public class EnglishQuestionInSentense extends EnglishQuestions {
	@OneToMany
	@JoinTable(name="ENG_Q_IN_SENTENCE_QUESTIONS_MAP",
			joinColumns=@JoinColumn(name="ENGLISH_QUESTIONS_ID"),
			inverseJoinColumns=@JoinColumn(name="PRACTICE_SENTENCE_QUESTION_ID"))
			private List<SentenceQuestion> questions;

	// ----------------------------- getter / setter -------------------//
	public List<SentenceQuestion> getQuestions() {	return questions;}
	public void setQuestions(List<SentenceQuestion> questions) {	this.questions = questions;}

	@Override
	public Type getType() {return Type.QuestionInSentence;}

	// ---------------------------- Public function -------------------- //

}
