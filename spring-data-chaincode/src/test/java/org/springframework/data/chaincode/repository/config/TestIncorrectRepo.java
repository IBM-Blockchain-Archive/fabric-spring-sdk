package org.springframework.data.chaincode.repository.config;

import org.springframework.data.chaincode.repository.ChaincodeInvoke;
import org.springframework.data.chaincode.repository.ChaincodeRepository;

public interface TestIncorrectRepo extends ChaincodeRepository {
    @ChaincodeInvoke
    String invoke(String arg);
}
