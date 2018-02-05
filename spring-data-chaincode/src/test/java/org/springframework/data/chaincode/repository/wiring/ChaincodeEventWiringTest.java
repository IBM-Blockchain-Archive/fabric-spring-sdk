package org.springframework.data.chaincode.repository.wiring;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChaincodeEventWiringTest {
    private static AnnotationConfigApplicationContext context;

    @Autowired
    private ChaincodeClient chaincodeClient;

    @Autowired
    private ChaincodeEventsListenerComponent component;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        context.close();
    }

    @Test
    public void testEventWiring() {
        Assert.assertNotNull(chaincodeClient);
        Assert.assertNotNull(component);

        Assert.assertEquals(1, ((TestConfig.TestChaincodeClient)chaincodeClient).ccReg.size());
        Assert.assertEquals(1, ((TestConfig.TestChaincodeClient)chaincodeClient).chReg.size());

    }

}
