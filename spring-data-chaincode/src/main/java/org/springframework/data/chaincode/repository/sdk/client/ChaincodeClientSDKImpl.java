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

package org.springframework.data.chaincode.repository.sdk.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.hyperledger.fabric.protos.common.Common.Status;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoPrimitives;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ChaincodeClientSDKImpl implements ChaincodeClient {

    private static final Logger logger = LoggerFactory.getLogger(ChaincodeClientSDKImpl.class);

    private PrivateKey privateKey;

    private Map<String, Channel> channels = new HashMap<>();

    private Map<String, Set<String>> chaincodeListenerChannelsAndChaincodes = new HashMap<>();
    private Set<String> blockListenerChannels = new HashSet<>();

    private HFClient client;

    private CryptoSuite cryptoSuite;

//    @Autowired
//    FabricEventsListenersRegistry listenersRegistry;

    @Resource(name = "ordererLocations")
    private Map<String, String> ordererLocations;

    @Resource(name = "peerLocations")
    private Map<String, String> peerLocations;

    @Resource(name = "eventHubLocations")
    private Map<String, String> eventHubLocations;

    @Autowired(required = false)
    @Qualifier("userSigningCert")
    private String userSigningCert;

    @Autowired
    @Qualifier("mspId")
    private String mspId;

    @Autowired(required = false)
    @Qualifier("caCert")
    private String caCert;

    @Autowired
    @Qualifier("privateKeyLocation")
    private void SetPrivateKeyLocation(String privateKeyLocation) {
        logger.debug(String.format("Setting private key file %s", privateKeyLocation));
        PrivateKeyInfo pemPair;

        logger.debug("Looking for private key file location at " + privateKeyLocation + " in file system");
        File f = new File(privateKeyLocation);
        if (!f.exists()) {
            logger.debug("Can't find file " + privateKeyLocation + " in file system, looking as resource");
            f = new File(getClass().getClassLoader().getResource(privateKeyLocation).getFile());
            if (!f.exists()) {
                logger.warn("Can't find file " + privateKeyLocation + " not in file system, nor as resource, let's hope we will have private key somehow later");
                return;
            }
        }
        try (final InputStream in = new FileInputStream(f)) {
            try (PEMParser parser = new PEMParser(
                    new StringReader(IOUtils.toString(in, Charset.defaultCharset())))) {
                pemPair = (PrivateKeyInfo) parser.readObject();
                JcaPEMKeyConverter pemConverter = new JcaPEMKeyConverter();
                privateKey = pemConverter.setProvider(BouncyCastleProvider.PROVIDER_NAME)
                        .getPrivateKey(pemPair);
            }
        } catch (Exception e) {
            logger.warn("Exception while loading private key", e);
        }
        logger.debug(String.format("Done setting private key file %s", privateKeyLocation));
    }

    @Autowired(required = false)
    @Qualifier("keyStoreLocation")
    private void setKeyStoreLocation(String keyStoreLocation) throws Exception {
        logger.debug(String.format("Setting keystore %s", keyStoreLocation));

        logger.debug("Looking for keystore file location at " + keyStoreLocation + " in file system");
        File f = new File(keyStoreLocation);
        if (!f.exists()) {
            logger.debug("Can't find file " + keyStoreLocation + " in file system, looking as resource");
            f = new File(getClass().getClassLoader().getResource(keyStoreLocation).getFile());
            if (!f.exists()) {
                logger.warn("Can't find file " + keyStoreLocation + " not in file system, nor as resource, let's hope we will have keystore somehow later");
                return;
            }
        }

        try (final InputStream in = new FileInputStream(f)) {
            ((CryptoPrimitives) cryptoSuite).getTrustStore().load(in, null);
        }

        Enumeration<String> aliases = ((CryptoPrimitives) cryptoSuite).getTrustStore().aliases();
        for (; aliases.hasMoreElements(); ) {
            String element = aliases.nextElement();
            if ("userSigningCert".equalsIgnoreCase(element)) {
                Certificate cert = ((CryptoPrimitives) cryptoSuite).getTrustStore().getCertificate(element);
                StringWriter sw = new StringWriter();
                JcaPEMWriter pw = new JcaPEMWriter(sw);
                pw.writeObject(cert);
                pw.flush();
                pw.close();
                userSigningCert = sw.toString();
                break;
            }
        }
    }

    {
        // Do it only once
        Security.addProvider(new BouncyCastleProvider());
    }

    public ChaincodeClientSDKImpl() throws InitException {
        try {
            // Create client and set default crypto suite
            cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
            client = HFClient.createNewInstance();
            client.setCryptoSuite(cryptoSuite);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | CryptoException | InvalidArgumentException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("Exception during SDK clinet init", e);
            throw new InitException("Exception during SDK clinet init", e);
        }
    }

    private void initUserContext() throws InvalidArgumentException {
        if (client.getUserContext() == null) {
            logger.debug("User context not initiated, initiating");
            final User user = new User() {

                @Override
                public Set<String> getRoles() {
                    return null;
                }

                @Override
                public String getName() {
                    return "testUser";
                }

                @Override
                public String getMspId() {
                    return mspId;
                }

                @Override
                public Enrollment getEnrollment() {
                    return new Enrollment() {

                        @Override
                        public PrivateKey getKey() {
                            return privateKey;
                        }

                        @Override
                        public String getCert() {
                            return userSigningCert;
                        }
                    };
                }

                @Override
                public String getAffiliation() {
                    return null;
                }

                @Override
                public String getAccount() {
                    return null;
                }
            };

            client.setUserContext(user);
        }
    }

    private synchronized Channel getChannel(String name) throws InvalidArgumentException, TransactionException {
        if (!channels.containsKey(name)) {
            logger.debug("Channel {} not initiated, initiating", name);
            Channel channel = client.newChannel(name);
            for (Map.Entry<String, String> peer : peerLocations.entrySet()) {
                channel.addPeer(client.newPeer(peer.getKey(), peer.getValue()));
            }
            // For now, all peers are event hubs
            for (Map.Entry<String, String> eventHub : eventHubLocations.entrySet()) {
                channel.addEventHub(client.newEventHub(eventHub.getKey(), eventHub.getValue()));
            }
            for (Map.Entry<String, String> orderer : ordererLocations.entrySet()) {
                channel.addOrderer(client.newOrderer(orderer.getKey(), orderer.getValue()));
            }
            channel.initialize();
            channels.put(name, channel);
            chaincodeListenerChannelsAndChaincodes.put(name, new HashSet<>());
        }
        return channels.get(name);
    }


    @Override
    public String invokeChaincode(String chName, String ccName, String ccVer, String func, String[] args)
            throws InvokeException {

        try {
            initUserContext();
        } catch (InvalidArgumentException e) {
            logger.warn("Exception during context initiation", e);
            throw new InvokeException("Exception during context initiation", e);
        }

        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();

        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(ccName)
                .setVersion(ccVer)
                .build();

        proposalRequest.setChaincodeID(chaincodeID);
        proposalRequest.setFcn(func);
        proposalRequest.setProposalWaitTime(TimeUnit.SECONDS.toMillis(10));
        proposalRequest.setArgs(args);

        // Send proposal and wait for responses
        logger.debug("Sending proposal for {} {}", func, args == null ? null : Arrays.asList(args));
        Collection<ProposalResponse> responses;
        try {
            responses = getChannel(chName).sendTransactionProposal(proposalRequest);
        } catch (ProposalException | InvalidArgumentException | TransactionException e) {
            logger.warn("Exception during send proposal", e);
            throw new InvokeException("Exception during send proposal", e);
        }

        // Verifying responses
        for (ProposalResponse proposal : responses) {
        		if (!proposal.isVerified()) {
        			logger.warn("Invalid proposal {}", proposal);
        			throw new InvokeException(String.format("Invalid proposal %s", proposal));
        		}
        }
        // Sending transaction to orderers
        logger.debug("Sending transaction for {} {}", func, args == null ? null : Arrays.asList(args));
        CompletableFuture<BlockEvent.TransactionEvent> txFuture;
        try {
            txFuture = getChannel(chName).sendTransaction(responses);
        } catch (InvalidArgumentException | TransactionException e) {
            logger.warn("Exception during send transaction", e);
            throw new InvokeException("During send transaction", e);
        }

        BlockEvent.TransactionEvent event = null;
        try {
            event = txFuture.get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warn("Exception during wait for transaction event", e);
            throw new InvokeException("Exception during wait", e);
        }

        if (event == null) {
            logger.warn("Something wrong, event from hub is null");
            throw new InvokeException("Something wrong, event from hub is null");
        }

        for (ProposalResponse resp : responses) {
            if (resp.getProposalResponse().getResponse().getStatus() == Status.SUCCESS.getNumber()) {
                logger.debug("Invoke response status {} msg {} payload {}", resp.getProposalResponse().getResponse().getStatus(),
                        resp.getProposalResponse().getResponse().getMessage(),
                        resp.getProposalResponse().getResponse().getPayload().toStringUtf8());
                return resp.getProposalResponse().getResponse().getPayload().toStringUtf8();
            }
        }

        return null;
    }

    @Override
    public String invokeQuery(String chName, String ccName, String ccVer, String func, String[] args)
            throws QueryException {

        try {
            initUserContext();
        } catch (InvalidArgumentException e) {
            logger.warn("Exception during context initiation", e);
            throw new QueryException("Exception during context initiation", e);
        }

        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();

        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(ccName)
                .setVersion(ccVer)
                .build();

        proposalRequest.setChaincodeID(chaincodeID);
        proposalRequest.setFcn(func);
        proposalRequest.setProposalWaitTime(TimeUnit.SECONDS.toMillis(10));
        proposalRequest.setArgs(args);

        // Send proposal and wait for responses
        logger.debug("Sending proposal for {} {}", func, args == null ? null : Arrays.asList(args));
        Collection<ProposalResponse> responses;
        try {
            responses = getChannel(chName).sendTransactionProposal(proposalRequest);
        } catch (ProposalException | InvalidArgumentException | TransactionException e) {
            logger.warn("Exception during query send proposal", e);
            throw new QueryException("Exception during query send proposal", e);
        }

        for (ProposalResponse resp : responses) {
            if (resp.getProposalResponse() == null || resp.getProposalResponse().getResponse() == null) {
                logger.warn("Wrong proposal response", resp);
                throw new QueryException("Wrong proposal response");
            }
            if (resp.getProposalResponse().getResponse().getStatus() == Status.SUCCESS.getNumber()) {
                logger.debug("Query response status {} msg {} payload {}", resp.getProposalResponse().getResponse().getStatus(),
                        resp.getProposalResponse().getResponse().getMessage(),
                        resp.getProposalResponse().getResponse().getPayload().toStringUtf8());
                return resp.getProposalResponse().getResponse().getPayload().toStringUtf8();
            }
        }

        return null;
    }

//    @Override
//    public void startChaincodeEventsListener(String chName, String ccName) throws EventException {
//        if (chaincodeListenerChannelsAndChaincodes.containsKey(chName) &&
//                chaincodeListenerChannelsAndChaincodes.get(chName).contains(ccName)) {
//            logger.info("SDK listener for channel {} and chaincode {} already registrated", chName, ccName);
//
//        }
//        try {
//            initUserContext();
//        } catch (InvalidArgumentException e) {
//            logger.warn("Exception during context initiation", e);
//            throw new EventException("Exception during context initiation", e);
//        }
//        logger.debug("Registrating listener for channel {} and chaincode {}", chName, ccName);
//        try {
//            getChannel(chName).registerChaincodeEventListener(Pattern.compile(ccName), Pattern.compile(".*"), new org.hyperledger.fabric.sdk.ChaincodeEventListener() {
//
//                @Override
//                public void received(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
//                    try {
//                        listenersRegistry.invokeChaincodeEventListener(chName, ccName, chaincodeEvent);
//                    } catch (Exception e) {
//                        logger.warn("Exception during event passing to listener", e);
//                        throw new EventException("Exception during event passing to listener", e);
//                    }
//                }
//            });
//            chaincodeListenerChannelsAndChaincodes.get(chName).add(ccName);
//        } catch (InvalidArgumentException | TransactionException e) {
//            logger.warn("Exception during event registartion", e);
//            throw new EventException("Exception during event registartion", e);
//        }
//        return;
//    }
//
//    @Override
//    public void startBlockEventsListener(String chName) throws EventException {
//        if (blockListenerChannels.contains(chName)) {
//            logger.info("SDK listener for channel {} already registrated", chName);
//            return;
//        }
//        try {
//            initUserContext();
//        } catch (InvalidArgumentException e) {
//            logger.warn("Exception during context initiation", e);
//            throw new EventException("Exception during context initiation", e);
//        }
//
//        try {
//            getChannel(chName).registerBlockListener(new BlockListener() {
//
//                @Override
//                public void received(BlockEvent blockEvent) {
//                    try {
//                        listenersRegistry.invokeBlockEventListeners(chName, blockEvent);
//                    } catch (Exception e) {
//                        logger.warn("Exception during event passing to listener", e);
//                        throw new EventException("Exception during event passing to listener", e);
//                    }
//                }
//            });
//            blockListenerChannels.add(chName);
//        } catch (InvalidArgumentException | TransactionException e) {
//            logger.warn("Exception during event registartion", e);
//            throw new EventException("Exception during event registartion", e);
//        }
//
//        return;
//    }
//
}
