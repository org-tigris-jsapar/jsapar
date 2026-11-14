package org.jsapar.schema;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SchemaExceptionTest {

    @Test
    public void getMessage() {
        SchemaException ex = new SchemaException("msg");
        assertEquals("msg", ex.getMessage());
    }
}