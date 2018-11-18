package org.jsapar.schema;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FixedWidthSchemaCellTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }



    @Test
    public final void testClone() {
        FixedWidthSchemaCell schemaCell = new FixedWidthSchemaCell("First name", 11);
        schemaCell.setAlignment(FixedWidthSchemaCell.Alignment.RIGHT);

        FixedWidthSchemaCell clone = schemaCell.clone();

        assertEquals(schemaCell.getName(), clone.getName());
        assertEquals(schemaCell.getAlignment(), clone.getAlignment());

        // Does not clone strings values yet. Might do that in the future.
        assertTrue(schemaCell.getName() == clone.getName());
    }

    @Test
    public final void testAlignmentFit() throws IOException {
        assertEquals("12345", applyFit(FixedWidthSchemaCell.Alignment.LEFT, 5, "123456789"));
        assertEquals("56789", applyFit(FixedWidthSchemaCell.Alignment.RIGHT, 5, "123456789"));
        assertEquals("34567", applyFit(FixedWidthSchemaCell.Alignment.CENTER, 5, "123456789"));

    }

    private String applyFit(FixedWidthSchemaCell.Alignment alignment, int length, String value) throws IOException {
        StringWriter writer = new StringWriter();
        alignment.fit(writer, length, value);
        return writer.toString();
    }

    @Test
    public void testSetGetLength() {
        FixedWidthSchemaCell schemaCell = new FixedWidthSchemaCell("First name", 11, FixedWidthSchemaCell.Alignment.LEFT, '*');
        assertEquals('*', schemaCell.getPadCharacter());
        assertEquals(11, schemaCell.getLength());
        schemaCell.setLength(5);
        assertEquals(5, schemaCell.getLength());
    }

    @Test
    public void testToString() {
        FixedWidthSchemaCell schemaCell = new FixedWidthSchemaCell("First name", 11, FixedWidthSchemaCell.Alignment.LEFT, '*');
        assertEquals("SchemaCell name='First name' cellFormat=CellType=STRING length=11 alignment=LEFT", schemaCell.toString());
    }

}
