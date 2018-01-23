package org.springframework.data.chaincode.repository.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.repository.sdk.client.ChaincodeClient;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.transaction.interceptor.TransactionalProxy;

public class ChaincodeRepositoryFactory extends RepositoryFactorySupport {
	private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryFactory.class);

	private ClassLoader classLoader;
	
	private SimpleChaincodeRepository<?, ?> targetRepository;
	private Class<?> repositoryInterface;

	private ChaincodeClient chaincodeClient;

	ChaincodeRepositoryFactory(Class<?> repositoryInterface, ChaincodeClient chaincodeClient) {
		super();
		this.chaincodeClient = chaincodeClient;
		logger.debug("Creating chaincode bean factory");
		this.classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
		this.repositoryInterface = repositoryInterface;
		Chaincode annotation = repositoryInterface.getAnnotation(Chaincode.class);
		this.targetRepository = new SimpleChaincodeRepository<>(annotation.channel(), annotation.name(), annotation.version());
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
		
		DefaultInterceptor defaultInterceptor = new DefaultInterceptor(targetRepository, repositoryInterface, chaincodeClient);
		InvokeInterceptor invokeInterceptor = new InvokeInterceptor(repositoryInterface, chaincodeClient);
		QueryInterceptor queryInterceptor = new QueryInterceptor(repositoryInterface, chaincodeClient);
		
		result.addAdvice(invokeInterceptor);
		result.addAdvice(queryInterceptor);
		result.addAdvice(defaultInterceptor);
		
		return (T)result.getProxy(this.classLoader);

	}
	
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		logger.debug("Setting bean class oader");
		this.classLoader = classLoader;
		super.setBeanClassLoader(classLoader);
	}
	
}
