package org.springframework.data.chaincode.repository.support;

import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;

public class SimpleChaincodeRepository implements ChaincodeRepository {
	
	private String channel;
	private String chaincode;
	private String version;
	private ChaincodeClient chaincodeClient;

	public SimpleChaincodeRepository(String channel, String chaincode, String version, ChaincodeClient ccClient) {
		this.channel = channel;
		this.chaincode = chaincode;
		this.version = version;
		this.chaincodeClient = ccClient;
		
	}

	@Override
	public boolean instantiate() {
		return false;
	}

}
