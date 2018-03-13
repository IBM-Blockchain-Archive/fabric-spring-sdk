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
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.hyperledger.fabric.protos.common.Common.Status;
import org.hyperledger.fabric.sdk.*;
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
import org.springframework.data.chaincode.events.FabricEventsListenersRegistry;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChaincodeClientSDKImpl implements ChaincodeClient {

    private static final Logger logger = LoggerFactory.getLogger(ChaincodeClientSDKImpl.class);

    private PrivateKey privateKey;

    private Map<String, Channel> channels = new HashMap<>();

    private HFClient client;

    private CryptoSuite cryptoSuite;

    private Map<String, Set<String>> chaincodeListenerChannelsAndChaincodes = new HashMap<>();
    private Set<String> blockListenerChannels = new HashSet<>();

    @Autowired
    private FabricEventsListenersRegistry listenersRegistry;

    @Autowired(required = false)
    @Qualifier("ordererLocations")
    private Map<String, String> ordererLocations;

    @Autowired(required = false)
    @Qualifier("peerLocations")
    private Map<String, String> peerLocations;

    @Autowired(required = false)
    @Qualifier("eventHubLocations")
    private Map<String, String> eventHubLocations;

    @Autowired(required = false)
    @Qualifier("ordererProperties")
    private Map<String, Properties> ordererProperties;

    @Autowired(required = false)
    @Qualifier("peerProperties")
    private Map<String, Properties> peerProperties;

    @Autowired(required = false)
    @Qualifier("eventHubProperties")
    private Map<String, Properties> eventHubProperties;

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
    private void setPrivateKeyLocation(String privateKeyLocation) {
        logger.debug("Setting private key file {}", privateKeyLocation);
        PrivateKeyInfo pemPair;

        logger.debug("Looking for private key file location at {} in file system", privateKeyLocation);
        File f = new File(privateKeyLocation);
        if (!f.exists()) {
            logger.debug("Can't find file {} in file system, looking as resource", privateKeyLocation);
            URL resource = getClass().getClassLoader().getResource(privateKeyLocation);
            if (resource == null || !(f = new File(resource.getFile())).exists()) {
                logger.warn("Can't find file {} not in file system, nor as resource, let's hope we will have private key somehow later", privateKeyLocation);
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
        logger.debug("Done setting private key file {}", privateKeyLocation);
    }

    @Autowired(required = false)
    @Qualifier("keyStoreLocation")
    private void setKeyStoreLocation(String keyStoreLocation) throws Exception {
        logger.debug("Setting keystore {}", keyStoreLocation);

        logger.debug("Looking for keystore file location at {} in file system", keyStoreLocation);
        File f = new File(keyStoreLocation);
        if (!f.exists()) {
            logger.debug("Can't find file {} in file system, looking as resource", keyStoreLocation);
            URL resource = getClass().getClassLoader().getResource(keyStoreLocation);
            if (resource == null || !(f = new File(resource.getFile())).exists()) {
                logger.warn("Can't find file not in file system, nor as resource, let's hope we will have keystore somehow later", keyStoreLocation);
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
        logger.debug("Creating new instance of ChaincodeClientSDKImpl");
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
                if (peerProperties != null && peerProperties.containsKey(peer.getKey())) {
                    logger.debug("Adding peer {} with address {} and properties {}", peer.getKey(), peer.getValue(), peerProperties.get(peer.getKey()));
                    channel.addPeer(client.newPeer(peer.getKey(), peer.getValue(), peerProperties.get(peer.getKey())));
                } else {
                    logger.debug("Adding peer {} with address {}", peer.getKey(), peer.getValue());
                    channel.addPeer(client.newPeer(peer.getKey(), peer.getValue()));
                }
            }

            for (Map.Entry<String, String> eventHub : eventHubLocations.entrySet()) {
                if (eventHubProperties != null && eventHubProperties.containsKey(eventHub.getKey())) {
                    logger.debug("Adding eventHub {} with address {} and properties {}", eventHub.getKey(), eventHub.getValue(), eventHubProperties.get(eventHub.getKey()));
                    channel.addEventHub(client.newEventHub(eventHub.getKey(), eventHub.getValue(), eventHubProperties.get(eventHub.getKey())));
                } else {
                    logger.debug("Adding eventHub {} with address {} ", eventHub.getKey(), eventHub.getValue());
                    channel.addEventHub(client.newEventHub(eventHub.getKey(), eventHub.getValue()));
                }
            }
            for (Map.Entry<String, String> orderer : ordererLocations.entrySet()) {
                if (ordererProperties != null && ordererProperties.containsKey(orderer.getKey())) {
                    logger.debug("Adding orderer {} with address {} and properties {}", orderer.getKey(), orderer.getValue(), ordererProperties.get(orderer.getKey()));
                    channel.addOrderer(client.newOrderer(orderer.getKey(), orderer.getValue(), ordererProperties.get(orderer.getKey())));
                } else {
                    logger.debug("Adding orderer {} with address {}", orderer.getKey(), orderer.getValue());
                    channel.addOrderer(client.newOrderer(orderer.getKey(), orderer.getValue()));
                }
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

        logger.debug("Get responses {}", responses);
        QueryException exc = null;
        String res = null;
        for (ProposalResponse resp : responses) {
            if (resp.getProposalResponse() == null || resp.getProposalResponse().getResponse() == null) {
                logger.warn("Wrong proposal response {} from peer {}", resp, resp.getPeer().getName());
                exc = new QueryException("Wrong proposal response");
                continue;
            }
            if (resp.getProposalResponse().getResponse().getStatus() == Status.SUCCESS.getNumber()) {
                logger.debug("Query response status {} msg {} payload {}", resp.getProposalResponse().getResponse().getStatus(),
                        resp.getProposalResponse().getResponse().getMessage(),
                        resp.getProposalResponse().getResponse().getPayload().toStringUtf8());
                res = resp.getProposalResponse().getResponse().getPayload().toStringUtf8();
            }
        }

        if (exc != null) {
            throw exc;
        }

        return res;
    }

    @Override
    public void startChaincodeEventsListener(final String chName, final String ccName) throws EventException {
        if (chaincodeListenerChannelsAndChaincodes.containsKey(chName) &&
                chaincodeListenerChannelsAndChaincodes.get(chName).contains(ccName)) {
            logger.info("SDK listener for channel {} and chaincode {} already registrated", chName, ccName);
        }

        try {
            initUserContext();
        } catch (InvalidArgumentException e) {
            logger.warn("Exception during context initiation", e);
            throw new EventException("Exception during context initiation", e);
        }
        final Map<String, Long> handledChaincodeEvents = new HashMap<>();

        logger.debug("Registrating listener for channel {} and chaincode {}", chName, ccName);
        try {
            final Channel channel = getChannel(chName);
            channel.registerChaincodeEventListener(Pattern.compile(ccName), Pattern.compile(".*"), new org.hyperledger.fabric.sdk.ChaincodeEventListener() {

                @Override
                synchronized public void received(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
                    try {
                        if (!handledChaincodeEvents.containsKey(chaincodeEvent.getTxId())) {
                            handledChaincodeEvents.put(chaincodeEvent.getTxId(), (long) 0);
                        }
                        handledChaincodeEvents.put(chaincodeEvent.getTxId(), handledChaincodeEvents.get(chaincodeEvent.getTxId()) + 1);
                        String es = blockEvent.getPeer() != null ? blockEvent.getPeer().getName() : blockEvent.getEventHub().getName();
                        logger.debug("Handling event for Tx {} chaincode {} channel {} from peer {}, event hubs in channel {}, peers in channel {} - {} time", chaincodeEvent.getTxId(), chaincodeEvent.getChaincodeId(), blockEvent.getChannelId(), es, channel.getEventHubs(), channel.getPeers(), handledChaincodeEvents.get(chaincodeEvent.getTxId()));
                        listenersRegistry.invokeChaincodeEventListener(chName, ccName, chaincodeEvent);
                    } catch (Exception e) {
                        logger.warn("Exception during event passing to listener", e);
                        throw new EventException("Exception during event passing to listener", e);
                    }
                }
            });
            chaincodeListenerChannelsAndChaincodes.get(chName).add(ccName);
        } catch (InvalidArgumentException | TransactionException e) {
            logger.warn("Exception during event registration", e);
            throw new EventException("Exception during event registration", e);
        }
        return;
    }

    @Override
    public void startBlockEventsListener(final String chName) throws EventException {
        if (blockListenerChannels.contains(chName)) {
            logger.info("SDK listener for channel {} already registrated", chName);
            return;
        }
        try {
            initUserContext();
        } catch (InvalidArgumentException e) {
            logger.warn("Exception during context initiation", e);
            throw new EventException("Exception during context initiation", e);
        }

        final Map<Long, Long> handledBlockEvents = new HashMap<>();

        try {
            getChannel(chName).registerBlockListener(new BlockListener() {

                @Override
                synchronized public void received(BlockEvent blockEvent) {
                    try {
                        if (!handledBlockEvents.containsKey(blockEvent.getBlockNumber())) {
                            handledBlockEvents.put(blockEvent.getBlockNumber(), (long) 0);
                        }
                        handledBlockEvents.put(blockEvent.getBlockNumber(), handledBlockEvents.get(blockEvent.getBlockNumber()) + 1);
                        logger.debug("Handling event for block {} channel {} {} time", blockEvent.getBlockNumber(), blockEvent.getChannelId(), handledBlockEvents.get(blockEvent.getBlockNumber()));

                        listenersRegistry.invokeBlockEventListeners(chName, blockEvent);
                    } catch (Exception e) {
                        logger.warn("Exception during event passing to listener", e);
                        throw new EventException("Exception during event passing to listener", e);
                    }
                }
            });
            blockListenerChannels.add(chName);
        } catch (InvalidArgumentException | TransactionException e) {
            logger.warn("Exception during event registartion", e);
            throw new EventException("Exception during event registartion", e);
        }

        return;
    }

    PrivateKey getPrivateKey() {
        return privateKey;
    }

    String getUserSigningCert() {
        return userSigningCert;
    }

}
