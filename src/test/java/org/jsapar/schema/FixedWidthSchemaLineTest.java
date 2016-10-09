package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jsapar.JSaParException;
import org.jsapar.error.ErrorEvent;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseException;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthSchemaLineTest {
    
    

    private class NullParsingEventListener implements LineEventListener {

        @Override
        public void lineErrorEvent(ErrorEvent event) throws ParseException {
            throw new ParseException(event.getError());
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) {
        }
    }

    protected boolean foundError = false;
    
    @Before
    public void setup(){
        foundError = false;
    }


    @Test
    public final void testClone() throws CloneNotSupportedException {
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine();
        schemaLine.setLineType("Nisse");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));

        FixedWidthSchemaLine clone = schemaLine.clone();

        assertEquals(schemaLine.getLineType(), clone.getLineType());

        // Does not clone strings values yet. Might do that in the future.
        assertSame(schemaLine.getLineType(), clone.getLineType());
        assertEquals(schemaLine.getSchemaCells().get(0).getName(), clone.getSchemaCells().get(0).getName());
        assertFalse(schemaLine.getSchemaCells().get(0) == clone.getSchemaCells().get(0));
    }

    @Test
    public void testGetCellPositions() throws IOException, JSaParException {
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("City", 8));
        
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
    public void testGetCellFirstPositions() throws IOException, JSaParException {
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("City", 8));
        
        int pos = schemaLine.getCellFirstPosition("First name");
        assertEquals(1, pos);
        
        pos = schemaLine.getCellFirstPosition("Street address");
        assertEquals(14, pos);
        
        pos = schemaLine.getCellFirstPosition("does not exist");
        assertEquals(-1, pos);
        
        
    }
    
    @Test
    public void testGetSchemaCell(){
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        FixedWidthSchemaCell cell1 = new FixedWidthSchemaCell("First name", 5); 
        schemaLine.addSchemaCell(cell1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));

        assertNull(schemaLine.getSchemaCell("Does not exist"));
        assertSame(cell1, schemaLine.getSchemaCell("First name"));
        
    }
    
    @Test
    public void testAddFillerCellToReachMinLength(){
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.setMinLength(10);
        FixedWidthSchemaCell cell1 = new FixedWidthSchemaCell("First name", 5);
        schemaLine.addSchemaCell(cell1);
        assertEquals(5, schemaLine.getTotalCellLenght());
        assertEquals(1, schemaLine.getSchemaCellsCount());
        
        schemaLine.addFillerCellToReachMinLength(2);
        assertEquals(8, schemaLine.getTotalCellLenght());
        assertEquals(2, schemaLine.getSchemaCellsCount());
    }
}
