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
    public void iterator() {
        CsvSchema schema = new CsvSchema();
        assertTrue(schema.isEmpty());
        assertEquals(0, schema.size());

        CsvSchemaLine schemaLine = new CsvSchemaLine(2);
        schemaLine.setLineType("Joho");
        schema.addSchemaLine(schemaLine);
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