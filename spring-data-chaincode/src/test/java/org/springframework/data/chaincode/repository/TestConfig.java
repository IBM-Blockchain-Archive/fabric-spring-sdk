package org.springframework.data.chaincode.repository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.chaincode.repository.config.EnableChaincodeRepositories;

@Configuration
@ComponentScan
@EnableChaincodeRepositories(basePackages = {"org.springframework.data.chaincode.repository"})
public class TestConfig {

}
