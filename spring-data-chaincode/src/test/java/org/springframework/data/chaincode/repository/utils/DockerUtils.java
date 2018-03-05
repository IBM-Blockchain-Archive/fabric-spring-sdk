package org.springframework.data.chaincode.repository.utils;

import com.github.dockerjava.api.model.Container;
import org.testcontainers.DockerClientFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DockerUtils {
    static public void waitForContainers(String[] peers) throws Exception{
        for (int i = 0; i < 20; i++) {
            List<Container> containers = DockerClientFactory.instance().client().listContainersCmd().exec();
            AtomicInteger exampleContainerExist = new AtomicInteger(0);
            AtomicInteger eventsContainerExist = new AtomicInteger(0);
            containers.forEach(container -> {
                for (String name : container.getNames()) {
                    for (String peer : peers) {
                        if (name.indexOf("dev-" + peer) != -1) {
                            if (name.indexOf("eventcc") != -1) {
                                eventsContainerExist.incrementAndGet();
                            }
                            if (name.indexOf("mycc") != -1) {
                                exampleContainerExist.incrementAndGet();
                            }
                        }
                    }
                }
            });
            if (exampleContainerExist.get() >= peers.length && eventsContainerExist.get() >= peers.length) {
                break;
            }
            TimeUnit.SECONDS.sleep(10);
        }
        TimeUnit.SECONDS.sleep(10);

    }
}
