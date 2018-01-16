package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.chaincode.repository.ChaincodeQuery;

public class QueryInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(InvokeInterceptor.class);

	private Class<?> repositoryInterface;

	public QueryInterceptor(Class<?> repositoryInterface) {
		this.repositoryInterface = repositoryInterface;
	}
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		ChaincodeQuery annotation = method.getAnnotation(ChaincodeQuery.class);
		if (annotation != null) {
			try {
			repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
			logger.debug("Quering {} with argiments {}", method.getName(), arguments);
			return "queried";
			} catch (NoSuchMethodException e) {
				logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
			}
		}
		return invocation.proceed();
	}

}
