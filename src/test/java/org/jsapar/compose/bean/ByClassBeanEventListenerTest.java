package org.jsapar.compose.bean;

import org.jsapar.TstPerson;
import org.jsapar.model.Line;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ByClassBeanEventListenerTest {

    private class TstEmployee extends TstPerson{

    }

    private class TstAuthor extends TstPerson{

    }

    @Test
    public void put_remove() {
        ByClassBeanEventListener<TstPerson> lineEventListener = new ByClassBeanEventListener<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put(TstEmployee.class, e -> {
            assertEquals(TstEmployee.class, e.getBean().getClass());
            called.set(true);
        });

        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstAuthor(), new Line("test0")));
        assertFalse(called.get());
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstEmployee(), new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove(TstEmployee.class).isPresent());
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstEmployee(), new Line("test1")));
        assertFalse(called.get());
    }


    @Test
    public void removeAll() {
        ByClassBeanEventListener<TstPerson> lineEventListener = new ByClassBeanEventListener<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put(TstEmployee.class, e -> {
            assertEquals(TstEmployee.class, e.getBean().getClass());
            called.set(true);
        });

        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstEmployee(), new Line("test1")));
        assertTrue(called.get());
        called.set(false);
        lineEventListener.removeAll();
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstEmployee(), new Line("test1")));
        assertFalse(called.get());
    }

    @Test
    public void setDefault() {
        ByClassBeanEventListener<TstPerson> lineEventListener = new ByClassBeanEventListener<>();
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean defaultCalled = new AtomicBoolean(false);
        lineEventListener.put(TstEmployee.class, e -> {
            assertEquals(TstEmployee.class, e.getBean().getClass());
            called.set(true);
        });
        lineEventListener.setDefault(e->defaultCalled.set(true));
        lineEventListener.beanComposedEvent(new BeanEvent<>(new TstAuthor(), new Line("test0")));
        assertFalse(called.get());
        assertTrue(defaultCalled.get());
    }
}