package org.springframework.data.chaincode.repository.support;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryMethod;

public class ChaincodeRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
		extends RepositoryFactoryBeanSupport<T, S, ID> {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryFactoryBean.class);
	
	Class<? extends T> repositoryInterface;

	private RepositoryFactorySupport factory;

	private T repository;
	

	protected ChaincodeRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
		logger.debug("Creating FactoryBean for class " + repositoryInterface.getName());
		this.repositoryInterface = repositoryInterface;
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		logger.debug("Getting factory for class " + repositoryInterface.getName());
		return new ChaincodeRepositoryFactory(repositoryInterface);
	}
	
	@Override
	public void afterPropertiesSet() {
		logger.debug("After properties set for factory bean " + repositoryInterface.getName());
		factory = createRepositoryFactory();
		repository = factory.getRepository(repositoryInterface);
	}
	
	@Override
	public T getObject() {
		logger.debug("getObject for factory bean " + repositoryInterface.getName());
		return repository;
	}
	
	@Override
	public Class<? extends T> getObjectType() {
		logger.debug("getObjectType for factory bean " + repositoryInterface.getName());
		return repositoryInterface;
	}
	
	@Override
	public List<QueryMethod> getQueryMethods() {
		logger.debug("getQueryMethods for factory bean " + repositoryInterface.getName());
		return super.getQueryMethods();
	}
	
	@Override
	public RepositoryInformation getRepositoryInformation() {
		logger.debug("getRepositoryInformation for factory bean " + repositoryInterface.getName());
		return super.getRepositoryInformation();
	}
	
	public Class<? extends T> getRepositoryInterface() {
		logger.debug("getRepositoryInterface for factory bean " + repositoryInterface.getName());
		return repositoryInterface;
	}
	
	@Override
	public boolean isSingleton() {
		logger.debug("isSingleton for factory bean " + repositoryInterface.getName());
		return super.isSingleton();
	}
		
}
