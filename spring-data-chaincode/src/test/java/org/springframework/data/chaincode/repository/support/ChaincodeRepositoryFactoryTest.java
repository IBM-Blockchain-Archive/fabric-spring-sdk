package org.springframework.data.chaincode.repository.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChaincodeRepositoryFactoryTest {

    private ChaincodeRepositoryFactory factory;

    @Autowired
    private ChaincodeClient chaincodeClient;

    @Before
    public void setUp() throws Exception {
        factory = new ChaincodeRepositoryFactory(TestCorrectRepo.class, chaincodeClient);
    }

    @Test
    public void getRepositoryBaseClass() {
        RepositoryMetadata metadata = new DefaultRepositoryMetadata(TestCorrectRepo.class);
        Assert.assertEquals("Incorrect base class", SimpleChaincodeRepository.class, factory.getRepositoryBaseClass(metadata));
    }

    @Test
    public void getTargetRepository() {
        Assert.assertTrue("Incorrect target repository", factory.getTargetRepository(null) instanceof SimpleChaincodeRepository);
    }

    @Test
    public void getRepository() {
        Assert.assertNotNull(chaincodeClient);

        TestCorrectRepo correctRepo = factory.getRepository(TestCorrectRepo.class, RepositoryComposition.RepositoryFragments.empty());

        Assert.assertNotNull("Can't create repository proxy", correctRepo);

        Assert.assertEquals("Incorrect data returned by repo method invocation", "invoked", correctRepo.invoke("asdf"));

    }
}