package org.springframework.data.chaincode.sdk.client;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestWrongFilesLocations {
    @Test
    public void testClientWrongKeyStrore() {
        ChaincodeClient chaincodeClient;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfigWrongKeyStrore.class);
        try {
            chaincodeClient = context.getBean(ChaincodeClient.class);
        } catch (NoSuchBeanDefinitionException e) {
            return;
        }

        Assert.assertNull("No user certificate should be defined", ((ChaincodeClientSDKImpl) chaincodeClient).getUserSigningCert());
    }

    @Test
    public void testClientWrongPrivateKey() {
        ChaincodeClient chaincodeClient;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfigWrongPrivateKey.class);
        chaincodeClient = context.getBean(ChaincodeClient.class);
        Assert.assertNull("No private key should be defined", ((ChaincodeClientSDKImpl) chaincodeClient).getPrivateKey());
    }

    @Test
    public void testClientInvokeWrongConfig() {
        ChaincodeClient chaincodeClient;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfigWrongPrivateKey.class);
        chaincodeClient = context.getBean(ChaincodeClient.class);

        try {
            chaincodeClient.invokeChaincode("mychannel", "mycc", "1.0", "invoke", new String[]{"a", "b", "1"});
        } catch (InvokeException e) {
            return;
        }
        Assert.fail("No InvokeException for not configured client");
    }

    @Test
    public void testClientQueryWrongConfig() {
        ChaincodeClient chaincodeClient;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfigWrongPrivateKey.class);
        chaincodeClient = context.getBean(ChaincodeClient.class);

        try {
            chaincodeClient.invokeQuery("mychannel", "mycc", "1.0", "query", new String[]{"a"});
        } catch (QueryException e) {
            return;
        }
        Assert.fail("No QueryException for not configured client");
    }
}