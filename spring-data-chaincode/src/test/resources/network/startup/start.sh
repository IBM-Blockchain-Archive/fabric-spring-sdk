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
peer chaincode install -n mycc -v 1.0 -p github.com/hyperledger/fabric/examples/chaincode/go/chaincode_example02
# instantiate chaincode
peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n mycc -v 1.0 -c '{"Args":["init","a","100","b","200"]}' -P "OR ('Org1MSP.member','Org2MSP.member')"
# install eventsender
peer chaincode install -n eventcc -v 1.0 -p github.com/hyperledger/fabric/examples/chaincode/go/eventsender
# instantiate eventsender
peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n eventcc -v 1.0 -c '{"Args":["init"]}' -P "OR ('Org1MSP.member','Org2MSP.member')"
# install marbles
## docker exec  cli peer chaincode install -n marbles -v 1.0 -p github.com/hyperledger/fabric/examples/chaincode/go/marbles02
#instantiate marbles
## docker exec cli peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n marbles -v 1.0 -c '{"Args":["init"]}' -P "OR ('Org1MSP.member','Org2MSP.member')"
