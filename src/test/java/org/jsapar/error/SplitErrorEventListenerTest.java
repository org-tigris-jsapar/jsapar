package org.jsapar.error;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SplitErrorEventListenerTest {


    @Test
    public void testRemoveEventListener() {
        MulticastErrorEventListener instance = new MulticastErrorEventListener();
        ErrorEventListener eventListener = event -> {        };
        assertEquals(0, instance.size());
        instance.addEventListener(eventListener);
        assertEquals(1, instance.size());
        instance.removeEventListener(eventListener);
        assertEquals(0, instance.size());
    }

    @Test(expected = JSaParException.class)
    public void testErrorEvent_default() {
        MulticastErrorEventListener instance = new MulticastErrorEventListener();
        instance.errorEvent(new ErrorEvent(this, new JSaParException("test")));
    }

    @Test
    public void testErrorEvent_one() {
        MulticastErrorEventListener instance = new MulticastErrorEventListener();
        instance.addEventListener(event -> {        });
        instance.errorEvent(new ErrorEvent(this, new JSaParException("test")));
    }

}