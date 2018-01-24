package org.springframework.data.chaincode.repository.wiring;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChaincodeCustomRepoTest {
	private static AnnotationConfigApplicationContext context;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new AnnotationConfigApplicationContext(TestConfig.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Test
	public void testCustomImpl() {
		
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
        		
            System.out.println(beanName + " " + context.getBean(beanName));
        }

		
		TestRepo4 testRepo = context.getBean(TestRepo4.class);
		
        testRepo.invokeMethod("asdf");
        Assert.assertArrayEquals("Wrong result for custom method call", new String[] {"customMethod"}, new String[] {testRepo.customMethod()});
        testRepo.instantiate();
		
	}

}
