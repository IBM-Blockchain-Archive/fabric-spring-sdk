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
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.repository.Channel;
import org.springframework.data.chaincode.repository.support.ChaincodeRepositoryFactoryBean;
import org.springframework.data.repository.config.*;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * {@link RepositoryConfigurationExtension} for Hyperlder Fabric Chaincode
 *
 * @author Gennady Laventman
 */
public class ChaincodeRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {
    private static final Logger logger = LoggerFactory.getLogger(ChaincodeRepositoryConfigurationExtension.class);

    @Override
    protected String getModulePrefix() {
        return "chaincode";
    }

    @Override
    public String getModuleName() {
        return "Chaincode";
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        logger.debug("getRepositoryFactoryBeanClassName return " + ChaincodeRepositoryFactoryBean.class.getName());
        return ChaincodeRepositoryFactoryBean.class.getName();
    }

    @Override
    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader, boolean strictMatchesOnly) {
        logger.debug("getRepositoryConfigurations for type " + configSource.getClass().getName());
        // Changing to work only with strict candidates
        return super.getRepositoryConfigurations(configSource, loader, true);
    }


    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        logger.debug("getIdentifyingAnnotations return {" + Chaincode.class.getName() + "}");
        return Collections.singleton(Chaincode.class);
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        logger.debug("getIdentifyingTypes return {" + ChaincodeRepository.class.getName() + "}");
        return Collections.singleton(ChaincodeRepository.class);
    }

    @Override
    protected boolean isStrictRepositoryCandidate(RepositoryMetadata metadata) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        logger.debug("Checking candidate {}", repositoryInterface.getName());
        Chaincode annotation = AnnotationUtils.findAnnotation(repositoryInterface, Chaincode.class);
        if (annotation == null) {
            logger.debug("Checking candidate {}, don't have Chaincode annotation", repositoryInterface.getName());
            return false;
        }

        if (annotation.channel().isEmpty()) {
            Channel channel = AnnotationUtils.findAnnotation(repositoryInterface, Channel.class);
            if (channel == null) {
                logger.debug("Checking candidate {}, no channel defined", repositoryInterface.getName());
                return false;
            }
        }

        return super.isStrictRepositoryCandidate(metadata);
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
        logger.debug("postProcess for AnnotationRepositoryConfigurationSource");
        String chaincodeClientRef = config.getAttributes().getString("chaincodeClientRef");

        if (StringUtils.hasText(chaincodeClientRef)) {
            builder.addPropertyReference("chaincodeClient", chaincodeClientRef);
        }

    }

}
