package org.springframework.data.chaincode.repository.config;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.repository.support.ChaincodeRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

public class ChaincodeRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

	@Override
	protected String getModulePrefix() {
		return "chaincode";
	}
	
	@Override
	public String getModuleName() {
		return "Chaincode";
	}
	
	@Override
	public String getRepositoryFactoryBeanClassName() { return ChaincodeRepositoryFactoryBean.class.getName();}
	
	@Override
	protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
		return Collections.singleton(Chaincode.class);
	}
	
	@Override
	protected Collection<Class<?>> getIdentifyingTypes() {
		return Collections.singleton(ChaincodeRepository.class);
	}

}
