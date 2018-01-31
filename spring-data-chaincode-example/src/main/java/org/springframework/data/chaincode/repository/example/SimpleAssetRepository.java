package org.springframework.data.chaincode.repository.example;

import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeQuery;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

@Chaincode(channel="mychannel", name="sass", version="1.0")
public interface SimpleAssetRepository extends ChaincodeRepository {
	@ChaincodeInvoke
	String set(String key, String value);
	
	@ChaincodeQuery
	String get(String key);

}
