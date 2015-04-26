package com.esl.test.service;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import com.esl.entity.News;
import com.esl.service.NewsService;

public class NewsServiceTest {
	NewsService newsService;

	public NewsServiceTest() {
		List<News> enNews = new ArrayList<News>();

		News zhNews1 = new News();
		zhNews1.setId(1l);
		zhNews1.setHtmlURL("2011020401_new_ranking_zh.html");
		News zhNews2 = new News();
		zhNews2.setId(2l);
		News zhNews3 = new News();
		zhNews3.setId(3l);
		List<News> zhNews = Arrays.asList(zhNews1,zhNews2,zhNews3);

		Map<String, List<News>> newsMap = new HashMap<String, List<News>>();
		newsMap.put("zh", zhNews);
		newsMap.put("en", enNews);

		newsService = new NewsService(newsMap);

	}

	@Test
	public void testGetNewsSubListLargeEnd() {
		List<News> result = newsService.getNewsSubList("en", 0, 5);
		assertTrue("result list can be empty", result.size() == 0);

		result = newsService.getNewsSubList("zh", 0, 5);
		assertTrue("result list size can be smaller than input end", result.size() > 0);
	}

	@Test
	public void testGetNewsSubListWithUnSupportedLocale() {
		List<News> result = newsService.getNewsSubList("cn", 0, 0);
		assertNull("Unsuppoerted Locale return null list", result);
	}

	@Test
	public void testGetNewsSubListNormal() {
		List<News> result = newsService.getNewsSubList("zh", 1, 3);
		assertTrue("First one is news 2", result.get(0).getId() == 2);
		assertTrue("Second one is news 3", result.get(1).getId() == 3);
	}

	@Test
	public void testEnrichRetreatHTMLContent() {
		List<News> result = newsService.getNewsSubList("zh", 0, 1);
		newsService.retreatHTMLContents(result);
		assertEquals("Check HTML content read from file", "<div style=\"line-height: 20px;\">",result.get(0).getHtmlContent().get(0));
	}

}
