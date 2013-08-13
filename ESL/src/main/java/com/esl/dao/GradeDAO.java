package com.esl.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.model.Grade;

@Transactional
@Repository("gradeDAO")
public class GradeDAO extends ESLDao<Grade> implements IGradeDAO {

	private static final String GET_GRADE_BY_TITLE = "from Grade grade where grade.title = :title";
	private static final String GET_FIRST_LEVEL_GRADE = "from Grade grade where grade.level = (select min(mingrade.level) from Grade mingrade)";

	public GradeDAO() {}

	public Grade getGradeById(Long id) {
		return (Grade) sessionFactory.getCurrentSession().get(Grade.class, id);
	}

	public Grade getGradeByTitle(String title) {
		List result = sessionFactory.getCurrentSession().createQuery(GET_GRADE_BY_TITLE).setParameter("title", title).list();
		if (result.size() > 0)
			return (Grade) result.get(0);
		else {
			Logger.getLogger("ESL").info("Do not find any grade of title:" + title);
			return null;
		}
	}

	public Grade getFirstLevelGrade() {
		List result = sessionFactory.getCurrentSession().createQuery(GET_FIRST_LEVEL_GRADE).list();
		if (result.size() > 0)
			return (Grade) result.get(0);
		else {
			Logger.getLogger("ESL").info("Do not find the First Grade");
			return null;
		}
	}

	public Grade getGradeByLevel(int level) {
		String queryString = "from Grade grade where grade.level = :level";
		List result = sessionFactory.getCurrentSession().createQuery(GET_GRADE_BY_TITLE).setParameter("level", level).list();;
		if (result.size() > 0)
			return (Grade) result.get(0);
		else {
			Logger.getLogger("ESL").info("Cannot find the Grade by level:" + level);
			return null;
		}
	}

	public void makePersistent(Grade grade)	{
		sessionFactory.getCurrentSession().saveOrUpdate(grade);
	}

	public void makeTransient(Grade grade) {
		sessionFactory.getCurrentSession().delete(grade);
	}
}
