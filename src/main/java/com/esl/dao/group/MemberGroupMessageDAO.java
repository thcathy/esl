package com.esl.dao.group;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.ESLDao;
import com.esl.exception.IllegalParameterException;
import com.esl.model.group.MemberGroup;
import com.esl.model.group.MemberGroupMessage;

@Transactional
@Repository("memberGroupMessageDAO")
public class MemberGroupMessageDAO extends ESLDao<MemberGroupMessage> implements IMemberGroupMessageDAO {
	private static Logger logger = Logger.getLogger("ESL");
	public static int NEW_MESSAGE_DATE_DIFF = 1;

	// ============== Setter / Getter ================//
	@Value("${MemberGroupMessageDAO.NewMessageDateDiff}") public void setNewMessageDateDiff(int dateDiff) {NEW_MESSAGE_DATE_DIFF = dateDiff; }

	public MemberGroupMessageDAO() {}

	public MemberGroupMessage getMemberGroupMessageById(Long id) {
		return (MemberGroupMessage) sessionFactory.getCurrentSession().get(MemberGroupMessage.class, id);
	}

	@Override
	public void persist(MemberGroupMessage msg) throws HibernateException {
		sessionFactory.getCurrentSession().saveOrUpdate(msg);
		logger.info("persist: msg[" + msg.getMessage() + "] is saved");
	}

	public void transit(MemberGroupMessage msg) throws HibernateException {
		sessionFactory.getCurrentSession().delete(msg);
		logger.info("transit: msg[" + msg.getMessage() + "] is deleted");
	}

	/**
	 * check the group have any message within the constant NEW_MESSAGE_DATE_DIFF
	 */
	public boolean haveNewMessage(MemberGroup group) throws IllegalParameterException {
		if (group == null) throw new IllegalParameterException(new String[]{"group"}, new Object[]{group});

		String query = "select new map(count(*) as counter)"
			+ " from MemberGroupMessage m"
			+ " where m.group = :group and m.createdDate > current_date - :dateDiff";
		Query q = sessionFactory.getCurrentSession().createQuery(query).setParameter("group", group).setParameter("dateDiff", NEW_MESSAGE_DATE_DIFF);
		List result = q.list();
		Long counter = (Long)((Map)result.get(0)).get("counter");
		logger.info("haveNewMessage: new messages size[" + counter.intValue() + "]");
		return (counter > 0);
	}

	/**
	 * find all message by group ordered by date desc
	 */
	public List<MemberGroupMessage> listByGroup(MemberGroup group) throws IllegalParameterException {
		if (group == null) throw new IllegalParameterException(new String[]{"group"}, new Object[]{group});

		String queryStr = "SELECT m FROM MemberGroupMessage m WHERE m.group = :group ORDER BY m.createdDate DESC";
		Session s = sessionFactory.getCurrentSession();
		Query query = s.createQuery(queryStr).setParameter("group", group);

		return query.list();
	}
}
