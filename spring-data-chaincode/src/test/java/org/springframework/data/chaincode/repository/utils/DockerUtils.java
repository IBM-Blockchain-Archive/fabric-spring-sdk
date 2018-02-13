package org.springframework.data.chaincode.repository.utils;

import com.github.dockerjava.api.model.Container;
import org.testcontainers.DockerClientFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DockerUtils {
    static public void waitForContainers() throws Exception{
        for (int i = 0; i < 20; i++) {
            List<Container> containers = DockerClientFactory.instance().client().listContainersCmd().exec();
            AtomicBoolean exampleContainerExist = new AtomicBoolean(false);
            AtomicBoolean eventsContainerExist = new AtomicBoolean(false);
            containers.forEach(container -> {
                for (String name : container.getNames()) {
                    if (name.indexOf("eventcc") != -1) {
                        eventsContainerExist.set(true);
                    }
                    if (name.indexOf("mycc") != -1) {
                        exampleContainerExist.set(true);
                    }
                }
            });
            if (exampleContainerExist.get() && eventsContainerExist.get()) {
                break;
            }
            TimeUnit.SECONDS.sleep(10);
        }
        TimeUnit.SECONDS.sleep(10);

    }
}
