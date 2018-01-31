package org.springframework.data.chaincode.repository.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.chaincode.config.AbstractChaincodeConfiguration;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;

@Configuration
@ComponentScan
@EnableChaincodeRepositories(basePackages = { "org.springframework.data.chaincode.repository.example" })
public class SimpleAssetConfig extends AbstractChaincodeConfiguration {
	@Bean(name = "privateKeyLocation")
	public String privateKeyLocation() {
		return "network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp"
				+ "/keystore/c75bd6911aca808941c3557ee7c97e90f3952e379497dc55eb903f31b50abc83_sk";
	}

	@Bean(name = "mspId")
	public String mspId() {
		return "Org1MSP";
	}

	@Bean(name = "keyStoreLocation")
	public String keyStoreLocation() {
		return "network/crypto-config/certificates.jks";
	}
}
