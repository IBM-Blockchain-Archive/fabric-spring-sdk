package org.springframework.data.chaincode.repository.asdf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleTestConfig {
	@Bean
	public String testBean() {
		return "testtstst";
	}
	
	@Bean public SimpleTestBean simpleTestBean() {
		System.out.println("Loading SimpleTestBean");
		return new SimpleTestBean();
	}

}
