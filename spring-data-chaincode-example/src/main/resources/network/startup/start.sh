#!/bin/bash

set -ev

export FABRIC_START_TIMEOUT=5
#echo ${FABRIC_START_TIMEOUT}
sleep ${FABRIC_START_TIMEOUT}

# Create the channel
peer channel create -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx
# Join peer0.org1.example.com to the channel.
peer channel join -b mychannel.block
# install chaincode
peer chaincode install -n sass -v 1.0 -p github.com/hyperledger/fabric/examples/chaincode/go/sass
# instantiate chaincode
peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n sass -v 1.0 -c '{"Args":["a","100"]}' -P "OR ('Org1MSP.member','Org2MSP.member')"
