#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#

docker pull hyperledger/fabric-peer:x86_64-1.1.0
docker pull hyperledger/fabric-ca:x86_64-1.1.0
docker pull hyperledger/fabric-tools:x86_64-1.1.0
docker pull hyperledger/fabric-orderer:x86_64-1.1.0
docker pull hyperledger/fabric-ccenv:x86_64-1.1.0
docker pull hyperledger/fabric-couchdb:x86_64-1.0.6
docker tag hyperledger/fabric-ca:x86_64-1.1.0 hyperledger/fabric-ca:latest
docker tag hyperledger/fabric-peer:x86_64-1.1.0 hyperledger/fabric-peer:latest
docker tag hyperledger/fabric-orderer:x86_64-1.1.0 hyperledger/fabric-orderer:latest
docker tag hyperledger/fabric-tools:x86_64-1.1.0 hyperledger/fabric-tools:latest
docker tag hyperledger/fabric-ccenv:x86_64-1.1.0 hyperledger/fabric-ccenv:latest
docker tag hyperledger/fabric-couchdb:x86_64-1.0.6 hyperledger/fabric-couchdb:latest
docker images
