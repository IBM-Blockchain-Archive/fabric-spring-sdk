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

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FabricEventsPostInitContextListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(FabricEventsPostInitContextListener.class);

    @Autowired
    private FabricEventsListenersRegistry registry;

    @Autowired
    private ChaincodeClient chaincodeClient;

    @Override
    synchronized public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.debug("Handling events listener registry on event {}, block listeners {}, chaincode listeners {}", event, registry.blockEventListeners.size(), registry.chaincodeEventListeners.size());
        Set<String> channelsToRegister = new HashSet<>();
        Map<String, Set<String>> chaincodesToRegister = new HashMap<>();

        ApplicationContext context = event.getApplicationContext();
        for (FabricEventsListenersRegistry.BlockEventListenerEntry entry : registry.blockEventListeners) {
            logger.debug("Looking for bean {} method {} to listen to block events in channel {}", entry.beanName, entry.methodName, entry.chName);
            if (entry.registrated) {
                logger.debug("Bean {} method {} to listen to block events in channel {} already registrated", entry.beanName, entry.methodName, entry.chName);
                continue;
            }
            Object bean = context.getBean(entry.beanName);
            if (bean != null) {
                Method method;
                try {
                    method = bean.getClass().getMethod(entry.methodName, BlockEvent.class);
                } catch (NoSuchMethodException | SecurityException e) {
                    logger.warn("Can't find method {} in bean {} class {}", entry.methodName, entry.beanName, bean.getClass().getName());
                    continue;
                }
                entry.bean = bean;
                entry.method = method;
            } else {
                logger.warn("Can't find bean {}", entry.beanName);
                continue;
            }
            entry.registrated = true;
            channelsToRegister.add(entry.chName);
        }

        for (FabricEventsListenersRegistry.ChaincodeEventListenerEntry entry : registry.chaincodeEventListeners) {
            logger.debug("Looking for bean {} method {} to listen to chaincode events in channel {} chaincode {}", entry.beanName, entry.methodName, entry.chName, entry.ccName);
            if (entry.registrated) {
                logger.debug("Bean {} method {} to listen to chaincode events in channel {} chaincode {} already registrated", entry.beanName, entry.methodName, entry.chName, entry.ccName);
                continue;
            }
            Object bean = context.getBean(entry.beanName);
            if (bean != null) {
                Method method;
                try {
                    method = bean.getClass().getMethod(entry.methodName, ChaincodeEvent.class);
                } catch (NoSuchMethodException | SecurityException e) {
                    logger.warn("Can't find method {} in bean {} class {}", entry.methodName, entry.beanName, bean.getClass().getName());
                    continue;
                }
                entry.bean = bean;
                entry.method = method;
            } else {
                logger.warn("Can't find bean {}", entry.beanName);
                continue;
            }
            if (!chaincodesToRegister.containsKey(entry.chName)) {
                chaincodesToRegister.put(entry.chName, new HashSet<>());
            }
            entry.registrated = true;
            chaincodesToRegister.get(entry.chName).add(entry.ccName);
        }

        channelsToRegister.forEach(channel -> {
            logger.debug("Staring block event listener for channel {}", channel);
            chaincodeClient.startBlockEventsListener(channel);
        });

        chaincodesToRegister.forEach((channel, chaincodes) -> {
            chaincodes.forEach(cc -> {
                logger.debug("Staring chaincode event listener for channel {} chaincode {}", channel, cc);
                chaincodeClient.startChaincodeEventsListener(channel, cc);
            });
        });

    }

}
