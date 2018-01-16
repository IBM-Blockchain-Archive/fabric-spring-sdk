package org.springframework.data.chaincode.repository.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.chaincode.repository.Chaincode;
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

	private InvokeInterceptor invokeInterceptor;
	private QueryInterceptor queryInterceptor;
	private DefaultInterceptor defaultInterceptor;

	ChaincodeRepositoryFactory(Class<?> repositoryInterface) {
		super();
		this.repositoryInterface = repositoryInterface;
		Chaincode annotation = repositoryInterface.getAnnotation(Chaincode.class);
		this.targetRepository = new SimpleChaincodeRepository<>("nochannel", annotation.name(), annotation.version());
		this.defaultInterceptor = new DefaultInterceptor(targetRepository, repositoryInterface);
		this.invokeInterceptor = new InvokeInterceptor(repositoryInterface);
		this.queryInterceptor = new QueryInterceptor(repositoryInterface);

		logger.debug("Creating chaincode bean factory");
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
		logger.debug("Creating proxy for {}", repositoryInterface.getSimpleName());
		ProxyFactory result = new ProxyFactory();
		result.setTarget(repositoryInterface);
		result.setInterfaces(repositoryInterface, Repository.class, TransactionalProxy.class);

		result.addAdvice(invokeInterceptor);
		result.addAdvice(queryInterceptor);
		result.addAdvice(defaultInterceptor);
		
		return (T)result.getProxy();

	}
	
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		super.setBeanClassLoader(classLoader);
	}

}
