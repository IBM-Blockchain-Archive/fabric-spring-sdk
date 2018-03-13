package org.springframework.data.chaincode.repository.events.wiring;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.chaincode.events.FabricEventsListenersRegistry;
import org.springframework.data.chaincode.sdk.client.ChaincodeClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { EventsInfrastructureConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class EventListenersRegistryTest {

    @Autowired
    private FabricEventsListenersRegistry registry;

    @Autowired
    private ChaincodeClient chaincodeClient;

    @Autowired
    private ChaincodeEventsListenerComponent component;

    @Test
    public void testRegistry() throws Exception{
        Assert.assertEquals("Number of block event listeners", 1, registry.getBlockEventListeners().size());
        Assert.assertEquals("Number of chaincode event listeners", 1, registry.getChaincodeEventListeners().size());
        Assert.assertEquals("Registrated block event listeners ", 1, ((EventsInfrastructureConfig.TestChaincodeClient)chaincodeClient).chReg.size());
        Assert.assertEquals("Registrated chaincode event listeners ", 1, ((EventsInfrastructureConfig.TestChaincodeClient)chaincodeClient).ccReg.size());

        registry.invokeBlockEventListeners("mychannel", null);
        registry.invokeChaincodeEventListener("mychannel", "eventcc", null);
        Assert.assertEquals("Number of block events received", 1, component.blockEvents);
        Assert.assertEquals("Number of chaincode events received", 1, component.ccEvents);
    }


}
