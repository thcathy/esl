package com.esl.entity.practice;

import java.util.List;

import com.esl.enumeration.Preposition;

public class PrepositionPractice extends GrammarPractice {
	private static final long serialVersionUID = 1L;

	private List<String> prepositions;

	@Override public List<String> getQuestions() {return prepositions;}
	@Override public void setQuestions(List<String> prepositions) {this.prepositions = prepositions;}

	@Override public String getQuestionsRegEx() {return Preposition.PREPOSITION_REGEX; }
	@Override public Object[] getQuestionsString() { return Preposition.values(); }

	public List<String> getPrepositions() {return prepositions;}
	public void setPrepositions(List<String> prepositions) {this.prepositions = prepositions;}
}
