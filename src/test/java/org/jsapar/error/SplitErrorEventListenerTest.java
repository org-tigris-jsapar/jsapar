package org.jsapar.error;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation")
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

    @Test
    public void testErrorEvent_default() {
        assertThrows(JSaParException.class, () -> {
            MulticastErrorEventListener instance = new MulticastErrorEventListener();
            instance.errorEvent(new ErrorEvent(this, new JSaParException("test")));
        });
    }

    @Test
    public void testErrorEvent_one() {
        MulticastErrorEventListener instance = new MulticastErrorEventListener();
        instance.addEventListener(event -> {        });
        instance.errorEvent(new ErrorEvent(this, new JSaParException("test")));
    }

}