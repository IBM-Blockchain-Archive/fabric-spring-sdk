package org.springframework.data.chaincode.repository.asdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {SimpleTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleTest {
	@Autowired
	private SimpleTestBean testBean;

	@Test
	public void testCorrectRepo() {
        
        Assert.assertNotNull(testBean);
	}

}
