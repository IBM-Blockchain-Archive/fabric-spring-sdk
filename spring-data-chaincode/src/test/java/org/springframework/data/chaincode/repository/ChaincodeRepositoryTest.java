package org.springframework.data.chaincode.repository;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {TestConfig.class})
public class ChaincodeRepositoryTest {

	@Autowired
	@Qualifier("testRepo1")
	private TestRepo1 testRepo1;

	private AnnotationConfigApplicationContext context;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		context = new AnnotationConfigApplicationContext(TestConfig.class);
	}

	@After
	public void tearDown() throws Exception {
		context.close();
	}
	

	@Test
	public void testCorrectRepo() {
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        
        TestRepo1 testRepo1 = context.getBean(TestRepo1.class);
        
        testRepo1.invokeMethod("asdf");
        testRepo1.qMethod("wert");
        testRepo1.instantiate();
	}

	@Test
	public void testRepoWithoutAnnotation() {
         try {
            TestRepo2 testRepo2 = context.getBean(TestRepo2.class);
		} catch (NoSuchBeanDefinitionException e) {
			return;
		}
        		Assert.fail("TestRepo2 repository should not instantiated");
 	}

}
