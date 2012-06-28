package edu.harvard.med.hks.bean;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

public class LoggingInterceptor implements MethodBeforeAdvice,
		AfterReturningAdvice, ThrowsAdvice {
	private static Logger logger = null;

	public void afterReturning(Object arg0, Method arg1, Object[] arg2,
			Object arg3) throws Throwable {
		logger = Logger.getLogger(arg3.getClass());
		if (logger.isDebugEnabled()) {
			String message = "Ending method: " + arg1.getName() + ". Return: "
				+ arg0;
			logger.debug(message);
		}
	}

	public void afterThrowing(Method m, Object[] args, Object target,
			Throwable ex) {
		logger = Logger.getLogger(target.getClass());
		logger.debug("Exception in method: " + m.getName() + " Exception is: "
				+ ex.getMessage());
	}

	public void before(Method arg0, Object[] arg1, Object arg2)
			throws Throwable {
		logger = Logger.getLogger(arg2.getClass());
		if (logger.isDebugEnabled()) {
			String message = "Beginning method: " + arg0.getName() + ": ";
			for (int i = 0; i < arg1.length; i++) {
				message += arg1[i] + ", ";
			}
			logger.debug(message);
		}
	}
}
