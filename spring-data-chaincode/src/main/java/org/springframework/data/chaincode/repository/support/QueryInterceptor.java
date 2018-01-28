package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeQuery;
import org.springframework.data.chaincode.repository.Channel;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;

public class QueryInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(InvokeInterceptor.class);

	private Class<?> repositoryInterface;

	private String ccName;
	private String ccVer;
	private String chName;

	private ChaincodeClient chaincodeClient;

	public QueryInterceptor(Class<?> repositoryInterface, ChaincodeClient chaincodeClient) {
		this.repositoryInterface = repositoryInterface;
		this.chaincodeClient = chaincodeClient;
		Chaincode annotation = AnnotationUtils.findAnnotation(repositoryInterface, Chaincode.class);
		chName = annotation.channel();
		Channel channel= AnnotationUtils.findAnnotation(repositoryInterface, Channel.class);
		if (channel != null) {
			chName = channel.name();
		}
		ccName = annotation.name();
		ccVer = annotation.version();
	}
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		ChaincodeQuery queryAnnotation = method.getAnnotation(ChaincodeQuery.class);
		String args[] =  Arrays.stream(arguments).map(arg -> (arg == null?  null : arg.toString())).toArray(size -> new String[size]);
		if (queryAnnotation != null) {
			try {
				repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
				logger.debug("Quering {} with argiments {}", method.getName(), args);
				return 	chaincodeClient.invokeQuery(chName, ccName, ccVer, method.getName(), args);
			} catch (NoSuchMethodException e) {
				logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
			}
		}
		return invocation.proceed();
	}

}
