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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.repository.Channel;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

/**
 * Factory to create {@link ChaincodeRepository} instances
 * 
 * @author Gennady Laventman
 *
 */
public class ChaincodeRepositoryFactory extends RepositoryFactorySupport {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryFactory.class);

	private ClassLoader classLoader;
	
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
		this.classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
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
		ProxyFactory result = new ProxyFactory();
		result.setTarget(repositoryInterface);
		result.setInterfaces(repositoryInterface, ChaincodeRepository.class, Repository.class);
		
		RepositoryMetadata metadata = getRepositoryMetadata(repositoryInterface);
		RepositoryInformation information = getRepositoryInformation(metadata, fragments);
		
		DefaultInterceptor defaultInterceptor = new DefaultInterceptor(targetRepository);
		InvokeInterceptor invokeInterceptor = new InvokeInterceptor(repositoryInterface, chaincodeClient);
		QueryInterceptor queryInterceptor = new QueryInterceptor(repositoryInterface, chaincodeClient);
		FragmentsInterceptor fragmentsInterceptor = new FragmentsInterceptor(fragments); 
		
		result.addAdvice(invokeInterceptor);
		result.addAdvice(queryInterceptor);
		result.addAdvice(defaultInterceptor);
		result.addAdvice(fragmentsInterceptor);
		logger.debug("Repository metadata for {} is {}, information is {}, has custom methods {}", repositoryInterface.getSimpleName(), metadata, information, information.hasCustomMethod());

		return (T)result.getProxy(this.classLoader);

	}
	
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		logger.debug("Setting bean class oader");
		this.classLoader = classLoader;
		super.setBeanClassLoader(classLoader);
	}
	
	private class ChaincodeMethodLookupStrategy implements QueryLookupStrategy {
		
		private ChaincodeClient chaincodeClient;
		
		public ChaincodeMethodLookupStrategy(ChaincodeClient chaincodeClient) {
			this.chaincodeClient = chaincodeClient;
		}

		@Override
		public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
				NamedQueries namedQueries) {
			logger.debug("Looking query for {}", method.getName());
			return new ChaincodeRepositoryQuery(chaincodeClient, method, metadata, factory, namedQueries);
		}
	}

	@Override
	protected Optional<QueryLookupStrategy> getQueryLookupStrategy(Key key,
			EvaluationContextProvider evaluationContextProvider) {
		logger.debug("getQueryLookupStrategy");
		return Optional.of(new ChaincodeMethodLookupStrategy(chaincodeClient));
	}
	
}
