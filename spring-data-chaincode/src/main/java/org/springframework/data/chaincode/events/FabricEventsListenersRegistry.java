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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Registry that connects between beans with method annotated by {@link BlockEventListener} or {@link ChaincodeEventListener}
 * and chaincode event listeners from SDK
 */
public class FabricEventsListenersRegistry {
    private static final Logger logger = LoggerFactory.getLogger(FabricEventsListenersRegistry.class);


    List<ChaincodeEventListenerEntry> chaincodeEventListeners = new ArrayList<>();
    List<BlockEventListenerEntry> blockEventListeners = new ArrayList<>();

    public FabricEventsListenersRegistry() {
        logger.debug("Creating FabricEventsListenersRegistry instance");
    }

    void registerChaincodeEventListener(String chName, String ccName, String beanName, String methodName) {
        ChaincodeEventListenerEntry entry = new ChaincodeEventListenerEntry(chName, ccName, beanName, methodName);
        if (!isChaincodeEventListenerExist(entry)) {
            chaincodeEventListeners.add(entry);
        } else {
            logger.warn("Listener for channel {} chaincode {} bean {} method {} already exist", chName, ccName, beanName, methodName);
        }
    }

    void registerBlockEventListener(String chName, String beanName, String methodName) {
        BlockEventListenerEntry entry = new BlockEventListenerEntry(chName, beanName, methodName);
        if (!isBlockEventListenerExist(entry)) {
            blockEventListeners.add(entry);
        } else {
            logger.warn("Listener for channel {} bean {} method {} already exist", chName, beanName, methodName);
        }
    }

    public void invokeChaincodeEventListener(String chName, String ccName, ChaincodeEvent event) throws Exception {
        logger.debug("Invoking chaincode event listeners for channel {}, chaincode {} listeners number {}", chName, ccName, blockEventListeners.size());
        for (ChaincodeEventListenerEntry listenerEntry : chaincodeEventListeners) {
            if (listenerEntry.chName.equals(chName) &&
                    listenerEntry.ccName.equals(ccName)) {
                logger.debug("Listener entry for channel {} chaincode {} invoked using bean {} method {}", chName, ccName, listenerEntry.beanName, listenerEntry.methodName);
                listenerEntry.method.invoke(listenerEntry.bean, new Object[]{event});
            }
        }
    }

    public void invokeBlockEventListeners(String chName, BlockEvent event) throws Exception {
        logger.debug("Invoking block event listeners for channel {}, listeners number {}", chName, blockEventListeners.size());
        for (BlockEventListenerEntry listenerEntry : blockEventListeners) {
            if (listenerEntry.chName.equals(chName)) {
                logger.debug("Listener entry for channel {} invoked using bean {} method {}", chName, listenerEntry.beanName, listenerEntry.methodName);
                listenerEntry.method.invoke(listenerEntry.bean, new Object[]{event});
            }
        }
    }

    private boolean isChaincodeEventListenerExist(ChaincodeEventListenerEntry entry) {
        for (ChaincodeEventListenerEntry listenerEntry : chaincodeEventListeners) {
            if (listenerEntry.chName.equals(entry.chName) &&
                    listenerEntry.ccName.equals(entry.ccName) &&
                    listenerEntry.beanName.equals(entry.beanName) &&
                    listenerEntry.methodName.equals(entry.methodName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlockEventListenerExist(BlockEventListenerEntry entry) {
        for (BlockEventListenerEntry listenerEntry : blockEventListeners) {
            if (listenerEntry.chName.equals(entry.chName) &&
                    listenerEntry.beanName.equals(entry.beanName) &&
                    listenerEntry.methodName.equals(entry.methodName)) {
                return true;
            }
        }
        return false;
    }

    public static class ChaincodeEventListenerEntry {
        String chName;
        String ccName;
        String beanName;
        String methodName;

        Method method;
        Object bean;

        boolean registrated;

        public ChaincodeEventListenerEntry(String chName, String ccName, String beanName, String methodName) {
            this.chName = chName;
            this.ccName = ccName;
            this.beanName = beanName;
            this.methodName = methodName;
            this.registrated = false;
        }
    }

    public static class BlockEventListenerEntry {
        String chName;
        String beanName;
        String methodName;

        Method method;
        Object bean;

        boolean registrated;

        public BlockEventListenerEntry(String chName, String beanName, String methodName) {
            this.chName = chName;
            this.beanName = beanName;
            this.methodName = methodName;
            this.registrated = false;

        }
    }


    /* For unit-test */
    public List<ChaincodeEventListenerEntry> getChaincodeEventListeners() {
        return chaincodeEventListeners;
    }

    /* For unit-test */
    public List<BlockEventListenerEntry> getBlockEventListeners() {
        return blockEventListeners;
    }


}
