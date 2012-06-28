package edu.harvard.med.hks.bean;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

public class InterfaceBeanNameGenerator implements BeanNameGenerator {

	@Override
	public String generateBeanName(BeanDefinition definition,
			BeanDefinitionRegistry registry) {
		ScannedGenericBeanDefinition scannedDef = (ScannedGenericBeanDefinition) definition;
		String[] interfaceNames = scannedDef.getMetadata().getInterfaceNames();
		String beanName;
		if (interfaceNames.length > 0) {
			beanName = interfaceNames[0];
		} else {
			beanName = scannedDef.getBeanClassName();
		}
		beanName = beanName.substring(beanName.lastIndexOf('.') + 1);
		beanName = beanName.substring(0, 1).toLowerCase()
			+ beanName.substring(1);
		return beanName;
	}
}
