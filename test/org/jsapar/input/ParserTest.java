package org.jsapar.input;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.Test;


public class ParserTest {

    @Test
    public void testParse_fixed_oneLine() throws JSaParException {
        String toParse = "JonasStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        Parser builder = new Parser();
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader, schema);

        assertEquals(1, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());
    }

    @Test
    public void testParse_fixed_twoLines() throws JSaParException {
        String toParse = "JonasStenberg" + System.getProperty("line.separator") + "FridaStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        Parser builder = new Parser();
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader, schema);

        assertEquals(2, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(1).getCell("Last name").getStringValue());
    }

    @Test
    public void testParse_fixed_twoLines_toLong() throws JSaParException {
        String toParse = "Jonas " + System.getProperty("line.separator") + "Frida";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schema.addSchemaLine(schemaLine);

        Parser builder = new Parser();
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader, schema);
        assertEquals(2, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
    }

    @Test
    public void testParse_fixed_twoLines_infiniteOccurs() throws JSaParException {
        String toParse = "Jonas" + System.getProperty("line.separator") + "Frida";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine();
        schemaLine.setOccursInfinitely();
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schema.addSchemaLine(schemaLine);

        Parser builder = new Parser();
        Reader reader = new StringReader(toParse);
        Document doc;
        doc = builder.build(reader, schema);

        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
    }
}
