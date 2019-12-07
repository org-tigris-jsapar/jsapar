package org.jsapar.compose.bean;

import org.jsapar.TstPerson;
import org.jsapar.model.Line;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ByLineTypeBeanConsumerTest {

    @Test
    public void testPut() {
        ByLineTypeBeanConsumer<TstPerson> lineEventListener = new ByLineTypeBeanConsumer<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", (bean, line) -> {
            assertEquals("test1", line.getLineType());
            called.set(true);
        });

        lineEventListener.accept(new TstPerson(), new Line("test0"));
        assertFalse(called.get());
        lineEventListener.accept(new TstPerson(), new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.accept(new TstPerson(), new Line("test1"));
        assertFalse(called.get());
    }

    @Test
    public void removeLineEventListener() {
        ByLineTypeBeanConsumer<TstPerson> lineEventListener = new ByLineTypeBeanConsumer<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", (bean, line) -> {
            assertEquals("test1", line.getLineType());
            called.set(true);
        });

        lineEventListener.accept(new TstPerson(), new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.accept(new TstPerson(), new Line("test1"));
        assertFalse(called.get());
    }

    @Test
    public void setDefaultLineEventListener() {
        ByLineTypeBeanConsumer<TstPerson> lineEventListener = new ByLineTypeBeanConsumer<>();
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean defaultCalled = new AtomicBoolean(false);
        lineEventListener.put("test1", (bean, line) -> {
            assertEquals("test1", line.getLineType());
            called.set(true);
        });
        lineEventListener.setDefault((bean, line) -> defaultCalled.set(true));
        lineEventListener.accept(new TstPerson(), new Line("test0"));
        assertFalse(called.get());
        assertTrue(defaultCalled.get());
    }

    @Test
    public void removeAllLineEventListeners() {
        ByLineTypeBeanConsumer<TstPerson> lineEventListener = new ByLineTypeBeanConsumer<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", (bean, line) -> {
            assertEquals("test1", line.getLineType());
            called.set(true);
        });

        lineEventListener.accept(new TstPerson(), new Line("test0"));
        assertFalse(called.get());
        lineEventListener.accept(new TstPerson(), new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        lineEventListener.removeAll();
        lineEventListener.accept(new TstPerson(), new Line("test1"));
        assertFalse(called.get());

    }
}