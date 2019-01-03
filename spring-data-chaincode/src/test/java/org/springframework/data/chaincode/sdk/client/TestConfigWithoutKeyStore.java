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

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.chaincode.events.FabricEventsListenersRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Import({FabricClientConfig.class})
public class TestConfigWithoutKeyStore {

    @Bean(name = "ordererLocations")
    public Map<String, String> ordererLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("orderer0", "grpc://localhost:7050");
        return res;
    }

    @Bean(name = "peerLocations")
    public Map<String, String> peerLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("peer0", "grpc://localhost:7051");
        return res;
    }

    @Bean(name = "eventHubLocations")
    public Map<String, String> eventHubLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("peer0", "grpc://localhost:7053");
        return res;
    }

    @Bean(name = "privateKeyLocation")
    public String privateKeyLocation() {
        return "basic-network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/keystore/0cd56151db5d102e209b295f16b562dd2fba7a41988341cd4a783a9f0520855f_sk";
    }

    @Bean(name = "userSigningCert")
    public String userSigningCert() {
        final String certificateFile = "basic-network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
        try (final InputStream in = new FileInputStream(getClass().getClassLoader().getResource(certificateFile).getFile())) {
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean(name = "mspId")
    public String mspId() {
        return "Org1MSP";
    }

    @Bean(name = "caCert")
    public String caCert() {
        final String certificateFile = "basic-network/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";
        try (final InputStream in = new FileInputStream(getClass().getClassLoader().getResource(certificateFile).getFile())) {
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public FabricEventsListenersRegistry fabricEventsListenersRegistry() {
        return new FabricEventsListenersRegistry();
    }


}
