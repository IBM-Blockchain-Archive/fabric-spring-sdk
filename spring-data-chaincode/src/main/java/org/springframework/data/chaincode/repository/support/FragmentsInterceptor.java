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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;

/**
 * Method interceptor to invoke methods defined in {@link RepositoryFragments} on repository proxy.
 * 
 * @author Gennady Laventman
 *
 */
public class FragmentsInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(FragmentsInterceptor.class);
	
	private RepositoryFragments fragments;
	
	public FragmentsInterceptor(RepositoryFragments fragments) {
		this.fragments = fragments;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();

		try {
			return fragments.invoke(method, arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.debug("Can't find fragment for method {} with arguments {}, exception {}", method.getName(), method.getParameterTypes(), e);
		}
		return invocation.proceed();
	}

}
