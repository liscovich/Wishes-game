package edu.harvard.med.hks.dao.hibernate;

import org.springframework.stereotype.Repository;

import edu.harvard.med.hks.dao.FeedbackDao;
import edu.harvard.med.hks.model.Feedback;

@Repository
public class FeedbackDaoImpl extends GenericDaoImpl<Feedback> implements FeedbackDao {
	
}