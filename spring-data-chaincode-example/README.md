# Spring Data for Hyperledger Fabric example

The example project contains simple Spring service `MyService` that uses `SimpleAssertRepository` to access `SimpleAsset` chaincode (`src/main/resources/chaincode/sacc/sacc.go`)

The Spring context and Service instantiated in main class `MyMain` and access to running chaincode configured in `SimpleAssetConfig` configuration.

## How to run it

The example configured to run locally on Linux/MacOS. If you want to run it on Windows, you can use this explanation as example and update it to work on Windows

First, download correct Hyperledger Fabric docker images. For that use `src/main/resources/network/download.sh` script and you have be connected to network.

Second, start Fabric network, including chaincode installation and instantiation. Use `src/main/resources/network/start.sh` to do this.

And finally, run example itself from your favorite IDE or from command line (after maven build)
