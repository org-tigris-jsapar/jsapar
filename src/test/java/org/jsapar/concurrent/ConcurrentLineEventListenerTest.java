package org.jsapar.concurrent;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.LineParsedEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConcurrentLineEventListenerTest {
    volatile int count = 0;

    @Before
    public void setUp() throws Exception {
        count = 0;
    }

    @Test
    public void testLineParsedEvent() throws Exception {
        try (ConcurrentLineEventListener instance = new ConcurrentLineEventListener()) {
            assertEquals(0, instance.size());
            instance.lineParsedEvent(new LineParsedEvent(this, new Line()));
            assertEquals(1, instance.size());
            instance.start();
            Thread.sleep(10L);
            assertEquals(0, instance.size());
            assertTrue(instance.isRunning());
        }

    }

    @Test
    public void testAddLineEventListener_run() throws Exception {
        try (ConcurrentLineEventListener instance = new ConcurrentLineEventListener()) {
            assertEquals(0, instance.size());
            instance.lineParsedEvent(new LineParsedEvent(this, new Line()));
            instance.addLineEventListener(event -> count += 1);
            assertEquals(1, instance.size());
            assertEquals(0, count);
            instance.start();
            assertTrue(instance.isRunning());
        }
        assertEquals(1, count);

    }

    @Test
    public void testWorkerTakesLongTime() throws Exception {
        try (ConcurrentLineEventListener instance = new ConcurrentLineEventListener()) {
            assertEquals(0, instance.size());
            instance.lineParsedEvent(new LineParsedEvent(this, new Line()));
            instance.addLineEventListener(event -> {
                try {
                    Thread.sleep(10L);
                    count += 1;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            assertEquals(1, instance.size());
            assertEquals(0, count);
            instance.start();
        }
        assertEquals(1, count);

    }

    @Test
    public void testProducerTakesLongTime() throws Exception {
        ConcurrentLineEventListener instance = new ConcurrentLineEventListener();
        assertEquals(0, instance.size());
        instance.lineParsedEvent(new LineParsedEvent(this, new Line()));
        instance.addLineEventListener(event -> count += 1);
        instance.lineParsedEvent(new LineParsedEvent(this, new Line()));

        assertEquals(2, instance.size());
        assertEquals(0, count);
        instance.start();

        instance.lineParsedEvent(new LineParsedEvent(this, new Line()));
        Thread.sleep(10L);
        instance.lineParsedEvent(new LineParsedEvent(this, new Line()));
        instance.close();
        assertEquals(0, instance.size());
        assertFalse(instance.isRunning());
        assertEquals(4, count);
    }

    @Test(expected = JSaParException.class)
    public void testExceptionFromListener() throws Exception {
        try (ConcurrentLineEventListener instance = new ConcurrentLineEventListener()) {
            instance.addLineEventListener(event -> {
                throw new AssertionError("Testing error");
            });
            instance.start();
            instance.lineParsedEvent(new LineParsedEvent(this, new Line()));

        }
        fail("Exception expected");

    }
}