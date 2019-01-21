package org.springframework.data.chaincode.sdk.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.chaincode.config.AbstractChaincodeConfiguration;
import org.springframework.data.chaincode.events.FabricEventsListenersRegistry;

@Configuration
@Import({FabricClientConfig.class})
public class TestConfigWrongKeyStrore extends AbstractChaincodeConfiguration {
    @Bean(name = "privateKeyLocation")
    public String privateKeyLocation() {
        return "basic-network/crypto-config/peerOrganizations/org1.example.com/users"
            + "/User1@org1.example.com/msp/keystore/0cd56151db5d102e209b295f16b562dd2fba7a41988341cd4a783a9f0520855f_sk";
    }

    @Bean(name = "mspId")
    public String mspId() {
        return "Org1MSP";
    }

    @Bean(name = "keyStoreLocation")
    public String keyStoreLocation() {
        return "basic-network/crypto-config/certificates.jks1";
    }

    @Bean
    public FabricEventsListenersRegistry fabricEventsListenersRegistry() {
        return new FabricEventsListenersRegistry();
    }


}
