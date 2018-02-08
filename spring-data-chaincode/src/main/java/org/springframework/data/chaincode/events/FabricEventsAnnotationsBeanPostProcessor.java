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

package org.springframework.data.chaincode.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.Channel;

import java.lang.reflect.Method;

/**
 * {@link BeanPostProcessor} looks for all beans with methods annotated by {@link BlockEventListener} or {@link ChaincodeEventListener}
 * and store those beans info in {@link FabricEventsListenersRegistry}
 */
public class FabricEventsAnnotationsBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FabricEventsAnnotationsBeanPostProcessor.class);

    @Autowired
    private FabricEventsListenersRegistry registry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Method method : bean.getClass().getMethods()) {
            ChaincodeEventListener cel = AnnotationUtils.findAnnotation(method, ChaincodeEventListener.class);
            BlockEventListener bel = AnnotationUtils.findAnnotation(method, BlockEventListener.class);
            if (cel != null) {
                logger.debug("Found annotation ChaincodeEventListener in bean {} method {}", beanName, method.getName());
                Class<?> ccClass = cel.chaincode();
                Chaincode ccAnnotation = AnnotationUtils.findAnnotation(ccClass, Chaincode.class);
                if (ccAnnotation != null) {
                    String ccName = ccAnnotation.name();
                    String methodName = method.getName();
                    String chName = ccAnnotation.channel();
                    Channel chAnnotation = AnnotationUtils.findAnnotation(ccClass, Channel.class);
                    if (chAnnotation != null) {
                        chName = chAnnotation.name();
                    }
                    logger.debug("Connect chaincode events for channel {} and chaincode {} to bean {} and method {}", chName, ccName, beanName, methodName);
                    registry.registerChaincodeEventListener(chName, ccName, beanName, methodName);
                } else {
                    logger.warn("Class {} don't have correct annotations", ccClass.getName());
                }
            } else if (bel != null) {
                logger.debug("Found annotation BlockEventListener in bean {} method {}", beanName, method.getName());
                String chName = bel.channel();
                String methodName = method.getName();
                logger.debug("Connect chaincode events for channel {} to bean {} and method {}", chName, beanName, methodName);
                registry.registerBlockEventListener(chName, beanName, methodName);
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
