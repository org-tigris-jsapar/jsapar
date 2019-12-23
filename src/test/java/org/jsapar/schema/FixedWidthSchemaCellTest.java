package org.jsapar.schema;

import org.junit.Test;

import static org.junit.Assert.*;

public class FixedWidthSchemaCellTest {


    @Test
    public final void testClone() {
        FixedWidthSchemaCell schemaCell =  FixedWidthSchemaCell.builder("First name", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.RIGHT)
                .build();

        FixedWidthSchemaCell clone = schemaCell.clone();

        assertEquals(schemaCell.getName(), clone.getName());
        assertEquals(schemaCell.getAlignment(), clone.getAlignment());

        // Does not clone strings values yet. Might do that in the future.
        assertSame(schemaCell.getName(), clone.getName());
    }

    @Test
    public final void testClone_byBuilder() {
        FixedWidthSchemaCell schemaCell =  FixedWidthSchemaCell.builder("First name", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.RIGHT)
                .build();

        FixedWidthSchemaCell clone = FixedWidthSchemaCell.builder("first name", 10, schemaCell).withPadCharacter('*').build();

        assertEquals("first name", clone.getName());
        assertEquals(schemaCell.getAlignment(), clone.getAlignment());
        assertEquals('*', clone.getPadCharacter());

        // Does not clone strings values yet. Might do that in the future.
        assertNotSame(schemaCell, clone);
    }


    @Test
    public void testSetGetLength() {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.LEFT)
                .withPadCharacter('*')
                .build();
        assertEquals('*', schemaCell.getPadCharacter());
        assertEquals(11, schemaCell.getLength());
        schemaCell.setLength(5);
        assertEquals(5, schemaCell.getLength());
    }

    @Test
    public void testToString() {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.LEFT)
                .withPadCharacter('*')
                .build();
        assertEquals("SchemaCell name='First name' CellType=STRING, Format={StringFormat} length=11 alignment=LEFT", schemaCell.toString());
    }

}
