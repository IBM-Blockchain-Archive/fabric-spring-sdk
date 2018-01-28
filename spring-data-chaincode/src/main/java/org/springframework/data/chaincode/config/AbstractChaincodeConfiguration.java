package org.springframework.data.chaincode.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.chaincode.sdk.client.FabricClientConfig;

@Configuration
@Import({ FabricClientConfig.class })
public abstract class AbstractChaincodeConfiguration {
	@Bean (name = "ordererLocations")
	public Map<String, String> ordererLocations() {
		final Map<String, String> res = new HashMap<>();
		res.put("orderer0", "grpc://localhost:7050");
		return res;
	}
	
	@Bean (name = "peerLocations")
	public Map<String, String> peerLocations() {
		final Map<String, String> res = new HashMap<>();
		res.put("peer0", "grpc://localhost:7051");
		return res;		
	}

}
