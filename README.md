# Spring Data for Hyperledger Fabric

The primary goal of the [Spring Data](http://projects.spring.io/spring-data) project is to make it easier to build Spring-powered applications that use new data access technologies such as non-relational databases, map-reduce frameworks, and cloud based data services.

The Spring Data for Hyperledger Fabric project aims to provide a familiar and consistent Spring-based programming model to access functionality of Hyperledger Fabric while retaining chaincode (smart contracts) features and capabilities. Key functional areas of Spring Data for Hyperledger Fabric is chaincode invocation abstraction, Hyperledger Fabric connectivity simplification and easily writing a repository style chaincode access layer.

## Getting Help

TODO

## Quick Start

Prerequisites:
* Java 8
* Access to Hyperledger Fabric 1.1

### Maven configuration

Add the Maven dependency:

```xml
<dependency>
  <groupId>org.springframework.data</groupId>
  <artifactId>spring-data-chaincode</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Spring Data repository like interface

Spring Data for Hyperledger Fabric provides Spring Repository like interface Hyperledger Fabric chaincode (smart contract). Fabric Chaincode initializes and manages the ledger state through transactions submitted by applications. For chaincode API explanation see [Chaincode Tutorials](http://hyperledger-fabric.readthedocs.io/en/release/chaincode.html) Chaincode expose to developer set of methods, part of them, like `instantiate` or `upgrade`, invoked in response to chaincode startup transactions and rest of the methods are chaincode application functions invoked as response to `invoke` transaction. In addition, part of Chaincode application functions can be invoked without resulting transaction (`query`) - they used to query state of the ledger.

From client application developer perspective, chaincode is remote piece of code, expose set of methods that can be invoked and those invocation change state of the ledger. To simplify the creation of data repositories Spring Data for Hyperledger Fabric provides a repository like programming model. It will automatically create a repository proxy for you that adds implementations of chaincode methods you specify on an interface.

For example, given a [Simple Asset](http://hyperledger-fabric.readthedocs.io/en/release/chaincode4ade.html#simple-asset-chaincode) chaincode, a `SimpleAssetRepository` interface that can invoke `get` and `put` methods is shown below:

```java
@Chaincode(channel="mychannel", name="sacc", version="1.0")
public interface SimpleAssetRepository extends ChaincodeRepository {
	@ChaincodeInvoke
	String set(String key, String value);

	@ChaincodeQuery
	String get(String key);

}
```

Each method execution will cause execution of corresponding chaincode method.
 `set()` method will cause to chaincode invoke: `peer chaincode invoke -n sacc -c '{"Args":["set", "a", "20"]}' -C mychannel`.
 `get()` method will cause to chaincode query: `peer chaincode query -n sacc -c '{"Args":["query","a"]}' -C mychannel`.

You can have Spring automatically create a proxy for the interface by using the following JavaConfig:

```java
@Configuration
@ComponentScan
@EnableChaincodeRepositories
public class SimpleAssetConfig extends AbstractChaincodeConfiguration {
	@Bean(name = "privateKeyLocation")
	public String privateKeyLocation() {
		return "network/crypto-config/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp"
				+ "/keystore/c75bd6911aca808941c3557ee7c97e90f3952e379497dc55eb903f31b50abc83_sk";
	}

	@Bean(name = "mspId")
	public String mspId() {
		return "Org1MSP";
	}

	@Bean(name = "keyStoreLocation")
	public String keyStoreLocation() {
		return "network/crypto-config/certificates.jks";
	}
}
```

This sets up a connection to a local Hyperledger Fabric instance and enables the detection of chaincode interfaces (repositories) (through `@EnableChaincodeRepositories`).

This will find the repository interface and register a proxy object in the container. You can use it as shown below:

```java
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
```


