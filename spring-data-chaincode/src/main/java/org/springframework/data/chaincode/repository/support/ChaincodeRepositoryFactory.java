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

public class ChaincodeRepositoryFactory extends RepositoryFactorySupport {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryFactory.class);

	private ClassLoader classLoader;
	
	private SimpleChaincodeRepository targetRepository;
	private Class<?> repositoryInterface;

	private ChaincodeClient chaincodeClient;

	ChaincodeRepositoryFactory(Class<?> repositoryInterface, ChaincodeClient chaincodeClient) {
		super();
		this.chaincodeClient = chaincodeClient;
		logger.debug("Creating chaincode bean factory");
		this.classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
		this.repositoryInterface = repositoryInterface;
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
