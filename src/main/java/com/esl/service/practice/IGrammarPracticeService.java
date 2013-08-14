package com.esl.service.practice;

import java.util.List;

import com.esl.entity.practice.*;
import com.esl.entity.practice.GrammarPractice.PracticeType;
import com.esl.entity.practice.GrammarPractice.QuestionFormat;

public interface IGrammarPracticeService {
	public GrammarPractice generatePracticeByPassage(String passage, PracticeType type, QuestionFormat format, String questionHTMLTag, String questionPattern, String answerPattern);
	public int checkAnswer(List<String> inputAnswers, List<Boolean> results, GrammarPractice practice);
}
