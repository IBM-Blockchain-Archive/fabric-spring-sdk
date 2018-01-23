package org.springframework.data.chaincode.repository.wiring;

import org.springframework.data.chaincode.repository.Chaincode;

@Chaincode(channel = "mychannel", name="mycc3", version="1.0")
public interface TestRepo31 extends TestRepo3 {

}
