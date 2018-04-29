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

package org.springframework.data.chaincode.repository.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * {@link ImportBeanDefinitionRegistrar} to setup Chaincode repositories via {@link EnableChaincodeRepositories}.
 *
 * @author Gennady Laventman
 */
public class ChaincodeRepositoryRegistrar extends RepositoryBeanDefinitionRegistrarSupport {
    private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryRegistrar.class);

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableChaincodeRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new ChaincodeRepositoryConfigurationExtension();
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        logger.debug("Starting beans registration");
        super.registerBeanDefinitions(annotationMetadata, registry);
        logger.debug("Done beans registration");
    }

}
