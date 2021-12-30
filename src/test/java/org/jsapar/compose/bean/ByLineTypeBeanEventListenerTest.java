package org.jsapar.compose.bean;

import org.jsapar.TstPerson;
import org.jsapar.model.Line;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class ByLineTypeBeanEventListenerTest {

    @Test
    public void testPut() {
        ByLineTypeBeanEventListener<TstPerson> lineEventListener = new ByLineTypeBeanEventListener<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        });

        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test0")));
        assertFalse(called.get());
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test1")));
        assertFalse(called.get());
    }

    @Test
    public void removeLineEventListener() {
        ByLineTypeBeanEventListener<TstPerson> lineEventListener = new ByLineTypeBeanEventListener<>();
        AtomicBoolean called = new AtomicBoolean(false);
        BeanEventListener<TstPerson> test1Listener = e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        };
        lineEventListener.put("test1", test1Listener);

        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove("test1").isPresent());
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test1")));
        assertFalse(called.get());
    }

    @Test
    public void setDefaultLineEventListener() {
        ByLineTypeBeanEventListener<TstPerson> lineEventListener = new ByLineTypeBeanEventListener<>();
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean defaultCalled = new AtomicBoolean(false);
        lineEventListener.put("test1", e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        });
        lineEventListener.setDefault(e->defaultCalled.set(true));
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test0")));
        assertFalse(called.get());
        assertTrue(defaultCalled.get());
    }

    @Test
    public void removeAllLineEventListeners() {
        ByLineTypeBeanEventListener<TstPerson> lineEventListener = new ByLineTypeBeanEventListener<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put("test1", e -> {
            assertEquals("test1", e.getLine().getLineType());
            called.set(true);
        });

        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test0")));
        assertFalse(called.get());
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        lineEventListener.removeAll();
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstPerson(), new Line("test1")));
        assertFalse(called.get());

    }
}