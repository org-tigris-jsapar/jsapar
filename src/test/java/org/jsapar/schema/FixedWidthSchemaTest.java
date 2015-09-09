package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FixedWidthSchemaTest {

    @Test
    public final void testClone() throws CloneNotSupportedException {
        FixedWidthSchema schema = new FixedWidthSchema();
        schema.setLineSeparator("");
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.setLineType("Joho");

        schema.addSchemaLine(schemaLine);

        FixedWidthSchema theClone = schema.clone();

        assertEquals(schema.getLineSeparator(), theClone.getLineSeparator());

        // Does not clone strings values yet. Might do that in the future.
        assertTrue(schema.getLineSeparator() == theClone.getLineSeparator());

        assertEquals(schema.getFixedWidthSchemaLines().get(0).getLineType(), theClone.getFixedWidthSchemaLines().get(0)
                .getLineType());
        assertFalse(schema.getFixedWidthSchemaLines().get(0) == theClone.getFixedWidthSchemaLines().get(0));
    }

}
