package com.esl.test.service.practice;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esl.entity.practice.*;
import com.esl.service.practice.GrammarPracticeService;
import com.esl.util.practice.GrammarPracticeGenerator;

public class GrammarPracticeServiceTest {
	GrammarPracticeService service = new GrammarPracticeService();

	private static GrammarPractice p1 = new PrepositionPractice();

	@BeforeClass static public void setup() {
		p1.setPassage("Huntington Ingalls The Ford is being built by Huntington Ingalls Industries Inc. (HII) under a “cost-plus, incentive-fee” contract in which the Navy pays for most of the overruns. Even so, the service’s efforts to control costs are putting the company’s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn’t disclose Huntington’s share. Huntington Ingalls is continuing “to see improvements in our performance” on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon’s fiscal 2013 budget proposal indicate that the Navy’s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.");
		p1.setQuestions(GrammarPracticeGenerator.retrieveQuestions(p1));
		p1.setQuestionPositions(Arrays.asList(0,1,2,3));
	}

	@Test
	public void testGeneralPrepositionPracticeNormal() {
		GrammarPractice practice = service.generatePracticeByPassage("abc to", GrammarPractice.PracticeType.Preposition, GrammarPractice.QuestionFormat.FillInTheBlank, "<span id=\"question{?}\" class=\"grammarQuestion\">&nbsp;&nbsp;({?})&nbsp;&nbsp;</span>", "", "");

		assertNotNull(practice);
		assertTrue(practice.getQuestions().size() == 1);
		assertTrue(practice.getQuestionPositions().size() == 1);
		assertNotNull(practice.getHtmlPassageWithQuestions());
		assertTrue(practice.getClass().equals(PrepositionPractice.class));
		assertEquals("abc <span id=\"question1\" class=\"grammarQuestion\">&nbsp;&nbsp;(1)&nbsp;&nbsp;</span>",practice.getHtmlPassageWithQuestions());
	}

	@Test
	public void testGeneralPrepositionPracticeNoQuestion() {
		GrammarPractice practice = service.generatePracticeByPassage("abc", GrammarPractice.PracticeType.Preposition, GrammarPractice.QuestionFormat.FillInTheBlank, "", "", "");

		assertNotNull(practice);
		assertTrue(practice.getQuestions().size() == 0);
		assertTrue(practice.getClass().equals(PrepositionPractice.class));
	}

	@Test public void testCheckAnswerAllCorrect() {
		List<Boolean> results = new ArrayList<Boolean>();
		List<String> inputAnswers = Arrays.asList("by","under","in","for");

		assertEquals("check result mark", 4, service.checkAnswer(inputAnswers, results, p1));
		assertEquals("check results size", 4, results.size());
		assertTrue("check results content", results.get(0)); assertTrue("check results content", results.get(1));
		assertTrue("check results content", results.get(2)); assertTrue("check results content", results.get(3));
	}

	@Test public void testCheckAnswerAllEmpty() {
		List<Boolean> results = new ArrayList<Boolean>();
		List<String> inputAnswers = Arrays.asList("","","","");

		assertEquals("check result mark", 0, service.checkAnswer(inputAnswers, results, p1));
		assertEquals("check results size", 4, results.size());
		assertFalse("check results content", results.get(0)); assertFalse("check results content", results.get(1));
		assertFalse("check results content", results.get(2)); assertFalse("check results content", results.get(3));
	}

	@Test public void testCheckAnswerWithSpaceAndCapitalLetter() {
		List<Boolean> results = new ArrayList<Boolean>();
		List<String> inputAnswers = Arrays.asList("  by   "," un der","IN","   foR");

		assertEquals("check result mark", 3, service.checkAnswer(inputAnswers, results, p1));
		assertEquals("check results size", 4, results.size());
		assertTrue("check results content", results.get(0)); assertFalse("check results content", results.get(1));
		assertTrue("check results content", results.get(2)); assertTrue("check results content", results.get(3));
	}

	@Test public void testCheckAnswerAllWrongWithSpecialCharacters() {
		List<Boolean> results = new ArrayList<Boolean>();
		List<String> inputAnswers = Arrays.asList("#$%$","<>?:{","#@:%$L&<^_@#RR","#$%TGfor^&H");

		assertEquals("check result mark", 0, service.checkAnswer(inputAnswers, results, p1));
		assertEquals("check results size", 4, results.size());
		assertFalse("check results content", results.get(0)); assertFalse("check results content", results.get(1));
		assertFalse("check results content", results.get(2)); assertFalse("check results content", results.get(3));
	}

	@Test
	public void testGeneralArticlePracticeNormal() {
		GrammarPractice practice = service.generatePracticeByPassage("It is a practice by the funfunspell", GrammarPractice.PracticeType.Article, GrammarPractice.QuestionFormat.FillInTheBlank, "<span id=\"question{?}\" class=\"grammarQuestion\">&nbsp;&nbsp;({?})&nbsp;&nbsp;</span>", "", "");

		assertNotNull(practice);
		assertTrue(practice.getQuestions().size() == 2);
		assertTrue(practice.getQuestionPositions().size() == 2);
		assertNotNull(practice.getHtmlPassageWithQuestions());
		assertTrue(practice.getClass().equals(ArticlePractice.class));
		assertEquals("It is <span id=\"question1\" class=\"grammarQuestion\">&nbsp;&nbsp;(1)&nbsp;&nbsp;</span> practice by <span id=\"question2\" class=\"grammarQuestion\">&nbsp;&nbsp;(2)&nbsp;&nbsp;</span> funfunspell",practice.getHtmlPassageWithQuestions());
	}

	@Test
	public void testGenerateVerbToBePracticeNormal() {
		GrammarPractice practice = service.generatePracticeByPassage("It is a dog and i have been eat it.", GrammarPractice.PracticeType.VerbToBe, GrammarPractice.QuestionFormat.FillInTheBlank,"<span id=\"question{?}\" class=\"grammarQuestion\">&nbsp;&nbsp;({?})&nbsp;&nbsp;</span>", "", "");

		assertNotNull(practice);
		assertTrue(practice.getQuestions().size() == 2);
		assertTrue(practice.getQuestionPositions().size() == 2);
		assertNotNull(practice.getHtmlPassageWithQuestions());
		assertTrue(practice.getClass().equals(VerbToBePractice.class));
		assertEquals("It <span id=\"question1\" class=\"grammarQuestion\">&nbsp;&nbsp;(1)&nbsp;&nbsp;</span> a dog and i have <span id=\"question2\" class=\"grammarQuestion\">&nbsp;&nbsp;(2)&nbsp;&nbsp;</span> eat it.",practice.getHtmlPassageWithQuestions());
	}

	@Test
	public void testGenerateVerbToBePracticeAllQuestions() {
		GrammarPractice practice = service.generatePracticeByPassage("am are is was were be being been", GrammarPractice.PracticeType.VerbToBe, GrammarPractice.QuestionFormat.FillInTheBlank,"({?})", "", "");

		assertNotNull(practice);
		assertTrue(practice.getQuestions().size() == 8);
		assertTrue(practice.getQuestionPositions().size() == 8);
		assertNotNull(practice.getHtmlPassageWithQuestions());
		assertTrue(practice.getClass().equals(VerbToBePractice.class));
		assertEquals("(1) (2) (3) (4) (5) (6) (7) (8)",practice.getHtmlPassageWithQuestions());
	}

	@Test
	public void testGenerateSubjectPronounPracticeNormal() {
		GrammarPractice practice = service.generatePracticeByPassage("It is a dog and i have been eat it.", GrammarPractice.PracticeType.SubjectPronoun, GrammarPractice.QuestionFormat.FillInTheBlank,"<span id=\"question{?}\" class=\"grammarQuestion\">&nbsp;&nbsp;({?})&nbsp;&nbsp;</span>", "", "");

		assertNotNull(practice);
		assertTrue(practice.getQuestions().size() == 3);
		assertTrue(practice.getQuestionPositions().size() == 3);
		assertNotNull(practice.getHtmlPassageWithQuestions());
		assertTrue(practice.getClass().equals(SubjectPronounPractice.class));
		assertEquals("<span id=\"question1\" class=\"grammarQuestion\">&nbsp;&nbsp;(1)&nbsp;&nbsp;</span> is a dog and <span id=\"question2\" class=\"grammarQuestion\">&nbsp;&nbsp;(2)&nbsp;&nbsp;</span> have been eat <span id=\"question3\" class=\"grammarQuestion\">&nbsp;&nbsp;(3)&nbsp;&nbsp;</span>.",practice.getHtmlPassageWithQuestions());
	}

}
