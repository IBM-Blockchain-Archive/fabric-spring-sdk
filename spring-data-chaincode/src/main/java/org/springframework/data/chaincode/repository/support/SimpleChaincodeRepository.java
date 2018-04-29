/*
 *
 *  Copyright 2017 IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.data.chaincode.repository.support;

import org.springframework.data.chaincode.repository.ChaincodeRepository;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;

/**
 * {@link ChaincodeRepository} basic methods implementation
 *
 * @author Gennady Laventman
 */
public class SimpleChaincodeRepository implements ChaincodeRepository {

    private String channel;
    private String chaincode;
    private String version;
    private ChaincodeClient chaincodeClient;

    public SimpleChaincodeRepository(String channel, String chaincode, String version, ChaincodeClient ccClient) {
        this.channel = channel;
        this.chaincode = chaincode;
        this.version = version;
        this.chaincodeClient = ccClient;

    }

    @Override
    public String instantiate() {
        // Check possibility of base methods
        return "instantiated";
    }
}
