package org.springframework.data.chaincode.repository.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SimpleAssetConfig.class);	
		
		try {
			context.getBean(MyService.class).doWork();
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.close();
	}

}
