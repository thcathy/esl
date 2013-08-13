package com.esl.test.util.practice;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.entity.practice.*;
import com.esl.test.dao.TestDictationDao;
import com.esl.util.practice.GrammarPracticeGenerator;

public class TestGrammarPracticeGenerator {
	Logger log = LoggerFactory.getLogger(TestDictationDao.class);
	private static GrammarPractice p1 = new PrepositionPractice();
	private static GrammarPractice article1 = new ArticlePractice();

	@BeforeClass static public void setup() {
		p1.setPassage("Huntington Ingalls The Ford is being built by Huntington Ingalls Industries Inc. (HII) under a “cost-plus, incentive-fee” contract in which the Navy pays for most of the overruns. Even so, the service’s efforts to control costs are putting the company’s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn’t disclose Huntington’s share. Huntington Ingalls is continuing “to see improvements in our performance” on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon’s fiscal 2013 budget proposal indicate that the Navy’s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.");
		p1.setQuestions(GrammarPracticeGenerator.retrieveQuestions(p1));
		article1.setPassage("Huntington Ingalls The Ford is being built by Huntington Ingalls Industries Inc. (HII) under a “cost-plus, incentive-fee” contract in which the Navy pays for most of the overruns. Even so, the service’s efforts to control costs are putting the company’s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn’t disclose Huntington’s share. Huntington Ingalls is continuing “to see improvements in our performance” on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon’s fiscal 2013 budget proposal indicate that the Navy’s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.");
		article1.setQuestions(GrammarPracticeGenerator.retrieveQuestions(article1));
	}


	@Test public void testReplaceQuestionsWithNumber1() {
		String expectedResult = "Huntington Ingalls The Ford is being built __(1)__ Huntington Ingalls Industries Inc. (HII) __(2)__ a &ldquo;cost-plus, incentive-fee&rdquo; contract __(3)__ which the Navy pays for most of the overruns. Even so, the service&rsquo;s efforts to control costs are putting the company&rsquo;s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn&rsquo;t disclose Huntington&rsquo;s share. Huntington Ingalls is continuing &ldquo;to see improvements in our performance&rdquo; on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon&rsquo;s fiscal 2013 budget proposal indicate that the Navy&rsquo;s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.";

		p1.setQuestionPositions(Arrays.asList(0,1,2));
		p1.setQuestionReplacePattern("__({?})__");
		String result = GrammarPracticeGenerator.getHTMLPassageWithQuestionNumber(p1);

		assertEquals(expectedResult, result);

		// test article practice
		article1.setQuestionPositions(Arrays.asList(0,1,2));
		article1.setQuestionReplacePattern("__({?})__");
		result = GrammarPracticeGenerator.getHTMLPassageWithQuestionNumber(article1);
		expectedResult = "Huntington Ingalls __(1)__ Ford is being built by Huntington Ingalls Industries Inc. (HII) under __(2)__ &ldquo;cost-plus, incentive-fee&rdquo; contract in which __(3)__ Navy pays for most of the overruns. Even so, the service&rsquo;s efforts to control costs are putting the company&rsquo;s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn&rsquo;t disclose Huntington&rsquo;s share. Huntington Ingalls is continuing &ldquo;to see improvements in our performance&rdquo; on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon&rsquo;s fiscal 2013 budget proposal indicate that the Navy&rsquo;s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.";
		assertEquals(expectedResult, result);
	}

	@Test public void testReplaceQuestionsWithNumber2() {
		String expectedResult = "Huntington Ingalls The Ford is being built by Huntington Ingalls Industries Inc. (HII) under a &ldquo;cost-plus, incentive-fee&rdquo; contract in which the Navy pays for most of the overruns. Even so, the service&rsquo;s efforts to control costs are putting the company&rsquo;s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn&rsquo;t disclose Huntington&rsquo;s share. Huntington Ingalls is continuing &ldquo;to see improvements in our performance&rdquo; on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon&rsquo;s fiscal 2013 budget proposal indicate that the Navy&rsquo;s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent __(1)__ the last year __(2)__ $11.4 billion __(3)__ $10.2 billion.";
		p1.setQuestionPositions(Arrays.asList(21,22,23));
		p1.setQuestionReplacePattern("__({?})__");

		String result = GrammarPracticeGenerator.getHTMLPassageWithQuestionNumber(p1);

		assertEquals(expectedResult, result);
	}

	@Test public void testGetHTMLPassageWithAnswerReplaceWithAnswer() {
		String expectedResult = "Huntington Ingalls The Ford is being built <span id=\"question1\" class=\"grammarQuestion\">_by_</span> Huntington Ingalls Industries Inc. (HII) <span id=\"question2\" class=\"grammarQuestion\">_under_</span> a &ldquo;cost-plus, incentive-fee&rdquo; contract <span id=\"question3\" class=\"grammarQuestion\">_in_</span> which the Navy pays for most of the overruns. Even so, the service&rsquo;s efforts to control costs are putting the company&rsquo;s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn&rsquo;t disclose Huntington&rsquo;s share. Huntington Ingalls is continuing &ldquo;to see improvements in our performance&rdquo; on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon&rsquo;s fiscal 2013 budget proposal indicate that the Navy&rsquo;s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.";

		p1.setQuestionReplacePattern("<span id=\"question{?}\" class=\"grammarQuestion\">&nbsp;&nbsp;({?})&nbsp;&nbsp;</span>");
		p1.setQuestionMatchingPattern("&nbsp;&nbsp;\\(\\d+\\)&nbsp;&nbsp;");
		p1.setAnswerReplacePattern("_{answer}_");

		p1.setQuestionPositions(Arrays.asList(0,1,2));
		p1.setHtmlPassageWithQuestions(GrammarPracticeGenerator.getHTMLPassageWithQuestionNumber(p1));

		String result = GrammarPracticeGenerator.getHTMLPassageWithAnswer(p1);

		assertEquals(expectedResult, result);

	}

	@Test public void testGetHTMLPassageWithAnswerReplaceWithAnswerAndNumber() {
		String expectedResult = "Huntington Ingalls The Ford is being built <span id=\"question1\" class=\"grammarQuestion\">_1 by_</span> Huntington Ingalls Industries Inc. (HII) <span id=\"question2\" class=\"grammarQuestion\">_2 under_</span> a &ldquo;cost-plus, incentive-fee&rdquo; contract <span id=\"question3\" class=\"grammarQuestion\">_3 in_</span> which the Navy pays for most of the overruns. Even so, the service&rsquo;s efforts to control costs are putting the company&rsquo;s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn&rsquo;t disclose Huntington&rsquo;s share. Huntington Ingalls is continuing &ldquo;to see improvements in our performance&rdquo; on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon&rsquo;s fiscal 2013 budget proposal indicate that the Navy&rsquo;s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.";

		p1.setQuestionReplacePattern("<span id=\"question{?}\" class=\"grammarQuestion\">&nbsp;&nbsp;({?})&nbsp;&nbsp;</span>");
		p1.setQuestionMatchingPattern("&nbsp;&nbsp;\\(\\d+\\)&nbsp;&nbsp;");
		p1.setAnswerReplacePattern("_{number} {answer}_");

		p1.setQuestionPositions(Arrays.asList(0,1,2));
		p1.setHtmlPassageWithQuestions(GrammarPracticeGenerator.getHTMLPassageWithQuestionNumber(p1));

		String result = GrammarPracticeGenerator.getHTMLPassageWithAnswer(p1);

		assertEquals(expectedResult, result);

	}


	@Test public void testRandomQuestionPositionsNormal() {
		List<Integer> results = GrammarPracticeGenerator.randomQuestionPositions(p1.getQuestions(), 10);
		assertEquals("Return 10 preposition position", 10, results.size());
		log.debug(results.toString());
	}

	@Test public void testRandomQuestionPositionsLargeMax() {
		List<Integer> results = GrammarPracticeGenerator.randomQuestionPositions(p1.getQuestions(), 10000);
		assertEquals("Return all prepositions", 24, results.size());
		assertEquals("First is pos 0", new Integer(0), results.get(0));
		assertEquals("Last is pos 23", new Integer(23), results.get(23));
	}

	@Test public void testRandomQuestionPositionsBadInput() {
		List<Integer> results = GrammarPracticeGenerator.randomQuestionPositions(null, 100);
		assertEquals("Return empty list", 0, results.size());

		results = GrammarPracticeGenerator.randomQuestionPositions(p1.getQuestions(), 0);
		assertEquals("Return empty list", 0, results.size());
	}

	@Test
	public void testRetrievePrepositionsNormal1() {
		GrammarPractice p = new PrepositionPractice();
		p.setPassage("Leaders of the Senate Armed Services Committee asked Congress’s auditing agency to investigate the rising cost of the aircraft carrier USS Gerald R. Ford, the Navy’s most expensive warship.");
		List<String> results = GrammarPracticeGenerator.retrieveQuestions(p);

		assertEquals("of", results.get(0));
		assertEquals("to", results.get(1));
		assertEquals("of", results.get(2));
	}

	@Test
	public void testRetrievePrepositionsNormal2() {
		GrammarPractice p = new PrepositionPractice();
		p.setPassage("In front of the boy, it is a none possible jump-into o my god!");
		List<String> results = GrammarPracticeGenerator.retrieveQuestions(p);

		assertEquals("In front of", results.get(0));
	}

	@Test
	public void testRetrievePrepositionsNormal3() {
		GrammarPractice p = new PrepositionPractice();
		p.setPassage("Huntington Ingalls The Ford is being built by Huntington Ingalls Industries Inc. (HII) under a “cost-plus, incentive-fee” contract in which the Navy pays for most of the overruns. Even so, the service’s efforts to control costs are putting the company’s $579.2 million profit at risk, according to the Navy. The Navy said Huntington Ingalls of Newport News, Virginia, is being docked millions of dollars in profit because of the cost overrun. It didn’t disclose Huntington’s share. Huntington Ingalls is continuing “to see improvements in our performance” on the carrier, Beci Brenton, a spokeswoman for the company, said in an e-mail this week. Documents submitted this week with the Pentagon’s fiscal 2013 budget proposal indicate that the Navy’s cost estimate for the second carrier in the group, the John F. Kennedy, has also grown about 12 percent in the last year to $11.4 billion from $10.2 billion.");
		List<String> results = GrammarPracticeGenerator.retrieveQuestions(p);

		assertEquals("by", results.get(0));
		assertEquals("under", results.get(1));
		assertEquals("in", results.get(2));
		assertEquals("for", results.get(3));
		assertEquals("of", results.get(4));
		assertEquals("to", results.get(5));
		assertEquals("at", results.get(6));
		assertEquals("to", results.get(7));
		assertEquals("of", results.get(8));
		assertEquals("of", results.get(9));
		assertEquals("in", results.get(10));
		assertEquals("of", results.get(11));
		assertEquals("to", results.get(12));
		assertEquals("in", results.get(13));
		assertEquals("on", results.get(14));
		assertEquals("for", results.get(15));
		assertEquals("in", results.get(16));
		assertEquals("with", results.get(17));
		assertEquals("for", results.get(18));
		assertEquals("in", results.get(19));
		assertEquals("about", results.get(20));
		assertEquals("in", results.get(21));
		assertEquals("to", results.get(22));
		assertEquals("from", results.get(23));
	}
}

