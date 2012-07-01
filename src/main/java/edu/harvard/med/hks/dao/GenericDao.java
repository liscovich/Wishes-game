package edu.harvard.med.hks.dao;

import java.util.List;
import java.util.Map;

import edu.harvard.med.hks.server.GeneralException;

public interface GenericDao<T> {

	void addObject(T object) throws GeneralException;

	void deleteObject(T object) throws GeneralException;

	List<T> getAll() throws GeneralException;

	List<T> getAllWithOrder(String orderByProperty, boolean asc)
			throws GeneralException;

	T getById(Integer id) throws GeneralException;

	List<T> getByProperties(Map<String, Object> restrictions)
			throws GeneralException;

	List<T> getByProperties(Map<String, Object> restrictions, String orderByProperty, boolean asc) throws GeneralException;

	List<T> getByProperty(String propertyName, Object propertyValue);

	List<T> getByProperty(String propertyName, Object propertyValue,
			String orderByProperty, boolean asc) throws GeneralException;

	void update(T object) throws GeneralException;
}
