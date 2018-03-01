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

package org.springframework.data.chaincode.repository.events;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.chaincode.repository.utils.DockerUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ChaincodeEventTest {

	@ClassRule
	public static DockerComposeContainer env = new DockerComposeContainer(
			new File("src/test/resources/network/docker-compose.yml")).withLocalCompose(false).withPull(false);

	@Autowired
    EventsRepo eventsRepo;

	@Autowired
    ChaincodeEventsListenerComponent listener;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DockerUtils.waitForContainers();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testEvents() throws Exception {
		listener.blockEvents = 0;
		listener.ccEvents = 0;

		int prevEvents = Integer.decode(eventsRepo.query().split(":")[1].split("}")[0].split("\"")[1]);

		eventsRepo.invoke();

		eventsRepo.invoke();

		int newEvents = Integer.decode(eventsRepo.query().split(":")[1].split("}")[0].split("\"")[1]);

		Assert.assertEquals("Events amount not increased correctly", prevEvents + 2, newEvents);

		Assert.assertEquals("Block events number incorrect", 2, listener.blockEvents);
		Assert.assertEquals("Chaincode events number incorrect", 2, listener.ccEvents);

	}


}
