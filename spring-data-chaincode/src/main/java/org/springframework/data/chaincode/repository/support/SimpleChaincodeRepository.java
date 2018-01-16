package org.springframework.data.chaincode.repository.support;

import org.springframework.data.chaincode.repository.ChaincodeRepository;

public class SimpleChaincodeRepository<T, ID> implements ChaincodeRepository<T, ID> {
	
	private String channel;
	private String chaincode;
	private String version;

	public SimpleChaincodeRepository(String channel, String chaincode, String version) {
		this.channel = channel;
		this.chaincode = chaincode;
		this.version = version;
		
	}

	@Override
	public boolean instantiate() {
		return false;
	}

}
