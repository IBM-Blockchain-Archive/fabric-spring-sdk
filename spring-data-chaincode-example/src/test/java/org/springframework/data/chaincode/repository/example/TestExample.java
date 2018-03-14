package org.springframework.data.chaincode.repository.example;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.data.chaincode.repository.utils.DockerUtils;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

public class TestExample {
    @ClassRule
    public static DockerComposeContainer env = new DockerComposeContainer(
            new File("src/main/resources/network/docker-compose.yml")).withLocalCompose(false).withPull(false);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DockerUtils.waitForContainers(new String[]{"peer0"});
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testMain() {
        MyMain.doWork();
    }
}
