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

package org.springframework.data.chaincode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.chaincode.events.FabricEventsConfig;
import org.springframework.data.chaincode.sdk.client.FabricClientConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Spring Data Chaincode configuration using JavaConfig.
 *
 * @author Gennady Laventman
 */
@Configuration
@Import({FabricClientConfig.class, FabricEventsConfig.class})
public abstract class AbstractChaincodeConfiguration {
    /**
     * @return location of orderers accessible to client
     */
    @Bean(name = "ordererLocations")
    public Map<String, String> ordererLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("orderer0", "grpc://localhost:7050");
        return res;
    }

    /**
     * @return location of peers accessible to client
     */
    @Bean(name = "peerLocations")
    public Map<String, String> peerLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("peer0", "grpc://localhost:7051");
        return res;
    }

    /**
     * @return of event hubs, usually runs as part of peers, accessible to client
     */
    @Bean(name = "eventHubLocations")
    public Map<String, String> eventHubLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("peer0", "grpc://localhost:7053");
        return res;
    }


}
