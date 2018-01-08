package org.springframework.data.chaincode.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class ChaincodeRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
		extends RepositoryFactoryBeanSupport<T, S, ID> {

	protected ChaincodeRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		return new ChaincodeRepositoryFactory();
	}
	
}
