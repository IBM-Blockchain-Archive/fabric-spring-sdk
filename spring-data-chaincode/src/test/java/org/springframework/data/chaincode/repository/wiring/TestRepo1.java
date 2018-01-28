package org.springframework.data.chaincode.repository.wiring;

import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeQuery;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

@Chaincode(channel = "mychannel", name="mycc", version="1.0")
public interface TestRepo1 extends ChaincodeRepository {
	@ChaincodeInvoke
	String invokeMethod(String arg1);
	
	@ChaincodeQuery
	String qMethod(String arg);
}
