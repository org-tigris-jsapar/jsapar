package org.jsapar.parse;

import org.jsapar.model.Line;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

@SuppressWarnings("EmptyMethod")
public class ByLineTypeLineConsumerTest {

    @Test
    public void lineParsedEvent() {
    }

    @Test
    public void testPut() {
        ByLineTypeLineConsumer lineEventListener = new ByLineTypeLineConsumer();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", l -> {
            assertEquals("test1", l.getLineType());
            called.set(true);
        });

        lineEventListener.accept(new Line("test0"));
        assertFalse(called.get());
        lineEventListener.accept(new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.accept(new Line("test1"));
        assertFalse(called.get());
    }

    @Test
    public void removeLineEventListener() {
        ByLineTypeLineConsumer lineEventListener = new ByLineTypeLineConsumer();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", l -> {
            assertEquals("test1", l.getLineType());
            called.set(true);
        });

        lineEventListener.accept(new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.accept(new Line("test1"));
        assertFalse(called.get());
    }

    @Test
    public void setDefaultLineEventListener() {
        ByLineTypeLineConsumer lineEventListener = new ByLineTypeLineConsumer();
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean defaultCalled = new AtomicBoolean(false);
        lineEventListener.put("test1", l -> {
            assertEquals("test1", l.getLineType());
            called.set(true);
        });
        lineEventListener.setDefault(e->defaultCalled.set(true));
        lineEventListener.accept(new Line("test0"));
        assertFalse(called.get());
        assertTrue(defaultCalled.get());
    }

    @Test
    public void removeAllLineEventListeners() {
        ByLineTypeLineConsumer lineEventListener = new ByLineTypeLineConsumer();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", l -> {
            assertEquals("test1", l.getLineType());
            called.set(true);
        });

        lineEventListener.accept(new Line("test0"));
        assertFalse(called.get());
        lineEventListener.accept(new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        lineEventListener.removeAll();
        lineEventListener.accept(new Line("test1"));
        assertFalse(called.get());

    }
}