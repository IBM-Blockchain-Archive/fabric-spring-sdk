package org.springframework.data.chaincode.repository.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {

  private final SimpleAssetRepository repository;

  @Autowired
  public MyService(SimpleAssetRepository repository) {
    this.repository = repository;
  }

  public void doWork() {
  
  	 repository.set("a", "Hello, world");
  	 System.out.println(repository.get("a"));
 }
}