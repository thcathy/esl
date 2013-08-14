package com.esl.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.entity.News;
import com.esl.enumeration.ESLSupportedLocale;

@Transactional
@Repository("newsDAO")
public class NewsDAO extends ESLDao<News> implements INewsDAO {
	private static Logger logger = Logger.getLogger("ESL");
	public static int MAX_LATEST_NEWS = 10;

	@Value("${NewsDAO.MaxLatestNews}") public void setMaxLatestNews(int maxLatestNews) { MAX_LATEST_NEWS = maxLatestNews;}

	public NewsDAO() {}

	public News getById(Long id) {
		return (News) sessionFactory.getCurrentSession().get(News.class, id);
	}

	/**
	 * Return the lastest news up to number MAX_LATEST_NEWS
	 */
	@SuppressWarnings("unchecked")
	public List<News> listLatestNews(String locale) {
		logger.info("listLatestNews: locale[" + locale + "]");
		String queryStr = "from News n where (n.deadline is null or n.deadline >= :now) and n.locale = :locale order by n.createdDate desc";
		Session s = sessionFactory.getCurrentSession();
		Query query = s.createQuery(queryStr).setParameter("now", new Date()).setParameter("locale", ESLSupportedLocale.valueOf(locale));
		query.setMaxResults(MAX_LATEST_NEWS);
		return query.list();
	}

	@Override
	public void persist(News news) {
		sessionFactory.getCurrentSession().saveOrUpdate(news);
		logger.info("persist: News.Title[" + news.getTitle() + "] is saved");
	}

	public void transit(News news) {
		sessionFactory.getCurrentSession().delete(news);
		logger.info("transit: news.Title[" + news.getTitle() + "] is deleted");
	}

	@SuppressWarnings("unchecked")
	public List<News> listOrderedNews(String locale) {
		String queryStr = "from News n where (n.deadline is null or n.deadline >= :now) and n.locale = :locale order by n.createdDate desc";
		return sessionFactory.getCurrentSession().createQuery(queryStr).setParameter("now", new Date()).setParameter("locale", ESLSupportedLocale.valueOf(locale)).list();
	}


}
