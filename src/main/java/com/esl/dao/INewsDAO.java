package com.esl.dao;

import java.util.List;

import com.esl.entity.News;

public interface INewsDAO extends IESLDao<News> {
	public News getById(Long id);
	public List<News> listLatestNews(String locale);
	public List<News> listOrderedNews(String locale);
	public void persist(News news);
	public void transit(News news);
}
