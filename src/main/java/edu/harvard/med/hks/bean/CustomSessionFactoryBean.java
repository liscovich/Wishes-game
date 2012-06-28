package edu.harvard.med.hks.bean;

import java.util.ArrayList;

import javax.persistence.Entity;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

public class CustomSessionFactoryBean extends AnnotationSessionFactoryBean {
	private static Logger logger = Logger.getLogger(CustomSessionFactoryBean.class);

	@Override
	protected SessionFactory buildSessionFactory() throws Exception {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		// the following will detect all classes that are annotated as @Entity
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

		// only register classes within "edu.harvard.med.hks.model" package
		for (BeanDefinition bd : scanner.findCandidateComponents("edu.harvard.med.hks.model")) {
			String name = bd.getBeanClassName();
			try {
				classes.add(Class.forName(name));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		// register detected classes with AnnotationSessionFactoryBean
		setAnnotatedClasses(classes.toArray(new Class[classes.size()]));
		return super.buildSessionFactory();
	}

}
