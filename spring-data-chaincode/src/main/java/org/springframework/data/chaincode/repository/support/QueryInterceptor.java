package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeQuery;
import org.springframework.data.chaincode.repository.sdk.client.ChaincodeClient;

public class QueryInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(InvokeInterceptor.class);

	private Class<?> repositoryInterface;
	private Chaincode annotation;

	private ChaincodeClient chaincodeClient;

	public QueryInterceptor(Class<?> repositoryInterface, ChaincodeClient chaincodeClient) {
		this.repositoryInterface = repositoryInterface;
		this.chaincodeClient = chaincodeClient;
		this.annotation = repositoryInterface.getAnnotation(Chaincode.class);		
	}
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		String ccName = annotation.name();
		String channel = annotation.channel();
		String ccVer = annotation.version();
		ChaincodeQuery queryAnnotation = method.getAnnotation(ChaincodeQuery.class);
		String args[] =  Arrays.stream(arguments).map(arg -> (arg == null?  null : arg.toString())).toArray(size -> new String[size]);
		if (queryAnnotation != null) {
			try {
				repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
				logger.debug("Quering {} with argiments {}", method.getName(), args);
				return 	chaincodeClient.invokeQuery(channel, ccName, ccVer, method.getName(), args);
			} catch (NoSuchMethodException e) {
				logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
			}
		}
		return invocation.proceed();
	}

}
