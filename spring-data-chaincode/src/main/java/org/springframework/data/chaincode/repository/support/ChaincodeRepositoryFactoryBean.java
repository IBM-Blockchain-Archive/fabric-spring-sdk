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
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

/**
 * {@Link FactoryBean} to create {@link ChaincodeRepository} instances
 *
 * @author Gennady Laventman
 */
public class ChaincodeRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends RepositoryFactoryBeanSupport<T, S, ID> {
    private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryFactoryBean.class);

    Class<? extends T> repositoryInterface;

    private RepositoryFactorySupport factory;

    private T repository;

    private ChaincodeClient chaincodeClient;

    private RepositoryFragments repositoryFragments;

    /**
     * Create new {@link ChaincodeRepositoryFactoryBean} for given repository interface
     *
     * @param repositoryInterface - must not be {@literal null}
     */
    protected ChaincodeRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
        logger.debug("Creating FactoryBean for class " + repositoryInterface.getName());
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        logger.debug("Getting factory for class " + repositoryInterface.getName());
        return new ChaincodeRepositoryFactory(repositoryInterface, chaincodeClient);
    }

    @Override
    public void afterPropertiesSet() {
        logger.debug("After properties set for factory bean " + repositoryInterface.getName());
        factory = createRepositoryFactory();
        repository = factory.getRepository(repositoryInterface, repositoryFragments);
    }

    @Override
    public T getObject() {
        return repository;
    }

    @Override
    public Class<? extends T> getObjectType() {
        return repositoryInterface;
    }

    public void setChaincodeClient(ChaincodeClient chaincodeClient) {
        this.chaincodeClient = chaincodeClient;
    }

    @Override
    public void setRepositoryFragments(RepositoryFragments repositoryFragments) {
        logger.debug("Set repository fragments for {} fragments {}", repositoryInterface.getName(), repositoryFragments);
        this.repositoryFragments = repositoryFragments;
        super.setRepositoryFragments(repositoryFragments);
    }


}
