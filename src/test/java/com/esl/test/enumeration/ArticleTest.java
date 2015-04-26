package com.esl.test.enumeration;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.enumeration.Article;
import com.esl.test.dao.DictationDaoTest;

public class ArticleTest {
	Logger log = LoggerFactory.getLogger(DictationDaoTest.class);

	@Test
	public void testArticleRegex() {
		log.debug("Article regex [{}]", Article.ARTICLE_REGEX);
		Pattern p = Pattern.compile(Article.ARTICLE_REGEX);
		String[] result = p.split("I an in front of the bus inside a tree.");
		assertEquals("I ", result[0]);
		assertEquals(" in front of ",result[1]);
		assertEquals(" bus inside ",result[2]);
		assertEquals(" tree.",result[3]);
	}
}
