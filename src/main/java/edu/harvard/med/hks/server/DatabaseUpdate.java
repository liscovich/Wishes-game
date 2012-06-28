package edu.harvard.med.hks.server;

import java.util.Properties;

import javax.persistence.Entity;

import org.apache.log4j.Logger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import edu.harvard.med.hks.bean.CustomNamingStrategy;

/**
 * Generate Schema DDL script from the configuration file hibernate.cfg.xml.
 */
public class DatabaseUpdate {
	private static Logger logger = Logger.getLogger(DatabaseUpdate.class);
	public static void main(String[] args) throws Exception {
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.configure("hibernate.cfg.xml");
		Properties props = new Properties();
		props.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
		props.put(Environment.URL, "jdbc:mysql://localhost:3306/hks");
		props.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
		props.put(Environment.USER, "root");
		props.put(Environment.PASS, "root");
		cfg.setNamingStrategy(new CustomNamingStrategy());

		// the following will detect all classes that are annotated as @Entity
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		// only register classes within "edu.harvard.med.hcp.model" package
		for (BeanDefinition bd : scanner.findCandidateComponents("edu.harvard.med.hks.model")) {
			String name = bd.getBeanClassName();
			try {
				cfg.addAnnotatedClass(Class.forName(name));
			} catch (Exception e) {
			}
		}
		cfg.addProperties(props);

		SchemaUpdate update = new SchemaUpdate(cfg);
		update.setDelimiter(";");
		update.setHaltOnError(true);
		update.setOutputFile("src/main/resources/update.sql");
		update.setFormat(true);

		boolean export = args.length == 1 && args[0].equals("export");
		logger.warn("export: " + export);
		update.execute(true, export);
		logger.warn("export successful");
	}
}
