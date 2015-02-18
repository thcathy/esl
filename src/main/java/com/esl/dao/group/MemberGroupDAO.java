package com.esl.dao.group;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.ESLDao;
import com.esl.model.group.MemberGroup;

@Transactional
@Repository("memberGroupDAO")
public class MemberGroupDAO extends ESLDao<MemberGroup> implements IMemberGroupDAO {
	private static Logger logger = LoggerFactory.getLogger(MemberGroupDAO.class);

	public MemberGroupDAO() {}

	public MemberGroup getMemberGroupById(Long id) {
		return (MemberGroup) sessionFactory.getCurrentSession().get(MemberGroup.class, id);
	}

	@Override
	public void persist(MemberGroup group) throws HibernateException {
		sessionFactory.getCurrentSession().saveOrUpdate(group);
		logger.info("makePersistent: memberGroup.Title[" + group.getTitle() + "] is saved");
	}

	public void transit(MemberGroup group) throws HibernateException {
		sessionFactory.getCurrentSession().delete(group);
		logger.info("makeTransient: memberGroup.Title[" + group.getTitle() + "] is deleted");
	}

	public MemberGroup getMemberGroupByTitle(String title) {
		String query = "from MemberGroup g where g.title = :title";
		List result = sessionFactory.getCurrentSession().createQuery(query).setParameter("title", title).list();

		if (result.size() > 0 ) return (MemberGroup) result.get(0);
		else {
			logger.info("Do not find Member Group with title[" + title + "]");
			return null;
		}
	}
}
