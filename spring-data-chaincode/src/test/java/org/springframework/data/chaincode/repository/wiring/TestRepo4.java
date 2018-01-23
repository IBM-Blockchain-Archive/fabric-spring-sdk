package org.springframework.data.chaincode.repository.wiring;

import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

@Chaincode(channel = "mychannel", name="mycc4", version="1.0")
public interface TestRepo4 extends ChaincodeRepository<String, String>, CustomRepo {
	@ChaincodeInvoke
	String invokeMethod(String arg1);

}
