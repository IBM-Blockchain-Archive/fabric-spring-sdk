#!/usr/bin/env bash

export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp
# Create the channel
peer channel create -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx
# Join channel
peer channel join -b mychannel.block

# install chaincode
peer chaincode install -n sacc -v 1.0 -p github.com/chaincode/sacc/
# instantiate chaincode
peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n sacc -v 1.0 -c '{"Args":["a","100"]}' -P "OR ('Org1MSP.member','Org2MSP.member')"

