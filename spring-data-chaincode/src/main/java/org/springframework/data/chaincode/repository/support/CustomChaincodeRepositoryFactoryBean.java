package org.springframework.data.chaincode.repository.support;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;

public class CustomChaincodeRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> implements InitializingBean, FactoryBean<T>, BeanClassLoaderAware,
		BeanFactoryAware, ApplicationEventPublisherAware {

	private ApplicationEventPublisher publisher;
	private BeanFactory beanFactory;
	private ClassLoader classLoader;
	private Class<? extends T> repositoryInterface;
	private Lazy<T> repository;

	@SuppressWarnings("null")
	protected CustomChaincodeRepositoryFactoryBean(Class<? extends T> repositoryInterface) {

		Assert.notNull(repositoryInterface, "Repository interface must not be null!");
		this.repositoryInterface = repositoryInterface;
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}
	
	@Override
	public T getObject() {
		return this.repository.get();
	}

	@Override
	public Class<?> getObjectType() {
		return repositoryInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
