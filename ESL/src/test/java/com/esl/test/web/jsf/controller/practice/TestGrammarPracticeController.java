package com.esl.test.web.jsf.controller.practice;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.esl.entity.practice.*;
import com.esl.web.jsf.controller.practice.GrammarPracticeController;

public class TestGrammarPracticeController {

	@Test
	public void testSetInputPracticeType() {
		GrammarPracticeController c = new GrammarPracticeController();
		c.setInputPracticeType("PracticeType.Preposition");
		assertEquals(GrammarPractice.PracticeType.Preposition, c.getPracticeType());

		c.setInputPracticeType("PracticeType.Article");
		assertEquals(GrammarPractice.PracticeType.Article, c.getPracticeType());

		c.setInputPracticeType("PracticeType.VerbToBe");
		assertEquals(GrammarPractice.PracticeType.VerbToBe, c.getPracticeType());

		c.setInputPracticeType("PracticeType.SubjectPronoun");
		assertEquals(GrammarPractice.PracticeType.SubjectPronoun, c.getPracticeType());
	}

	@Test
	public void testGetPossibleQuestionsForVerbToBe() {
		GrammarPractice p = new VerbToBePractice();
		List<String> result = p.getSortedPossibleQuestions();

		assertEquals("am", result.get(0));
		assertEquals("are", result.get(1));
		assertEquals("be", result.get(2));
		assertEquals("been", result.get(3));
		assertEquals("being", result.get(4));
		assertEquals("is", result.get(5));
		assertEquals("was", result.get(6));
		assertEquals("were", result.get(7));

	}

	@Test
	public void testGetPossibleQuestionsForArticle() {
		GrammarPractice p = new ArticlePractice();
		List<String> result = p.getSortedPossibleQuestions();

		assertEquals("a", result.get(0));
		assertEquals("an", result.get(1));
		assertEquals("the", result.get(2));
	}
}
