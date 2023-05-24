package org.jsapar.compose.bean;

import org.jsapar.TstPerson;
import org.jsapar.model.Line;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class BeanEventTest {

    @Test
    public void getBean() {
        TstPerson person = new TstPerson();
        BeanEvent<TstPerson> beanEvent = new BeanEvent<>(person, new Line("test"));
        assertSame(person, beanEvent.getBean());
    }

    @Test
    public void getLineNumber() {
        TstPerson person = new TstPerson();
        Line line = new Line("test", 16, 17);
        BeanEvent<TstPerson> beanEvent = new BeanEvent<>(person, line);
        assertEquals(17, beanEvent.getLineNumber());
    }

    @Test
    public void getLineType() {
        TstPerson person = new TstPerson();
        BeanEvent<TstPerson> beanEvent = new BeanEvent<>(person, new Line("test"));
        assertEquals("test", beanEvent.getLineType());
    }

    @Test
    public void getLine() {
        TstPerson person = new TstPerson();
        Line line = new Line("test");
        BeanEvent<TstPerson> beanEvent = new BeanEvent<>(person, line);
        assertSame(line, beanEvent.getLine());
    }
}