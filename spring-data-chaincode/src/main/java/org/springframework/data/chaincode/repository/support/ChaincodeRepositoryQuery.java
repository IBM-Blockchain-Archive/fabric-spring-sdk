/*
 *
 *  Copyright 2017 IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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

/**
 * {@link RepositoryQuery} implementation for Chaincode, WIP
 * 
 * @author Gennady Laventman
 *
 */
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
