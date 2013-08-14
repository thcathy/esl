package com.esl.entity.practice;

import java.util.List;

import com.esl.enumeration.SubjectPronoun;

public class SubjectPronounPractice extends GrammarPractice {
	private static final long serialVersionUID = 1L;

	private List<String> subjectPronouns;

	@Override public List<String> getQuestions() {return subjectPronouns;}
	@Override public void setQuestions(List<String> subjectPronouns) {this.subjectPronouns = subjectPronouns;}

	@Override public String getQuestionsRegEx() {return SubjectPronoun.SUBJECT_PRONOUN_REGEX; }
	@Override public Object[] getQuestionsString() { return SubjectPronoun.values(); }

}
