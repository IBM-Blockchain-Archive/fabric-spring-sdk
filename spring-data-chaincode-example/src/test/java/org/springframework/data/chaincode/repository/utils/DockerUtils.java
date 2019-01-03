package org.springframework.data.chaincode.repository.utils;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.testcontainers.DockerClientFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DockerUtils {
    static public void waitForContainers(String[] peers, String[] chaincodes) throws Exception {
        for (int i = 0; i < 20; i++) {
            List<Container> containers = DockerClientFactory.instance().client().listContainersCmd().exec();
            int chaincodeContainerExist[] = new int[chaincodes.length];
            Arrays.fill(chaincodeContainerExist, 0);
            containers.forEach(container -> {
                for (String name : container.getNames()) {
                    for (String peer : peers) {
                        if (name != null && name.indexOf("dev-" + peer) != -1) {
                            for (int j = 0; j < chaincodes.length; j++) {
                                if (name != null && name.indexOf(chaincodes[j]) != -1) {
                                    chaincodeContainerExist[j] += 1;
                                }
                            }
                        }
                    }
                }
            });
            boolean allContainersUp = true;
            for (int j = 0; j < chaincodes.length; j++) {
                allContainersUp = allContainersUp && (chaincodeContainerExist[j] >= peers.length);
            }
            if (allContainersUp) {
                System.out.println("=============>>>>>>>>All expected containers are up!<<<<<<<<==========");
                break;
            }
            TimeUnit.SECONDS.sleep(10);
        }
        TimeUnit.SECONDS.sleep(30);

    }

    static public void removeDevContainerAndImages() throws Exception {
        List<Container> containers = DockerClientFactory.instance().client().listContainersCmd().exec();
        containers.forEach(container -> {
            for (String name : container.getNames()) {
                if (name.indexOf("dev-peer") != -1) {
                    DockerClientFactory.instance().client().stopContainerCmd(container.getId()).exec();
                    break;
                }
            }
        });
        TimeUnit.SECONDS.sleep(10);
        containers.forEach(container -> {
            for (String name : container.getNames()) {
                if (name.indexOf("dev-peer") != -1) {
                    DockerClientFactory.instance().client().removeContainerCmd(container.getId()).exec();
                    break;
                }
            }
        });
        TimeUnit.SECONDS.sleep(10);
        List<Image> images = DockerClientFactory.instance().client().listImagesCmd().exec();

        images.forEach(image -> {
            String names[] = image.getRepoTags();
            if (names != null) {
                for (String name : names) {
                    if (name != null && name.indexOf("dev-peer") != -1) {
                        DockerClientFactory.instance().client().removeImageCmd(image.getId()).exec();
                    }
                }
            }
        });
        TimeUnit.SECONDS.sleep(10);
    }
}
