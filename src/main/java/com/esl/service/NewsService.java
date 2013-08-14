package com.esl.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.esl.dao.INewsDAO;
import com.esl.entity.News;
import com.esl.enumeration.ESLSupportedLocale;

@Service("newsService")
public class NewsService {
	// Logging
	private static Logger log = LoggerFactory.getLogger("ESL");
	private static String htmlFilesFolder = "/com/esl/news/html/";

	private Map<String, List<News>> newsMap;

	// Supporting class
	@Resource private INewsDAO newsDAO;

	// ============== Setter / Getter ================//
	public void setNewsDAO(INewsDAO newsDAO) { this.newsDAO = newsDAO; }


	// ============== Constructor ================//
	public NewsService() {}

	public NewsService(Map<String, List<News>> newsMap) {
		this.newsMap = newsMap;
	}

	@PostConstruct
	public void enrichNewsMap() {
		newsMap = new HashMap<String, List<News>>();

		synchronized (newsMap) {
			for (ESLSupportedLocale localeEnum : ESLSupportedLocale.values()) {
				String locale = localeEnum.toString();
				log.info("Get all news with locale [{}]", locale);

				List<News> result = newsDAO.listOrderedNews(locale);
				newsMap.put(locale, result);
				log.info("Total news for locale [{}]: {}", locale, result.size());

				log.info("Retreat HTML Contents");
				retreatHTMLContents(result);
			}
		}
	}


	public void retreatHTMLContents(List<News> result) {
		for (News news : result) {
			try {
				File file = new File( getClass().getResource("/com/esl/news/html/" + news.getHtmlURL()).getFile());
				news.setHtmlContent(FileUtils.readLines(file, "utf-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ============== Functions ================//
	public List<News> getNewsSubList(String locale, int start, int end) {
		log.debug("getNewsSubList: input locale [" + locale  + "], start [{}], end [{}]", start, end);

		if (!StringUtils.hasText(locale)) return null;

		List<News> result = newsMap.get(locale);
		if (result == null || result.size() == 0) {
			log.debug("No news with input locale");
			return result;
		}

		if (start < 0) start = 0;
		if (end < 0 || end < start || end > result.size()) end = result.size();

		return result.subList(start, end);
	}

}
