package com.esl.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.esl.entity.IAuditable;

@Transactional
public abstract class ESLDao<T> implements IESLDao<T> {
	protected Class<?> entityClass;

	@Resource(name="sessionFactory") protected SessionFactory sessionFactory;
	protected EntityManager em;

	public ESLDao() {
		Class<?> clazz = getClass();
		while (!(clazz.getGenericSuperclass() instanceof ParameterizedType))
			clazz = (Class<?>) getClass().getGenericSuperclass();
		this.entityClass = (Class<?>) ((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Hibernate reattach object to session
	 * 
	 * @param practiceResult
	 */
	public Object attachSession(Object o) {
		if (o == null) return null;
		Session session = sessionFactory.getCurrentSession();
		if (!session.contains(o)) {
			session.lock(o, LockMode.NONE);
		}
		return o;
	}

	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	public void persist(Object entity) {
		if (entity instanceof IAuditable) {
			((IAuditable)entity).setLastUpdatedDate(new Date());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(entity);
	}

	public void persistAll(Collection<? extends Object> entities) {
		for (Object entity : entities) {
			if (entity instanceof IAuditable) {
				((IAuditable)entity).setLastUpdatedDate(new Date());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(entity);
		}
	}

	public void refresh(Object entity) {
		sessionFactory.getCurrentSession().refresh(entity);
	}

	@SuppressWarnings("unchecked")
	public T merge(T entity) {
		return (T) sessionFactory.getCurrentSession().merge(entity);
	}

	public void delete(Object entity) {
		sessionFactory.getCurrentSession().delete(entity);
	}
	public void deleteAll(Collection<? extends Object> entities) {
		for (Object entity : entities) {
			sessionFactory.getCurrentSession().delete(entity);
		}
	}

	@SuppressWarnings("unchecked")
	public T get(Serializable id) {
		return (T) sessionFactory.getCurrentSession().get(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return sessionFactory.getCurrentSession().createQuery("From " + entityClass.getName()).list();
	}

}
