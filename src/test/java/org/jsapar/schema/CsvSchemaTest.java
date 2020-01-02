package org.jsapar.schema;

import org.junit.Test;

import static org.junit.Assert.*;

public class CsvSchemaTest {

    @Test
    public void testClone() {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("Joho").withOccurs(2).build();
        CsvSchema schema = CsvSchema.builder()
                .withLine(schemaLine)
                .build();

        CsvSchema clone = schema.clone();
        assertEquals(1, clone.size());

        assertNotSame(schemaLine, clone.iterator().next());
    }
    @Test
    public void empty() {
        CsvSchema schema = CsvSchema.builder().build();
        assertTrue(schema.isEmpty());
        assertEquals(0, schema.size());
    }

    @Test
    public void iterator() {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("Joho").withOccurs(2).build();
        CsvSchema schema = CsvSchema.builder()
                .withLine(schemaLine).build();

        assertFalse(schema.isEmpty());
        assertEquals(1, schema.size());

        assertSame(schemaLine, schema.iterator().next());
    }

    @Test
    public void testToString() {
        CsvSchema schema = CsvSchema.builder().build();
        assertEquals("CsvSchema lineSeparator='\\n' schemaLines={}", schema.toString());
    }
}