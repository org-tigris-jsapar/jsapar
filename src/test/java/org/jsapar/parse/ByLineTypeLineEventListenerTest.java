package org.jsapar.parse;

import org.jsapar.model.Line;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

@SuppressWarnings({"deprecation", "EmptyMethod"})
public class ByLineTypeLineEventListenerTest {

    @Test
    public void lineParsedEvent() {
    }

    @Test
    public void testPut() {
        ByLineTypeLineEventListener lineEventListener = new ByLineTypeLineEventListener();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        });

        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test0")));
        assertFalse(called.get());
        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test1")));
        assertFalse(called.get());
    }

    @Test
    public void removeLineEventListener() {
        ByLineTypeLineEventListener lineEventListener = new ByLineTypeLineEventListener();
        AtomicBoolean called = new AtomicBoolean(false);
        LineEventListener test1Listener = e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        };
        lineEventListener.put("test1", test1Listener);

        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test1")));
        assertFalse(called.get());
    }

    @Test
    public void setDefaultLineEventListener() {
        ByLineTypeLineEventListener lineEventListener = new ByLineTypeLineEventListener();
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean defaultCalled = new AtomicBoolean(false);
        lineEventListener.put("test1", e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        });
        lineEventListener.setDefault(e->defaultCalled.set(true));
        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test0")));
        assertFalse(called.get());
        assertTrue(defaultCalled.get());
    }

    @Test
    public void removeAllLineEventListeners() {
        ByLineTypeLineEventListener lineEventListener = new ByLineTypeLineEventListener();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        });

        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test0")));
        assertFalse(called.get());
        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        lineEventListener.removeAll();
        lineEventListener.lineParsedEvent(new LineParsedEvent(this, new Line("test1")));
        assertFalse(called.get());

    }
}