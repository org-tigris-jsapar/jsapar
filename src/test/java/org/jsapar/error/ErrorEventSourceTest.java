package org.jsapar.error;

import org.jsapar.parse.ParseException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by stejon0 on 2016-10-16.
 */
public class ErrorEventSourceTest {


    @Test
    public void testRemoveEventListener() throws Exception {
        ErrorEventSource instance = new ErrorEventSource();
        ErrorEventListener eventListener = new ErrorEventListener() {
            @Override
            public void errorEvent(ErrorEvent event) {
            }
        };
        assertEquals(0, instance.size());
        instance.addEventListener(eventListener);
        assertEquals(1, instance.size());
        instance.removeEventListener(eventListener);
        assertEquals(0, instance.size());
    }

    @Test(expected = ParseException.class)
    public void testErrorEvent_default() throws Exception {
        ErrorEventSource instance = new ErrorEventSource();
        instance.errorEvent(new ErrorEvent(this, new JSaParError("test")));
    }

    @Test
    public void testErrorEvent_one() throws Exception {
        ErrorEventSource instance = new ErrorEventSource();
        instance.addEventListener(new ErrorEventListener() {
            @Override
            public void errorEvent(ErrorEvent event) {
            }
        });
        instance.errorEvent(new ErrorEvent(this, new JSaParError("test")));
    }

}