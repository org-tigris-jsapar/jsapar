package org.jsapar;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jsapar.parse.CellParseError;
import org.jsapar.parse.ParseException;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.convert.MaxErrorsExceededException;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.Assert;
import org.junit.Test;


public class ParserTest {

    @Test
    public void testBuild_fixed_oneLine() throws JSaParException {
        String toParse = "JonasStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader);

        assertEquals(1, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());
    }

    @Test(expected=ParseException.class)
    public void testBuild_error_throws() throws JSaParException {
        String toParse = "JonasAAA";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Shoe size", 3, new SchemaCellFormat(CellType.INTEGER)));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        builder.build(reader);
    }

    @Test
    public void testBuild_error_list() throws JSaParException {
        String toParse = "JonasAAA";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Shoe size", 3, new SchemaCellFormat(CellType.INTEGER)));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        List<CellParseError> parseErrors = new ArrayList<CellParseError>();
        builder.build(reader, parseErrors);
        Assert.assertEquals(1, parseErrors.size());
        Assert.assertEquals("Shoe size", parseErrors.get(0).getCellName());
        Assert.assertEquals(1, parseErrors.get(0).getLineNumber());
    }

    @Test(expected=MaxErrorsExceededException.class)
    public void testBuild_error_list_max() throws JSaParException {
        String toParse = "JonasAAA";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Shoe size", 3, new SchemaCellFormat(CellType.INTEGER)));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        List<CellParseError> parseErrors = new ArrayList<CellParseError>();
        builder.build(reader, parseErrors, 0);
    }
    
    
    @Test
    public void testBuild_fixed_twoLines() throws JSaParException {
        String toParse = "JonasStenberg" + System.getProperty("line.separator") + "FridaStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader);

        assertEquals(2, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(1).getCell("Last name").getStringValue());
    }

    @Test
    public void testBuild_fixed_twoLines_lineType() throws JSaParException {
        String toParse = "JonasStenberg" + System.getProperty("line.separator") + "FridaStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader);

        assertEquals(2, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(1).getCell("Last name").getStringValue());
    }
    
    @Test
    public void testBuild_fixed_twoLines_toLong() throws JSaParException {
        String toParse = "Jonas " + System.getProperty("line.separator") + "Frida";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader);
        assertEquals(2, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
    }

    @Test
    public void testBuild_fixed_twoLines_infiniteOccurs() throws JSaParException {
        String toParse = "Jonas" + System.getProperty("line.separator") + "Frida";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine();
        schemaLine.setOccursInfinitely();
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schema.addSchemaLine(schemaLine);

        TextParser builder = new TextParser(schema);
        Reader reader = new StringReader(toParse);
        Document doc = builder.build(reader);

        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
    }
    
}
