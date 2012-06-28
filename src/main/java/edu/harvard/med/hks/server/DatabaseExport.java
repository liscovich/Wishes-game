package edu.harvard.med.hks.server;

import java.util.Properties;

import javax.persistence.Entity;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import edu.harvard.med.hks.bean.CustomNamingStrategy;

/**
 * Generate Schema DDL script from the configuration file hibernate.cfg.xml.
 */
public class DatabaseExport {

	public static void main(String[] args) throws Exception {
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.configure("hibernate.cfg.xml");
		Properties props = new Properties();
		props.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
		props.put(Environment.URL, "jdbc:mysql://localhost:3306/hcp");
		props.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
		props.put(Environment.USER, "root");
		props.put(Environment.PASS, "root");
		cfg.addProperties(props);
		cfg.setNamingStrategy(new CustomNamingStrategy());

		// the following will detect all classes that are annotated as @Entity
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		// only register classes within "edu.harvard.med.hcp.model" package
		for (BeanDefinition bd : scanner.findCandidateComponents("edu.harvard.med.hcp.model")) {
			String name = bd.getBeanClassName();
			try {
				cfg.addAnnotatedClass(Class.forName(name));
			} catch (Exception e) {
			}
		}
		cfg.addProperties(props);

		SchemaExport export = new SchemaExport(cfg);
		export.setDelimiter(";");
		export.setHaltOnError(true);
		export.setOutputFile("src/main/resources/ddl.sql");
		export.setFormat(true);
		export.setImportFile("import.sql");
		export.execute(true, false, false, false);
	}
}
