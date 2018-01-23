package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.sdk.client.ChaincodeClient;

public class InvokeInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(InvokeInterceptor.class);

	private Class<?> repositoryInterface;
	private Chaincode annotation;

	private ChaincodeClient chaincodeClient;
	
	public InvokeInterceptor(Class<?> repositoryInterface, ChaincodeClient chaincodeClient) {
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
		ChaincodeInvoke invokeAnnotation = method.getAnnotation(ChaincodeInvoke.class);
		String args[] =  Arrays.stream(arguments).map(arg -> (arg == null?  null : arg.toString())).toArray(size -> new String[size]);
		if (invokeAnnotation != null) {
			try {
				repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
				logger.debug("Invoking {} with argiments {}", method.getName(), arguments);
				return 	chaincodeClient.invokeChaincode(channel, ccName, ccVer, method.getName(), args);
			} catch (NoSuchMethodException e) {
				logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
			}
		}
		
		return invocation.proceed();
	}

}
