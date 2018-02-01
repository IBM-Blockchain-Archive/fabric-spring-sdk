/*
 *
 *  Copyright 2017 IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.data.chaincode.repository.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.Channel;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;

/**
 * Method interceptor to invoke methods annotated by {@link ChaincodeInvoke} on the repository proxy.
 * 
 * @author Gennady Laventman
 *
 */
public class InvokeInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(InvokeInterceptor.class);

	private Class<?> repositoryInterface;
	
	private String ccName;
	private String ccVer;
	private String chName;

	private ChaincodeClient chaincodeClient;
	
	public InvokeInterceptor(Class<?> repositoryInterface, ChaincodeClient chaincodeClient) {
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
		ChaincodeInvoke invokeAnnotation = method.getAnnotation(ChaincodeInvoke.class);
		String args[] =  Arrays.stream(arguments).map(arg -> (arg == null?  null : arg.toString())).toArray(size -> new String[size]);
		if (invokeAnnotation != null) {
			try {
				repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
				logger.debug("Invoking {} with argiments {}", method.getName(), arguments);
				return 	chaincodeClient.invokeChaincode(chName, ccName, ccVer, method.getName(), args);
			} catch (NoSuchMethodException e) {
				logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
			}
		}
		
		return invocation.proceed();
	}

}
