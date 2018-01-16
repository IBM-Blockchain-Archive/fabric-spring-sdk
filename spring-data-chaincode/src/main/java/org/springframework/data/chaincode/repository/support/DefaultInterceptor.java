package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(DefaultInterceptor.class);


	private SimpleChaincodeRepository<?, ?> simpleChaincodeRepository;
	private Class<?> repositoryInterface;
	
	public DefaultInterceptor(SimpleChaincodeRepository<?, ?> simpleChaincodeRepository, Class<?> repositoryInterface) {
		this.simpleChaincodeRepository = simpleChaincodeRepository;
		this.repositoryInterface = repositoryInterface;
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		try {
			Method defaultImplMethod = simpleChaincodeRepository.getClass().getMethod(method.getName(), method.getParameterTypes());
			logger.debug("Invoking {} with argiments {}", method.getName(), arguments);
			return defaultImplMethod.invoke(simpleChaincodeRepository, arguments);
		} catch (NoSuchMethodException e) {
			logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
		}
		return invocation.proceed();
	}

}
