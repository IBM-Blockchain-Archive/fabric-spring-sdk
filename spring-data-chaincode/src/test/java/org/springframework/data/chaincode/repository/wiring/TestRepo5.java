package org.springframework.data.chaincode.repository.wiring;

import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

@Chaincode(name="mycc5", version="1.0")
public interface TestRepo5 extends ChaincodeRepository{
	@ChaincodeInvoke
	String invokeMethod(String arg1);
}
