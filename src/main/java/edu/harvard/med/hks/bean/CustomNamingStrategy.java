package edu.harvard.med.hks.bean;

import org.hibernate.cfg.EJB3NamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.util.StringHelper;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class CustomNamingStrategy extends EJB3NamingStrategy
		implements NamingStrategy {
	@Override
	public String collectionTableName(String ownerEntity,
			String ownerEntityTable, String associatedEntity,
			String associatedEntityTable, String propertyName) {
		return new StringBuffer(ownerEntityTable).append("_")
				.append(associatedEntityTable != null ?
								associatedEntityTable :
								StringHelper.unqualify(propertyName)
				).append("_").append(StringHelper.unqualify(propertyName))
				.toString();
	}
}
