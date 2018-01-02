package org.springframework.data.chaincode.repository.config;

import org.springframework.data.chaincode.repository.support.ChaincodeRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

public class ChaincodeRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

	@Override
	public String getRepositoryFactoryClassName() {
		return ChaincodeRepositoryFactoryBean.class.getName();
	}

	@Override
	protected String getModulePrefix() {
		return "chaincode";
	}
	
	@Override
	public String getModuleName() {
		return "Chaincode";
	}

}
