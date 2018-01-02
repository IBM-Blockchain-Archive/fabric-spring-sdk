package org.springframework.data.chaincode.repository.config;

import java.lang.annotation.Annotation;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class ChaincodeRepositoryRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableChaincodeRepositories.class;
	}

	@Override
	protected RepositoryConfigurationExtension getExtension() {
		// TODO Auto-generated method stub
		return new ChaincodeRepositoryConfigurationExtension();
	}

}
