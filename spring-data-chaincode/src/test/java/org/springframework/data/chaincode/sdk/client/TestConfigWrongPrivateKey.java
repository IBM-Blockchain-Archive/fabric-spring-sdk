package org.springframework.data.chaincode.sdk.client;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.chaincode.config.AbstractChaincodeConfiguration;
import org.springframework.data.chaincode.events.FabricEventsListenersRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Configuration
@Import({FabricClientConfig.class})
public class TestConfigWrongPrivateKey extends AbstractChaincodeConfiguration {
    @Bean(name = "privateKeyLocation")
    public String privateKeyLocation() {
        return "basic-network.1/crypto-config/peerOrganizations/org1.example.com/users" +
                "/User1@org1.example.com/msp/keystore/0cd56151db5d102e209b295f16b562dd2fba7a41988341cd4a783a9f0520855f_sk";
    }

    @Bean(name = "userSigningCert")
    public String userSigningCert() {
        final String certificateFile = "basic-network/crypto-config/peerOrganizations/org1.example.com/users"
                + "/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
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
