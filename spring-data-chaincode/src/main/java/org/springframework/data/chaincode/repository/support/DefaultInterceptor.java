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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
/**
 * Method interceptor to invoke default methods on the repository proxy.
 * 
 * @author Gennady Laventman
 *
 */
public class DefaultInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(DefaultInterceptor.class);

	private SimpleChaincodeRepository simpleChaincodeRepository;

	public DefaultInterceptor(SimpleChaincodeRepository simpleChaincodeRepository) {
		this.simpleChaincodeRepository = simpleChaincodeRepository;
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
