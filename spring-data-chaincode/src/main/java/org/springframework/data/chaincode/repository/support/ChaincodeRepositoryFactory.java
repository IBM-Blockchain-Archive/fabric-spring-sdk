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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.repository.Channel;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Factory to create {@link ChaincodeRepository} instances
 *
 * @author Gennady Laventman
 *
 */
public class ChaincodeRepositoryFactory extends RepositoryFactorySupport {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryFactory.class);

	private SimpleChaincodeRepository targetRepository;

	private ChaincodeClient chaincodeClient;

	/**
	 * Create new {@link ChaincodeRepositoryFactory} for given repository interface with given {@link ChaincodeClient}
	 *
	 * @param repositoryInterface - must not be {@literal null}
	 * @param chaincodeClient - must not be {@literal null}
	 */
	ChaincodeRepositoryFactory(Class<?> repositoryInterface, ChaincodeClient chaincodeClient) {
		super();
		this.chaincodeClient = chaincodeClient;
		logger.debug("Creating chaincode bean factory");
		Chaincode annotation = AnnotationUtils.findAnnotation(repositoryInterface, Chaincode.class);
		String channelName = annotation.channel();
		Channel channel= AnnotationUtils.findAnnotation(repositoryInterface, Channel.class);
		if (channel != null) {
			channelName = channel.name();
		}
		this.targetRepository = new SimpleChaincodeRepository(channelName, annotation.name(), annotation.version(), chaincodeClient);
	}

	@Override
	protected Object getTargetRepository(RepositoryInformation metadata) {
			return targetRepository;
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SimpleChaincodeRepository.class;
	}

	@Override
	public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		return null;
	}

	@Override
	public <T> T getRepository(Class<T> repositoryInterface, RepositoryFragments fragments) {
		logger.debug("Creating proxy for {}, fragments {}", repositoryInterface.getSimpleName(), fragments);

		return super.getRepository(repositoryInterface, fragments);
	}

	private class ChaincodeMethodLookupStrategy implements QueryLookupStrategy {

		private ChaincodeClient chaincodeClient;

		public ChaincodeMethodLookupStrategy(ChaincodeClient chaincodeClient) {
			this.chaincodeClient = chaincodeClient;
		}

		@Override
		public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
				NamedQueries namedQueries) {
			logger.debug("Looking query for {}, ProjectionFactory {}, NamedQueries {}", method.getName(), factory, namedQueries);
			return new ChaincodeRepositoryQuery(chaincodeClient, method, metadata, factory, namedQueries);
		}
	}

	@Override
	protected Optional<QueryLookupStrategy> getQueryLookupStrategy(Key key,
			EvaluationContextProvider evaluationContextProvider) {
		logger.debug("getQueryLookupStrategy");
		return Optional.of(new ChaincodeMethodLookupStrategy(chaincodeClient));
	}

//	@Override
//	protected ProjectionFactory getProjectionFactory(ClassLoader classLoader, BeanFactory beanFactory) {
//		ChaincodeProxyProjectionFactory factory = new ChaincodeProxyProjectionFactory();
//		logger.debug("Created project factory {}", factory);
//		factory.setBeanClassLoader(classLoader);
//		factory.setBeanFactory(beanFactory);
//
//		return factory;
//	}

}
