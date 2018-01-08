package org.springframework.data.chaincode.repository.support;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class ChaincodeRepositoryFactory extends RepositoryFactorySupport {


	@Override
	protected Object getTargetRepository(RepositoryInformation metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return null;
	}

	@Override
	public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
