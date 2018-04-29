package org.springframework.data.chaincode.sdk.client;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionTest {

    @Test
    public void exceptionTest() {
        boolean initThrown;
        boolean eventThrown;
        boolean queryThrown1;
        boolean queryThrown2;
        boolean invokeThrown1;
        boolean invokeThrown2;
        try {
            throw new InitException("Test", new Exception());
        } catch (InitException e) {
            initThrown = true;
        }
        Assert.assertTrue("No init exception thrown", initThrown);

        try {
            throw new EventException("Test", new Exception());
        } catch (EventException e) {
            eventThrown = true;
        }
        Assert.assertTrue("No event exception thrown", eventThrown);

        try {
            throw new QueryException("Test");
        } catch (QueryException e) {
            queryThrown1 = true;
        }
        Assert.assertTrue("No query exception thrown", queryThrown1);

        try {
            throw new QueryException("Test", new Exception());
        } catch (QueryException e) {
            queryThrown2 = true;
        }
        Assert.assertTrue("No query exception thrown", queryThrown2);

        try {
            throw new InvokeException("Test");
        } catch (InvokeException e) {
            invokeThrown1 = true;
        }
        Assert.assertTrue("No invoke exception thrown", invokeThrown1);

        try {
            throw new InvokeException("Test", new Exception());
        } catch (InvokeException e) {
            invokeThrown2 = true;
        }
        Assert.assertTrue("No invoke exception thrown", invokeThrown2);
    }

}
