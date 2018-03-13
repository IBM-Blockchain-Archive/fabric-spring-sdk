/*
 *
 *  Copyright 2017 IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.data.chaincode.sdk.client;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfigWithKeyStore.class})
public class ChaincodeClientWithKeyStoreTest {
	
    @ClassRule
    public static DockerComposeContainer env = new DockerComposeContainer(
            new File("src/test/resources/network/docker-compose.yml")
    )
            .withLocalCompose(false)
            .withPull(false);

	@BeforeClass
	public static void setUp() throws Exception {
		TimeUnit.SECONDS.sleep(15);
	}

	@Autowired
	ChaincodeClient chaincodeClient;

	@Test
	public void testClient() throws InterruptedException {
		
		
		String prevAString = chaincodeClient.invokeQuery("mychannel", "mycc", "1.0", "query", new String[] {"a"});
		String prevBString = chaincodeClient.invokeQuery("mychannel", "mycc", "1.0", "query", new String[] {"b"});
		chaincodeClient.invokeChaincode("mychannel", "mycc", "1.0", "invoke", new String[] {"a", "b", "1"});

		String newAString = chaincodeClient.invokeQuery("mychannel", "mycc", "1.0", "query", new String[] {"a"});
		String newBString = chaincodeClient.invokeQuery("mychannel", "mycc", "1.0", "query", new String[] {"b"});

		int prevA = Integer.parseInt(prevAString);
		int prevB = Integer.parseInt(prevBString);
		
		int newA = Integer.parseInt(newAString);
		int newB = Integer.parseInt(newBString);
		
		System.out.println("<<<<<<<<<<<<<<<<<<<<< prevA " + prevAString + " prevB " + prevBString + " newA " + newAString + " newB " + newBString + " >>>>>>>>>>>>>>>>>>>>>");
		
		assertEquals("A value didn't changed", prevA - 1, newA);
		assertEquals("B value didn't changed", prevB + 1, newB);

		chaincodeClient.invokeChaincode("mychannel", "mycc", "1.0", "invoke", new String[] {"b", "a", "1"});
		
		newAString = chaincodeClient.invokeQuery("mychannel", "mycc", "1.0", "query", new String[] {"a"});
		newBString = chaincodeClient.invokeQuery("mychannel", "mycc", "1.0", "query", new String[] {"b"});
		
		newA = Integer.parseInt(newAString);
		newB = Integer.parseInt(newBString);

		assertEquals("A value changed", prevA, newA);
		assertEquals("B value changed", prevB, newB);

		System.out.println("<<<<<<<<<<<<<<<<<<<<< prevA " + prevAString + " prevB " + prevBString + " newA " + newAString + " newB " + newBString + " >>>>>>>>>>>>>>>>>>>>>");
	}

}
