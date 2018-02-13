package org.springframework.data.chaincode.repository.wiring;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChaincodeRepoBaseMethodsTest {
    @Autowired
    @Qualifier("testRepo1")
    private TestRepo1 testRepo1;

    @Autowired
    private TestRepo31 testRepo31;

    @Test
    public void testBaseMethodDirect() {
        Assert.assertNotNull(testRepo1);
        Assert.assertArrayEquals("Wrong instantiate result", new String[] {"instantiated"}, new String[] {testRepo1.instantiate()});

    }

    @Test
    public void testBaseMethodWithInherirance() {
        Assert.assertNotNull(testRepo31);
        Assert.assertArrayEquals("Wrong instantiate result", new String[] {"instantiated"}, new String[] {testRepo31.instantiate()});
    }
}
