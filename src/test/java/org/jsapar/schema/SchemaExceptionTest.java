package org.jsapar.schema;

import org.junit.Test;

import static org.junit.Assert.*;

public class SchemaExceptionTest {

    @Test
    public void getMessage() {
        SchemaException ex = new SchemaException("msg");
        assertEquals("msg", ex.getMessage());
    }
}