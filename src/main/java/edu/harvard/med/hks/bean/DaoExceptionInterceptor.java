package edu.harvard.med.hks.bean;

import org.apache.log4j.Logger;
import org.springframework.aop.ThrowsAdvice;

import edu.harvard.med.hks.server.GeneralException;

public class DaoExceptionInterceptor implements ThrowsAdvice {
	private static Logger logger = Logger.getLogger(DaoExceptionInterceptor.class);

	public void afterThrowing(Exception ex) throws GeneralException {
		logger.error(ex.getMessage(), ex);
		throw new GeneralException("Error occur - Please see server log for detail");
	}
}
