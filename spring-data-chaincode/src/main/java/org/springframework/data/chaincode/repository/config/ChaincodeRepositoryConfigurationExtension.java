package org.springframework.data.chaincode.repository.config;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.repository.support.ChaincodeRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;

public class ChaincodeRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryConfigurationExtension.class);
	
	@Override
	protected String getModulePrefix() {
		return "chaincode";
	}
	
	@Override
	public String getModuleName() {
		return "Chaincode";
	}
	
	@Override
	public String getRepositoryFactoryBeanClassName() {
		logger.debug("getRepositoryFactoryBeanClassName return " + ChaincodeRepositoryFactoryBean.class.getName());
		return ChaincodeRepositoryFactoryBean.class.getName();
	}
	
	@Override
	protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
		logger.debug("getIdentifyingAnnotations return {" + Chaincode.class.getName() + "}");
		return Collections.singleton(Chaincode.class);
	}
	
	@Override
	protected Collection<Class<?>> getIdentifyingTypes() {
		logger.debug("getIdentifyingTypes return {" + ChaincodeRepository.class.getName() + "}");
		return Collections.singleton(ChaincodeRepository.class);
	}
	
	@Override
	public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
			T configSource, ResourceLoader loader, boolean strictMatchesOnly) {
		logger.debug("getRepositoryConfigurations for type " + configSource.getClass().getName());
		return super.getRepositoryConfigurations(configSource, loader, strictMatchesOnly);
	}
	
	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry,
			RepositoryConfigurationSource configurationSource) {
		logger.debug("registerBeansForRoot ");
		super.registerBeansForRoot(registry, configurationSource);
	}
	
	@Override
	protected <T extends RepositoryConfigurationSource> RepositoryConfiguration<T> getRepositoryConfiguration(
			BeanDefinition definition, T configSource) {
		logger.debug("getRepositoryConfiguration for " + definition.getBeanClassName() + " " + definition.getFactoryBeanName() + " " + definition.getFactoryMethodName());
		return super.getRepositoryConfiguration(definition, configSource);
	}

	@Override
	public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
			T configSource, ResourceLoader loader) {
		logger.debug("getRepositoryConfigurations ");
		return super.getRepositoryConfigurations(configSource, loader);
	}
}
