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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeQuery;
import org.springframework.data.chaincode.repository.Channel;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * {@link RepositoryQuery} implementation for Chaincode, WIP
 *
 * @author Gennady Laventman
 */
public class ChaincodeRepositoryQuery implements RepositoryQuery {
    private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryQuery.class);

    private ChaincodeClient chaincodeClient;
    private Method method;
    private RepositoryMetadata metadata;
    private ProjectionFactory factory;
    private NamedQueries namedQueries;

    private Class<?> repositoryInterface;

    private String ccName;
    private String ccVer;
    private String chName;


    public ChaincodeRepositoryQuery(ChaincodeClient chaincodeClient, Method method, RepositoryMetadata metadata,
                                    ProjectionFactory factory, NamedQueries namedQueries) {
        logger.debug("Creating repository query for method {}, domain class {}, repository interface {}", method.getName(), metadata.getDomainType().getName(), metadata.getRepositoryInterface());
        this.chaincodeClient = chaincodeClient;
        this.method = method;
        this.metadata = metadata;
        this.factory = factory;
        this.namedQueries = namedQueries;

        this.repositoryInterface = metadata.getRepositoryInterface();
        Chaincode annotation = AnnotationUtils.findAnnotation(repositoryInterface, Chaincode.class);
        chName = annotation.channel();
        Channel channel = AnnotationUtils.findAnnotation(repositoryInterface, Channel.class);
        if (channel != null) {
            chName = channel.name();
        }
        ccName = annotation.name();
        ccVer = annotation.version();
    }

    @Override
    public Object execute(Object[] parameters) {
        logger.debug("Executing method {} with params {} for repo {}, domain class {}", method.getName(), parameters, metadata.getRepositoryInterface().getName(), metadata.getReturnedDomainClass(method));
        ChaincodeInvoke invokeAnnotation = AnnotationUtils.findAnnotation(method, ChaincodeInvoke.class);
        ChaincodeQuery queryAnnotation = AnnotationUtils.findAnnotation(method, ChaincodeQuery.class);

        String args[] = Arrays.stream(parameters).map(arg -> (arg == null ? null : arg.toString())).toArray(size -> new String[size]);

        if (invokeAnnotation != null) {
            try {
                repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
                logger.debug("Invoking {} ", method.getName());
                return chaincodeClient.invokeChaincode(chName, ccName, ccVer, method.getName(), args);
            } catch (NoSuchMethodException e) {
                logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
            }
        } else if (queryAnnotation != null) {
            try {
                repositoryInterface.getMethod(method.getName(), method.getParameterTypes());
                logger.debug("Querying {} ", method.getName());
                return chaincodeClient.invokeQuery(chName, ccName, ccVer, method.getName(), args);
            } catch (NoSuchMethodException e) {
                logger.debug("Can't find method {} with arguments {}", method.getName(), method.getParameterTypes());
            }
        }
        return null;
    }

    @Override
    public QueryMethod getQueryMethod() {
        logger.debug("Get query method for method {}, repository {}", method.getName(), metadata.getRepositoryInterface().getName());
        return new QueryMethod(method, metadata, factory);
    }

}
