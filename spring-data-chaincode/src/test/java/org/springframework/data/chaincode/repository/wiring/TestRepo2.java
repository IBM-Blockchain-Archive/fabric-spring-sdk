package org.springframework.data.chaincode.repository.wiring;

import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeQuery;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

public interface TestRepo2 extends ChaincodeRepository {
	@ChaincodeInvoke
	String invokeMethod(String arg1);
	
	@ChaincodeQuery
	String qMethod(String arg);
}
