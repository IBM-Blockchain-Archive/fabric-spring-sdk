package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;

public class InvokeInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(InvokeInterceptor.class);

	private Class<?> repositoryInterface;
	public InvokeInterceptor(Class<?> repositoryInterface) {
		this.repositoryInterface = repositoryInterface;
	}
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		ChaincodeInvoke annotation = method.getAnnotation(ChaincodeInvoke.class);
		if (annotation != null) {
			try {
			repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
			logger.debug("Invoking {} with argiments {}", method.getName(), arguments);
			return "invoked";
			} catch (NoSuchMethodException e) {
				logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
			}
		}
		
		return invocation.proceed();
	}

}
