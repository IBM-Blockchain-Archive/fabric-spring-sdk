package org.springframework.data.chaincode.repository;

@Chaincode(name="mycc", version="1.0")
public interface TestRepo1 extends ChaincodeRepository<String, String> {
	@ChaincodeInvoke
	String invokeMethod(String arg1);
	
	@ChaincodeQuery
	String qMethod(String arg);
}
