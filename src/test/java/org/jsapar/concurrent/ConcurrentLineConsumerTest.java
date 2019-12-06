package org.jsapar.concurrent;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ConcurrentLineConsumerTest {
    private AtomicInteger count;
    private boolean started = false;
    private boolean stopped = false;

    @Before
    public void setUp() {
        count = new AtomicInteger(0);
        started = false;
        stopped = false;
    }

    @Test
    public void testLineParsedEvent() throws Exception {
        try (ConcurrentConsumer<Line> instance = new ConcurrentConsumer<>(line -> {
        })) {
            assertEquals(0, instance.size());
            instance.registerOnStart(() -> this.started = true);
            instance.registerOnStop(() -> this.stopped = true);
            instance.accept( new Line(""));
            assertEquals(1, instance.size());
            assertFalse(this.started);
            instance.start();
            assertTrue(this.started);
            Thread.sleep(10L);
            assertEquals(0, instance.size());
            assertFalse(this.stopped);
            assertTrue(instance.isRunning());
        }
        assertTrue(this.stopped);

    }

    @Test
    public void testAddLineEventListener_run() {
        try (ConcurrentConsumer<Line> instance = new ConcurrentConsumer<>(event -> count.getAndIncrement())) {
            assertEquals(0, instance.size());
            instance.accept( new Line(""));
            assertEquals(1, instance.size());
            assertEquals(0, count.get());
            instance.start();
            assertTrue(instance.isRunning());
        }
        assertEquals(1, count.get());

    }

    @Test
    public void testWorkerTakesLongTime() {
        try (ConcurrentConsumer<Line> instance = new ConcurrentConsumer<>(event -> {
            try {
                Thread.sleep(10L);
                count.getAndIncrement();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        )) {
            assertEquals(0, instance.size());
            instance.accept( new Line(""));
            assertEquals(1, instance.size());
            assertEquals(0, count.get());
            instance.start();
        }
        assertEquals(1, count.get());

    }

    @Test
    public void testProducerTakesLongTime() throws Exception {
        ConcurrentConsumer<Line> instance = new ConcurrentConsumer<>(event -> count.getAndIncrement());
        assertEquals(0, instance.size());
        instance.accept( new Line(""));
        instance.accept( new Line(""));

        assertEquals(2, instance.size());
        assertEquals(0, count.get());
        instance.start();

        instance.accept( new Line(""));
        Thread.sleep(10L);
        instance.accept( new Line(""));
        instance.close();
        assertEquals(0, instance.size());
        assertFalse(instance.isRunning());
        assertEquals(4, count.get());
    }

    @Test(expected = JSaParException.class)
    public void testExceptionFromListener_slow() {
        try (ConcurrentConsumer<Line> instance = new ConcurrentConsumer<>(event -> {
            try {
                Thread.sleep(10); // Do some work.
                throw new AssertionError("Testing error");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })) {
            instance.start();
            instance.accept( new Line(""));
        }
        fail("Exception expected");

    }

    @Test(expected = JSaParException.class)
    public void testExceptionFromListener_fast() {
        try (ConcurrentConsumer<Line> instance = new ConcurrentConsumer<>(event -> {
            throw new AssertionError("Testing error");
        })) {
            instance.start();
            instance.accept( new Line(""));
        }
        fail("Exception expected");

    }

}