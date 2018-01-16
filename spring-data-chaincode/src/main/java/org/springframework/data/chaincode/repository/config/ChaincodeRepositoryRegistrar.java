package org.springframework.data.chaincode.repository.config;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class ChaincodeRepositoryRegistrar extends RepositoryBeanDefinitionRegistrarSupport {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryRegistrar.class);
	
	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableChaincodeRepositories.class;
	}

	@Override
	protected RepositoryConfigurationExtension getExtension() {
		return new ChaincodeRepositoryConfigurationExtension();
	}
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		logger.debug("Starting beans registration");
		for (String beanName : registry.getBeanDefinitionNames()) {
			System.out.println(beanName);
		}
		super.registerBeanDefinitions(annotationMetadata, registry);
		logger.debug("Done beans registration");
		for (String beanName : registry.getBeanDefinitionNames()) {
			System.out.println(beanName);
			BeanDefinition bean = registry.getBeanDefinition(beanName);
			System.out.println(bean.getBeanClassName() + " " + bean.getFactoryBeanName() + " " + bean.getFactoryMethodName());
		}
	}

}
