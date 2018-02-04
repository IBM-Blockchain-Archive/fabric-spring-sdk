package org.springframework.data.chaincode.repository.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;

@Configuration
public class TestConfig {
    @Bean
    public ChaincodeClient chaincodeClient() {
        return new ChaincodeClient() {

            @Override
            public String invokeQuery(String chName, String ccName, String ccVer, String func, String[] args) {
                return "queried";
            }

            @Override
            public String invokeChaincode(String chName, String ccName, String ccVer, String func, String[] args) {
                return "invoked";
            }

        };
    }

}
