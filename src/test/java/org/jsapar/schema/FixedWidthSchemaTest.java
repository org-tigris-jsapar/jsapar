package org.jsapar.schema;

import org.junit.Test;

import static org.junit.Assert.*;

public class FixedWidthSchemaTest {

    @Test
    public final void testClone() {
        FixedWidthSchema schema = new FixedWidthSchema();
        schema.setLineSeparator("");
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.setLineType("Joho");

        schema.addSchemaLine(schemaLine);

        FixedWidthSchema theClone = schema.clone();

        assertEquals(schema.getLineSeparator(), theClone.getLineSeparator());

        // Does not clone strings values yet. Might do that in the future.
        assertSame(schema.getLineSeparator(), theClone.getLineSeparator());

        assertEquals(schema.getSchemaLines().iterator().next().getLineType(), theClone.getSchemaLines().iterator().next()
                .getLineType());
        assertFalse(schema.getSchemaLines().iterator().next() == theClone.getSchemaLines().iterator().next());
    }


    @Test
    public void testIterator(){
        FixedWidthSchema schema = new FixedWidthSchema();
        assertTrue(schema.isEmpty());
        assertEquals(0, schema.size());

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.setLineType("Joho");
        schema.addSchemaLine(schemaLine);
        assertFalse(schema.isEmpty());
        assertEquals(1, schema.size());

        assertSame(schemaLine, schema.iterator().next());

    }


    @Test
    public void testToString() {
        FixedWidthSchema schema = new FixedWidthSchema();
        assertEquals("FixedWidthSchema lineSeparator='\\n' locale=en_US schemaLines={}", schema.toString());
    }
}
