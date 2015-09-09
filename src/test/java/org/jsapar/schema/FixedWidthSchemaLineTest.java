package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthSchemaLineTest {
    
    

    private class NullParsingEventListener implements ParsingEventListener {

        @Override
        public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            throw new ParseException(event.getCellParseError());
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
    public void testOutput() throws IOException, JSaParException {
        Line line = new Line();
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));
        line.addCell(new StringCell("Street address", "Spiselvagen 19"));

        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        FixedWidthSchemaCell cityCellSchema = new FixedWidthSchemaCell("City", 8);
        cityCellSchema.setDefaultValue("Huddinge");
        schemaLine.addSchemaCell(cityCellSchema);
        schema.addSchemaLine(schemaLine);

        java.io.Writer writer = new java.io.StringWriter();
        schemaLine.output(line, writer);
        String sResult = writer.toString();

        assertEquals("JonasStenbergSpiselvagen 19      Huddinge", sResult);
    }

    @Test
    public void testOutput_minLength() throws IOException, JSaParException {
        Line line = new Line();
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));
        line.addCell(new StringCell("Street address", "Hemvagen 19"));

        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.setMinLength(50);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        FixedWidthSchemaCell cityCellSchema = new FixedWidthSchemaCell("City", 12);
        cityCellSchema.setDefaultValue("Storstaden");
        schemaLine.addSchemaCell(cityCellSchema);
        schema.addSchemaLine(schemaLine);

        java.io.Writer writer = new java.io.StringWriter();
        schemaLine.output(line, writer);
        String sResult = writer.toString();

        assertEquals("JonasStenbergHemvagen 19         Storstaden       ", sResult);
    }
    
    @Test
    public void testOutput_ignorewrite() throws IOException, JSaParException {
        Line line = new Line();
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));
        line.addCell(new StringCell("Street address", "Spiselvägen 19"));

        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        FixedWidthSchemaCell firstNameSchema = new FixedWidthSchemaCell("First name", 5);
        firstNameSchema.setIgnoreWrite(true);
        schemaLine.addSchemaCell(firstNameSchema);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        java.io.Writer writer = new java.io.StringWriter();
        schemaLine.output(line, writer);
        String sResult = writer.toString();

        assertEquals("     Stenberg", sResult);
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
        assertTrue(schemaLine.getLineType() == clone.getLineType());
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
