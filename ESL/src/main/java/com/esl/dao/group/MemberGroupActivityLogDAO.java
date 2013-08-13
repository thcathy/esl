package com.esl.dao.group;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.ESLDao;
import com.esl.exception.IllegalParameterException;
import com.esl.model.group.MemberGroup;
import com.esl.model.group.MemberGroupActivityLog;

@Transactional
@Repository("memberGroupActivityLogDAO")
public class MemberGroupActivityLogDAO extends ESLDao<MemberGroupActivityLog> implements IMemberGroupActivityLogDAO {

	public MemberGroupActivityLogDAO() {}

	public MemberGroupActivityLog getMemberGroupActivityLogById(Long id) {
		return (MemberGroupActivityLog) sessionFactory.getCurrentSession().get(MemberGroupActivityLog.class, id);
	}

	public List<MemberGroupActivityLog> listByGroup(MemberGroup group) throws IllegalParameterException {
		if (group == null) throw new IllegalParameterException(new String[]{"group"}, new Object[]{group});

		String queryStr = "SELECT logs FROM MemberGroupActivityLog logs WHERE logs.group = :group ORDER BY logs.createdDate DESC";
		Session s = sessionFactory.getCurrentSession();
		Query query = s.createQuery(queryStr).setParameter("group", group);

		return query.list();
	}
}
