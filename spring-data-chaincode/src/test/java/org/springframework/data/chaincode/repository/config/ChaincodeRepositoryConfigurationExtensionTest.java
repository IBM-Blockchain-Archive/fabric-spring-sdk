package org.springframework.data.chaincode.repository.config;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.chaincode.repository.Chaincode;
import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ChaincodeRepositoryConfigurationExtensionTest {

    static private ChaincodeRepositoryConfigurationExtension extension;

    @BeforeClass
    public static void setUp() throws Exception {
        extension = new ChaincodeRepositoryConfigurationExtension();
    }

    @Test
    public void getIdentifyingAnnotations() {
        Assert.assertTrue("Not contain correct annotation", extension.getIdentifyingAnnotations().contains(Chaincode.class));
    }

    @Test
    public void getIdentifyingTypes() {
        Assert.assertTrue("Not contain correct base class", extension.getIdentifyingTypes().contains(ChaincodeRepository.class));
    }

    @Test
    public void isStrictRepositoryCandidate() {
        Assert.assertTrue("Correct repo not identified as candidate", extension.isStrictRepositoryCandidate(new DefaultRepositoryMetadata(TestCorrectRepo.class)));
        Assert.assertFalse("Incorrect repo identified as candidate", extension.isStrictRepositoryCandidate(new DefaultRepositoryMetadata(TestIncorrectRepo.class)));
    }
}