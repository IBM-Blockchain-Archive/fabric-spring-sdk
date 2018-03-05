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

package org.springframework.data.chaincode.repository.usage;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.repository.utils.DockerUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ChaincodeUsageTest {
	@ClassRule
	public static DockerComposeContainer env = new DockerComposeContainer(
			new File("src/test/resources/network/docker-compose.yml")).withLocalCompose(false).withPull(false);

	@Autowired
	Example02 example02;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DockerUtils.waitForContainers(new String[]{"peer0"});
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testExample02() throws Exception {

		Assert.assertNotNull(example02);

		String aString1 = example02.query("a");
		String bString1 = example02.query("b");

		int a1 = Integer.decode(aString1);
		int b1 = Integer.decode(bString1);

		example02.invoke("a", "b", "10");
		String aString2 = example02.query("a");
		String bString2 = example02.query("b");

		int a2 = Integer.decode(aString2);
		int b2 = Integer.decode(bString2);

		Assert.assertEquals("", a1, a2 + 10);
		Assert.assertEquals("", b2, b1 + 10);

	}


}
