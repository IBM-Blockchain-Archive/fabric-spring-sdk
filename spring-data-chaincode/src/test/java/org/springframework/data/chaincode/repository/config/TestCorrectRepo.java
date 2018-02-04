package org.springframework.data.chaincode.repository.config;

import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

@Chaincode(channel = "c", name = "cc", version = "1")
public interface TestCorrectRepo extends ChaincodeRepository {
    @ChaincodeInvoke
    String invoke(String arg);
}
