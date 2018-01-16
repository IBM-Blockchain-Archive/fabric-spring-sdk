package org.springframework.data.chaincode.repository;

public interface TestRepo2 extends ChaincodeRepository<String, String> {
	@ChaincodeInvoke
	String invokeMethod(String arg1);
	
	@ChaincodeQuery
	String qMethod(String arg);
}
