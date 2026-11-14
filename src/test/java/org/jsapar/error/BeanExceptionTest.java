package org.jsapar.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BeanExceptionTest {

    @Test
    public void beanException(){
        Throwable t = new Exception("Original exception");
        BeanException e = new BeanException("message", t);
        assertSame(t, e.getCause());
        assertEquals("message - Original exception", e.getMessage());
    }

}