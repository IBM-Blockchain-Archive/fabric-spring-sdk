package org.springframework.data.chaincode.repository.usage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.chaincode.config.AbstractChaincodeConfiguration;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;

@Configuration
@ComponentScan
@EnableChaincodeRepositories(basePackages = {"org.springframework.data.chaincode.repository.usage"})
public class TestConfig extends AbstractChaincodeConfiguration{
	@Bean (name = "privateKeyLocation") 
	public String privateKeyLocation() {
		return "network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp"
				+ "/keystore/c75bd6911aca808941c3557ee7c97e90f3952e379497dc55eb903f31b50abc83_sk";
	}
	
	@Bean (name = "userSigningCert")
	public String userSigningCert() {
		final String certificateFile = "network/crypto-config/peerOrganizations/org1.example.com/users"
				+ "/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
		try (final InputStream in = new FileInputStream(getClass().getClassLoader().getResource(certificateFile).getFile())) {
			return IOUtils.toString(in, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Bean (name = "mspId")
	public String mspId() {
		return "Org1MSP";
	}
	
	@Bean (name = "caCert")
	public String caCert() {
		final String certificateFile = "network/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";
		try (final InputStream in = new FileInputStream(getClass().getClassLoader().getResource(certificateFile).getFile())) {
			return IOUtils.toString(in, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}