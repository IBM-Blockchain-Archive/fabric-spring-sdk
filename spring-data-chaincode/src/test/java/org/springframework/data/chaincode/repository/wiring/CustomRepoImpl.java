package org.springframework.data.chaincode.repository.wiring;

public class CustomRepoImpl implements CustomRepo {

	@Override
	public String customMethod() {
		return "customMethod";
	}

}
