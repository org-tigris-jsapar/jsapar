package org.jsapar.compose.bean;

import org.jsapar.TstPerson;
import org.jsapar.model.Line;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class ByClassBeanConsumerTest {

    private static class TstEmployee extends TstPerson{

    }

    private static class TstAuthor extends TstPerson{

    }

    @Test
    public void put_remove() {
        ByClassBeanConsumer<TstPerson> lineEventListener = new ByClassBeanConsumer<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put(TstEmployee.class, (bean, line) -> {
            assertEquals(TstEmployee.class, bean.getClass());
            called.set(true);
        });

        lineEventListener.accept(new TstAuthor(), new Line("test0"));
        assertFalse(called.get());
        lineEventListener.accept(new TstEmployee(), new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        assertTrue(lineEventListener.remove(TstEmployee.class).isPresent());
        lineEventListener.accept(new TstEmployee(), new Line("test1"));
        assertFalse(called.get());
    }


    @Test
    public void removeAll() {
        ByClassBeanConsumer<TstPerson> lineEventListener = new ByClassBeanConsumer<>();
        AtomicBoolean called = new AtomicBoolean(false);
        lineEventListener.put(TstEmployee.class, (bean, line) -> {
            assertEquals(TstEmployee.class, bean.getClass());
            called.set(true);
        });

        lineEventListener.accept(new TstEmployee(), new Line("test1"));
        assertTrue(called.get());
        called.set(false);
        lineEventListener.removeAll();
        lineEventListener.accept(new TstEmployee(), new Line("test1"));
        assertFalse(called.get());
    }

    @Test
    public void setDefault() {
        ByClassBeanConsumer<TstPerson> lineEventListener = new ByClassBeanConsumer<>();
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean defaultCalled = new AtomicBoolean(false);
        Optional<BiConsumer<TstPerson, Line>> old = lineEventListener.put(TstEmployee.class, (bean, line) -> {
            assertEquals(TstEmployee.class, bean.getClass());
            called.set(true);
        });
        assertTrue(old.isEmpty());
        lineEventListener.setDefault((bean, line)->defaultCalled.set(true));
        lineEventListener.accept(new TstAuthor(), new Line("test0"));
        assertFalse(called.get());
        assertTrue(defaultCalled.get());
    }
}