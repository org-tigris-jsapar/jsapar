package org.jsapar.schema;

import org.junit.Test;

import static org.junit.Assert.*;

public class FixedWidthSchemaTest {

    @Test
    public final void testClone() {
        FixedWidthSchema schema = FixedWidthSchema.builder().withLineSeparator("")
                .withLine("Joho", l->l.withOccurs(2))
                .build();

        FixedWidthSchema theClone = schema.clone();

        assertEquals(schema.getLineSeparator(), theClone.getLineSeparator());

        // Does not clone strings values yet. Might do that in the future.
        assertSame(schema.getLineSeparator(), theClone.getLineSeparator());

        assertEquals(schema.getSchemaLines().iterator().next().getLineType(), theClone.getSchemaLines().iterator().next()
                .getLineType());
        assertNotSame(schema.getSchemaLines().iterator().next(), theClone.getSchemaLines().iterator().next());
    }


    @Test
    public void testIterator_empty() {
        FixedWidthSchema schema = FixedWidthSchema.builder().build();
        assertTrue(schema.isEmpty());
        assertEquals(0, schema.size());
    }

    @Test
    public void testIterator(){
        FixedWidthSchemaLine schemaLine = FixedWidthSchemaLine.builder("Joho").withOccurs(2).build();
        FixedWidthSchema schema = FixedWidthSchema.builder().withLineSeparator("")
                .withLine(schemaLine)
                .build();
        assertFalse(schema.isEmpty());
        assertEquals(1, schema.size());

        assertSame(schemaLine, schema.iterator().next());

    }


    @Test
    public void testToString() {
        FixedWidthSchema schema = FixedWidthSchema.builder().build();
        assertEquals("FixedWidthSchema lineSeparator='\\n' schemaLines={}", schema.toString());
    }
}
