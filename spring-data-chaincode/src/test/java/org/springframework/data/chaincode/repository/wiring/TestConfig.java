package org.springframework.data.chaincode.repository.wiring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;
import org.springframework.data.chaincode.repository.sdk.client.ChaincodeClient;

@Configuration
@ComponentScan
@EnableChaincodeRepositories(basePackages = {"org.springframework.data.chaincode.repository.wiring"})
public class TestConfig {

	@Bean
	public String testBean() {
		return "testtstst";
	}
	
	@Bean
	public ChaincodeClient chaincodeClient() {
		return new ChaincodeClient() {
			
			@Override
			public String invokeQuery(String chName, String ccName, String ccVer, String func, String[] args) {
				return null;
			}
			
			@Override
			public String invokeChaincode(String chName, String ccName, String ccVer, String func, String[] args) {
				return null;
			}

		};
	}

}
