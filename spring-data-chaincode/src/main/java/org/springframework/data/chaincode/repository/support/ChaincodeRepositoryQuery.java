package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

public class ChaincodeRepositoryQuery implements RepositoryQuery {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryQuery.class);
	
	private ChaincodeClient chaincodeClient;
	private Method method;
	private RepositoryMetadata metadata;
	private ProjectionFactory factory;
	private NamedQueries namedQueries;

	
	
	public ChaincodeRepositoryQuery(ChaincodeClient chaincodeClient, Method method, RepositoryMetadata metadata,
			ProjectionFactory factory, NamedQueries namedQueries) {
		this.chaincodeClient = chaincodeClient;
		this.method = method;
		this.metadata = metadata;
		this.factory = factory;
		this.namedQueries = namedQueries;
	}

	@Override
	public Object execute(Object[] parameters) {
		logger.debug("Executing {} with params {} for repo {}, domain class {}", method.getName(), parameters, metadata.getRepositoryInterface().getName(), metadata.getReturnedDomainClass(method));
		return null;
	}

	@Override
	public QueryMethod getQueryMethod() {
		logger.debug("Get query method for method {}, repository {}", method.getName(), metadata.getRepositoryInterface().getName());
		return null;
	}

}
