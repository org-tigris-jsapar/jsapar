package org.jsapar.schema;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class FixedWidthSchemaCellTest {


    @Test
    public final void testClone() {
        FixedWidthSchemaCell schemaCell = new FixedWidthSchemaCell("First name", 11);
        schemaCell.setAlignment(FixedWidthSchemaCell.Alignment.RIGHT);

        FixedWidthSchemaCell clone = schemaCell.clone();

        assertEquals(schemaCell.getName(), clone.getName());
        assertEquals(schemaCell.getAlignment(), clone.getAlignment());

        // Does not clone strings values yet. Might do that in the future.
        assertSame(schemaCell.getName(), clone.getName());
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
        assertEquals("SchemaCell name='First name' cellFormat=CellType=STRING, Format={StringFormat} length=11 alignment=LEFT", schemaCell.toString());
    }

}
