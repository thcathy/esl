package com.esl.service.practice;

import com.esl.entity.practice.*;
import com.esl.entity.practice.GrammarPractice.PracticeType;
import com.esl.entity.practice.GrammarPractice.QuestionFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service("grammarPracticeService")
public class GrammarPracticeService implements IGrammarPracticeService {
	private static Logger logger = LoggerFactory.getLogger(GrammarPracticeService.class);

	@Value("${GrammarPractice.MaxQuestions}")
	private int maxQuestions = 10;

	// ============== Constructor ================//
	public GrammarPracticeService() {}

	// ============== Functions ================//
	public GrammarPractice generatePracticeByPassage(String passage, PracticeType type, QuestionFormat format, String questionHTMLTag, String questionPattern, String answerPattern) {
		if (passage == null || type == null || format == null) {
			return null;
		}

		GrammarPractice practice = createPractice(passage, type, questionHTMLTag, questionPattern, answerPattern);
		return practice;
	}

	/**
	 * @param inputAnswers : user input answer
	 * @param results : return a list of correct / wrong boolean
	 */
	public int checkAnswer(List<String> inputAnswers, List<Boolean> results, GrammarPractice practice) {
		int mark = 0;
		if (inputAnswers == null || results == null || practice == null || inputAnswers.size() == 0 || practice.getQuestionPositions() == null || practice.getQuestionPositions().size() == 0) {
			return mark;
		}

		results.clear();
		for (int i=0; i < inputAnswers.size(); i++) {
			String answer = practice.getQuestions().get(practice.getQuestionPositions().get(i));
			if (answer.equalsIgnoreCase(inputAnswers.get(i).trim())) {
				mark++;
				results.add(true);
			} else {
				results.add(false);
			}
		}

		return mark;
	}


	// ============== Supporting Functions ================//
	private GrammarPractice createPractice(String passage, PracticeType type, String questionHTMLTag, String questionPattern, String answerPattern ) {
		GrammarPractice practice = null;
		if (GrammarPractice.PracticeType.Preposition.equals(type)) {
			practice = new PrepositionPractice();
		} else if (GrammarPractice.PracticeType.Article.equals(type)) {
			practice = new ArticlePractice();
		} else if (GrammarPractice.PracticeType.VerbToBe.equals(type)) {
			practice = new VerbToBePractice();
		} else if (GrammarPractice.PracticeType.SubjectPronoun.equals(type)) {
			practice = new SubjectPronounPractice();
		}

		practice.setPassage(passage);
		practice.setQuestionReplacePattern(questionHTMLTag);
		practice.setQuestionMatchingPattern(questionPattern);
		practice.setAnswerReplacePattern(answerPattern);

		practice.generateQuestions(maxQuestions);
		return practice;
	}

	// ============== Setter / Getter ================//
	public int getMaxQuestions() {return maxQuestions;}
	public void setMaxQuestions(int maxQuestions) {	this.maxQuestions = maxQuestions;}


}
