package com.esl.web.jsf.controller;

import com.esl.dao.INewsDAO;
import com.esl.dao.NewsDAO;
import com.esl.entity.News;
import com.esl.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Controller
@Scope("session")
public class NewsController extends ESLController {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(NewsController.class);

	private static String singleNewsView = "/public/news/singlenews";
	private static String newsView = "/public/news/news";
	private static int NEWS_PER_PAGE = 5;

	//	 Supporting instance
	@Resource private INewsDAO newsDAO;
	@Resource private NewsService newsService;

	// ============== UI display data ================//
	private News selectedNews;
	private Long selectedNewsId;
	private String selectedNewsContent;

	private int pageNumber = 0;
	private boolean hasMoreNews;
	private List<News> newsList;

	// ============== Setter / Getter ================//
	public void setNewsDAO(INewsDAO dao) {this.newsDAO = dao; }

	public News getSelectedNews() {	return selectedNews;}
	public void setSelectedNews(News selectedNews) {this.selectedNews = selectedNews;}

	public Long getSelectedNewsId() {return selectedNewsId;	}
	public void setSelectedNewsId(Long selectedNewsId) {this.selectedNewsId = selectedNewsId;}

	public int getPageNumber() {return pageNumber;}
	public void setPageNumber(int pageNumber) {	this.pageNumber = pageNumber;}

	public boolean isHasMoreNews() {return hasMoreNews;}
	public void setHasMoreNews(boolean hasMoreNews) {this.hasMoreNews = hasMoreNews;}

	public List<News> getNewsList() {return newsList;}
	public void setNewsList(List<News> newsList) {this.newsList = newsList;}

	// ============== Getter Function ================//
	public List<News> getOrderedNews() {
		logger.info("getOrderedNews: START");
		String locale = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		logger.info("getOrderedNews: locale[" + locale + "]");
		List<News> l =newsDAO.listOrderedNews(locale);
		logger.info("getOrderedNews: returned news size[" + l.size() + "]");

		return l;
	}

	public List<News> getLatestNews() {
		logger.info("getLatestNews: START");
		String locale = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		logger.info("getLatestNews: locale[" + locale + "]");
		List<News> l =newsService.getNewsSubList(locale, 0, NewsDAO.MAX_LATEST_NEWS);
		logger.info("getLatestNews: returned news size[" + l.size() + "]");
		return l;
	}

	public String getSelectedNewsContent() {
		return selectedNewsContent;
	}

	// ============== Functions ================//

	/**
	 * Request by url get for display single news
	 */
	public String viewSingleNews() {
		logger.info("viewSingleNews: START");
		logger.info("viewSingleNews: input new id[" + selectedNewsId + "]");

		selectedNews = newsDAO.getById(selectedNewsId);
		logger.info("viewSingleNews: selected news [" + selectedNews + "]");

		String filePath = "/com/esl/news/html/" + selectedNews.getHtmlURL();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)));
			String line = reader.readLine();
			selectedNewsContent = "";
			while (line != null) {
				selectedNewsContent += line;
				line = reader.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (selectedNews == null)
			return errorView;
		else
			return singleNewsView;
	}

	public String viewNews() {
		String locale = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		logger.info("viewNews: page [{}], locale [{}]", pageNumber, locale);

		// Get one more news to check has more
		newsList = newsService.getNewsSubList(locale, pageNumber * NEWS_PER_PAGE, (pageNumber + 1) * NEWS_PER_PAGE + 1);
		if (newsList.size() > NEWS_PER_PAGE) {
			hasMoreNews = true;
			newsList.remove(newsList.size() - 1);
		} else {
			hasMoreNews = false;
		}

		return newsView;
	}


}
