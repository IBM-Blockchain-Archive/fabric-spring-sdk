package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;

public class FragmentsInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(FragmentsInterceptor.class);
	
	private RepositoryFragments fragments;
	private Class<?> repositoryInterface;
	
	public FragmentsInterceptor(Class<?> repositoryInterface, RepositoryFragments fragments) {
		this.repositoryInterface = repositoryInterface;
		this.fragments = fragments;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();

		try {
			return fragments.invoke(method, arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.debug("Can't find fragment for method {} with arguments {}, {}", method.getName(), method.getParameterTypes(), e);
		}
		return invocation.proceed();
	}

}
