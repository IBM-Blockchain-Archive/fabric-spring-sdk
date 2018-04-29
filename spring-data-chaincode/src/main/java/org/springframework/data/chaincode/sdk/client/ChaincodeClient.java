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

package org.springframework.data.chaincode.sdk.client;


import com.google.protobuf.ByteString;
import org.springframework.data.chaincode.repository.Chaincode;

/**
 * Interface that expose basic chaincode operations to package
 *
 * @author Gennady Laventman
 */
public interface ChaincodeClient {

    /**
     * Chaincode invocation in channel
     *
     * @param chName - channel name, specified in {@link Chaincode#channel()}
     * @param ccName - chaincode name, specified in in {@link Chaincode#name()}
     * @param ccVer  - chaincode version, specified in in {@link Chaincode#version()}
     * @param func   - function name, as defined in repo interface
     * @param args   - function arguments, as supplied in call to repo interface
     * @return - payload {@link ByteString} converted to {@link String}
     */
    String invokeChaincode(String chName, String ccName, String ccVer, String func, String args[]) throws InvokeException;

    /**
     * Querying chaincode in channel
     *
     * @param chName - channel name, specified in {@link Chaincode#channel()}
     * @param ccName - chaincode name, specified in in {@link Chaincode#name()}
     * @param ccVer  - chaincode version, specified in in {@link Chaincode#version()}
     * @param func   - function name, as defined in repo interface
     * @param args   - function arguments, as supplied in call to repo interface
     * @return - payload {@link ByteString} converted to {@link String}
     */
    String invokeQuery(String chName, String ccName, String ccVer, String func, String args[]) throws QueryException;

    /**
     * Chaincode events listener for all components with {@link org.springframework.data.chaincode.events.ChaincodeEventListener} methods and corresponding channel and chaincode
     *
     * @param chName - from {@link Chaincode#channel()}
     * @param ccName - from {@link Chaincode#name()} ()}
     */
    void startChaincodeEventsListener(String chName, String ccName);

    /**
     * Block events listener for all components with {@link org.springframework.data.chaincode.events.BlockEventListener} methods and corresponding channel
     *
     * @param chName - from {@link org.springframework.data.chaincode.events.BlockEventListener#channel()}
     */
    void startBlockEventsListener(String chName);


}
