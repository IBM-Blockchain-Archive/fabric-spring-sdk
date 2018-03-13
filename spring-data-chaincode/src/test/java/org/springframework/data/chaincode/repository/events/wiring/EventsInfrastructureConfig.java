package org.springframework.data.chaincode.repository.events.wiring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.chaincode.events.FabricEventsConfig;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
@Import({FabricEventsConfig.class})
public class EventsInfrastructureConfig {

    @Bean(name="chaincodeClient")
    public ChaincodeClient chaincodeClient() {

        ChaincodeClient client = new TestChaincodeClient();
        return client;
    }

    public static class TestChaincodeClient implements ChaincodeClient {

        public List<String> ccReg = new ArrayList<>();
        public List<String> chReg = new ArrayList<>();


        @Override
        public String invokeQuery(String chName, String ccName, String ccVer, String func, String[] args) {
            return "queried";
        }


        @Override
        public String invokeChaincode(String chName, String ccName, String ccVer, String func, String[] args) {
            return "invoked";
        }

        @Override
        public void startChaincodeEventsListener(String chName, String ccName) {
            if (!ccReg.contains(ccName))
                ccReg.add(ccName);

        }

        @Override
        public void startBlockEventsListener(String chName) {
            if (!chReg.contains(chName))
                chReg.add(chName);
        }
    }

}
