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

package org.springframework.data.chaincode.repository.multipeers;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.chaincode.config.AbstractChaincodeConfiguration;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@ComponentScan
@EnableChaincodeRepositories(basePackages = {"org.springframework.data.chaincode.repository.multipeers"})
public class TestConfig extends AbstractChaincodeConfiguration {

    @Override
    @Bean(name = "peerLocations")
    public Map<String, String> peerLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("peer0", "grpcs://localhost:7051");
        res.put("peer1", "grpcs://localhost:8051");
        return res;
    }

    @Override
    @Bean(name = "eventHubLocations")
    public Map<String, String> eventHubLocations() {
        final Map<String, String> res = new HashMap<>();
        return res;
    }

    @Override
    @Bean(name = "ordererLocations")
    public Map<String, String> ordererLocations() {
        final Map<String, String> res = new HashMap<>();
        res.put("orderer0", "grpcs://localhost:7050");
        return res;
    }

    @Bean(name = "ordererProperties")
    public Map<String, Properties> ordererProperties() throws IOException {
        final Map<String, Properties> propertiesMap = new HashMap<>();
        Properties orderer0Properties = new Properties();
        String ordererPemFileLocation = "first-network/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt";
        File ordererPemFile = new File(getClass().getClassLoader().getResource(ordererPemFileLocation).getFile());

        orderer0Properties.setProperty("pemFile", ordererPemFile.getCanonicalPath());
        orderer0Properties.setProperty("hostnameOverride", "orderer.example.com");
        orderer0Properties.setProperty("sslProvider", "openSSL");
        orderer0Properties.setProperty("negotiationType", "TLS");

        propertiesMap.put("orderer0", orderer0Properties);
        return propertiesMap;

    }

    @Bean(name = "peerProperties")
    public Map<String, Properties> peerProperties() throws IOException {
        Properties peer0Properties = new Properties();
//        String peer0PemFileLocation = "first-network/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt";
//        File peer0PemFile = new File(getClass().getClassLoader().getResource(peer0PemFileLocation).getFile());
        File peer0PemFile = new File("/Users/gennady/Development/Blockchain/Hyperledger/gopath/src/github.com/hyperledger/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
        peer0Properties.setProperty("pemFile", peer0PemFile.getCanonicalPath());
        peer0Properties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peer0Properties.setProperty("sslProvider", "openSSL");
        peer0Properties.setProperty("negotiationType", "TLS");


        Properties peer1Properties = new Properties();
//        String peer1PemFileLocation = "first-network/crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt";
//        File peer1PemFile = new File(getClass().getClassLoader().getResource(peer1PemFileLocation).getFile());
        File peer1PemFile = new File("/Users/gennady/Development/Blockchain/Hyperledger/gopath/src/github.com/hyperledger/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt");
        peer1Properties.setProperty("pemFile", peer1PemFile.getCanonicalPath());
        peer1Properties.setProperty("hostnameOverride", "peer1.org1.example.com");
        peer1Properties.setProperty("sslProvider", "openSSL");
        peer1Properties.setProperty("negotiationType", "TLS");

        final Map<String, Properties> propertiesMap = new HashMap<>();
        propertiesMap.put("peer0", peer0Properties);
        propertiesMap.put("peer1", peer1Properties);
        return propertiesMap;
    }

    @Bean(name = "privateKeyLocation")
    public String privateKeyLocation() {
        return "/Users/gennady/Development/Blockchain/Hyperledger/gopath/src/github.com/hyperledger/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/keystore/1f737a2d658c94546d600a9e905079b955002a5acc306657c508c9ce05958616_sk";
//        return "first-network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp"
//                + "/keystore/0132e37e19156739cfe20e1a0cb952b9e0e7d24e091520b895b9d26e27ab729d_sk";
    }

    @Bean(name = "userSigningCert")
    public String userSigningCert() {
//        final String certificateFile = "first-network/crypto-config/peerOrganizations/org1.example.com/users"
//                + "/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
        final String certificateFile = "/Users/gennady/Development/Blockchain/Hyperledger/gopath/src/github.com/hyperledger/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
//        try (final InputStream in = new FileInputStream(getClass().getClassLoader().getResource(certificateFile).getFile())) {
        try (final InputStream in = new FileInputStream(new File(certificateFile))) {
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
//        final String certificateFile = "first-network/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";
        final String certificateFile = "/Users/gennady/Development/Blockchain/Hyperledger/gopath/src/github.com/hyperledger/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";
//        try (final InputStream in = new FileInputStream(getClass().getClassLoader().getResource(certificateFile).getFile())) {
        try (final InputStream in = new FileInputStream(new File(certificateFile))) {
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
