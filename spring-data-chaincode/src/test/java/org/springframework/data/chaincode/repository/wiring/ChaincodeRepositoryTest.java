package org.springframework.data.chaincode.repository.wiring;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
//@Configurable
public class ChaincodeRepositoryTest {

	@Autowired
	@Qualifier("testRepo1")
	private TestRepo1 testRepo1;
	
	@Autowired
	@Qualifier("testBean")
	private String testBean;

	private static AnnotationConfigApplicationContext context;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new AnnotationConfigApplicationContext(TestConfig.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	public void testCorrectRepo() {
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        
        Assert.assertNotNull(testBean);
        
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
	
	@Test
	public void testRepoWithInheritance() {
		TestRepo31 testRepo = context.getBean(TestRepo31.class);

        testRepo.invokeMethod("asdf");
        testRepo.qMethod("wert");
        testRepo.instantiate();

	}
	
	@Test
	public void testCustomImpl() {
		TestRepo4 testRepo = context.getBean(TestRepo4.class);
		
        testRepo.invokeMethod("asdf");
        testRepo.customMethod();
        testRepo.instantiate();
		
	}

}
