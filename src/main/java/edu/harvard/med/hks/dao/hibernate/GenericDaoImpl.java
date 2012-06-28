package edu.harvard.med.hks.dao.hibernate;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import edu.harvard.med.hks.dao.GenericDao;
import edu.harvard.med.hks.model.AbstractIdentityEntity;
import edu.harvard.med.hks.server.GeneralException;

public class GenericDaoImpl<T extends AbstractIdentityEntity> implements GenericDao<T> {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(GenericDaoImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	public void addObject(T object) throws GeneralException {
		sessionFactory.getCurrentSession().save(object);
	}

	@Override
	public void deleteObject(T object) throws GeneralException {
		sessionFactory.getCurrentSession().delete(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() throws GeneralException {
		return getCriteria().list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAllWithOrder(String orderByProperty, boolean asc) throws GeneralException {
		Criteria criteria = getCriteria();
		if (asc) {
			criteria.addOrder(Order.asc(orderByProperty));
		} else {
			criteria.addOrder(Order.desc(orderByProperty));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	protected AbstractIdentityEntity getById(Class<?> clazz, Integer id)
			throws GeneralException {
		Criteria criteria = sessionFactory.getCurrentSession()
				.createCriteria(clazz).add(Restrictions.eq("id", id));
		T result = (T) criteria.uniqueResult();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getById(Integer id) throws GeneralException {
		Criteria criteria = getCriteria().add(Restrictions.eq("id", id));
		T result = (T) criteria.uniqueResult();
		return result;
	}

	@Override
	public List<T> getByProperties(Map<String, Object> restrictions)
			throws GeneralException {
		return getByProperties(restrictions, null, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getByProperties(Map<String, Object> restrictions,
			String orderByProperty, boolean asc)
	throws GeneralException {
		Set<String> keySet = restrictions.keySet();
		Criteria criteria = getCriteria();
		for (String key : keySet) {
			if (restrictions.get(key) == null) {
				criteria.add(Restrictions.isNull(key));
			} else {
				criteria.add(Restrictions.eq(key, restrictions.get(key)));
			}
		}
		if (orderByProperty != null) {
			if (asc) {
				criteria.addOrder(Order.asc(orderByProperty));
			} else {
				criteria.addOrder(Order.desc(orderByProperty));
			}
		}

		return criteria.list();
	}

	@Override
	public List<T> getByProperty(String propertyName, Object propertyValue) {
		return getByProperty(propertyName, propertyValue, null, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getByProperty(String propertyName, Object propertyValue,
			String orderByProperty, boolean asc) {
		Criteria criteria = getCriteria();
		if (propertyValue == null) {
			criteria.add(Restrictions.isNull(propertyName));
		} else {
			criteria.add(Restrictions.eq(propertyName, propertyValue));
		}
		if (orderByProperty != null) {
			if (asc) {
				criteria.addOrder(Order.asc(orderByProperty));
			} else {
				criteria.addOrder(Order.desc(orderByProperty));
			}
		}
		return criteria.list();
	}

	protected Criteria getCriteria() {
		Session session = sessionFactory.getCurrentSession();
		Class<T> genericClass = getGenericClass();
		Criteria criteria = session.createCriteria(genericClass);
		return criteria;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getGenericClass() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass()
			.getGenericSuperclass();
		return (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void update(T object) throws GeneralException {
		sessionFactory.getCurrentSession().merge(object);
	}
}