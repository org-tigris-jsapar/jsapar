package org.jsapar.parse;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class MulticastLineEventListenerTest {

    @Test
    public void testLineParsedEvent() {
        MulticastLineEventListener listener = new MulticastLineEventListener();
        assertTrue(listener.isEmpty());
        AtomicBoolean firstCalled = new AtomicBoolean(false);
        AtomicBoolean secondCalled = new AtomicBoolean(false);
        listener.addLineEventListener(event ->firstCalled.getAndSet(true) );
        assertFalse(listener.isEmpty());
        assertEquals(1, listener.size());
        listener.addLineEventListener(event ->secondCalled.getAndSet(true) );
        listener.lineParsedEvent(new LineParsedEvent(this, null));
        assertTrue(firstCalled.get());
        assertTrue(secondCalled.get());
    }

    @Test
    public void removeLineEventListener() {
        MulticastLineEventListener listener = new MulticastLineEventListener();
        final LineEventListener eventListener = event -> {};
        listener.addLineEventListener(eventListener);
        assertEquals(1, listener.size());
        listener.addLineEventListener(null);
        assertEquals(1, listener.size());
        listener.removeLineEventListener(eventListener);
        assertEquals(0, listener.size());

    }

}