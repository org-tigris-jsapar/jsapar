package org.jsapar.schema;

import org.jsapar.error.JSaParException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FixedWidthSchemaLineTest {
    
    protected boolean foundError = false;
    
    @Before
    public void setup(){
        foundError = false;
    }


    @Test
    public final void testClone() {
        FixedWidthSchemaLine schemaLine =  FixedWidthSchemaLine.builder("Nisse")
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .build();

        FixedWidthSchemaLine clone = schemaLine.clone();

        assertEquals(schemaLine.getLineType(), clone.getLineType());

        // Does not clone strings values yet. Might do that in the future.
        assertSame(schemaLine.getLineType(), clone.getLineType());
        assertEquals(schemaLine.iterator().next().getName(), clone.iterator().next().getName());
        assertNotSame(schemaLine.iterator().next(), clone.iterator().next());
    }

    @Test
    public void testGetCellPositions() throws JSaParException {
        FixedWidthSchemaLine schemaLine =  FixedWidthSchemaLine.builder("Nisse")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("Street address", 14)
                .withCell("Zip code", 6)
                .withCell("City", 8)
                .build();

        FixedWidthCellPositions pos = schemaLine.getCellPositions("First name");
        assertEquals(1, pos.getFirst());
        assertEquals(5, pos.getLast());
        
        pos = schemaLine.getCellPositions("Street address");
        assertEquals(14, pos.getFirst());
        assertEquals(27, pos.getLast());
        
        pos = schemaLine.getCellPositions("does not exist");
        assertNull(pos);
        
        
    }

    @Test
    public void testGetCellFirstPositions() throws JSaParException {
        FixedWidthSchemaLine schemaLine =  FixedWidthSchemaLine.builder("Nisse")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("Street address", 14)
                .withCell("Zip code", 6)
                .withCell("City", 8)
                .build();
        
        int pos = schemaLine.getCellFirstPosition("First name");
        assertEquals(1, pos);
        
        pos = schemaLine.getCellFirstPosition("Street address");
        assertEquals(14, pos);
        
        pos = schemaLine.getCellFirstPosition("does not exist");
        assertEquals(-1, pos);
        
        
    }
    
    @Test
    public void testGetSchemaCell(){
        FixedWidthSchemaCell cell1 = FixedWidthSchemaCell.builder("First name", 5).build();

        FixedWidthSchemaLine schemaLine =  FixedWidthSchemaLine.builder("Nisse")
                .withOccurs(1)
                .withCell(cell1)
                .withCell("Last name", 8)
                .build();

        assertTrue(schemaLine.findSchemaCell("Does not exist").isEmpty());
        assertTrue(schemaLine.findSchemaCell("First name").isPresent());
        
    }
    
    @Test
    @Deprecated
    public void testAddFillerCellToReachMinLength(){
        FixedWidthSchemaLine schemaLine = FixedWidthSchemaLine.builder("A")
                .withMinLength(10)
                .withCell("First name", 5)
                .build();

        assertEquals(10, schemaLine.getTotalCellLength());
        assertEquals(2, schemaLine.size()); // A filler cell is added.
    }

    @Test
    public void testBuildWithMinLength(){
        FixedWidthSchemaLine schemaLine =  FixedWidthSchemaLine.builder("Nisse")
                .withOccurs(1)
                .withMinLength(10)
                .withCell("First name", 5)
                .build();

        assertEquals(10, schemaLine.getTotalCellLength());
        assertEquals(2, schemaLine.size());
    }

}
