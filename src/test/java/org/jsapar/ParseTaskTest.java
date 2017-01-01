package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.error.MaxErrorsExceededException;
import org.jsapar.error.RecordingErrorEventListener;
import org.jsapar.error.ThresholdRecordingErrorEventListener;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.parse.CellParseException;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ParseTaskTest {

    @Test
    public void testBuild_fixed_oneLine() throws IOException {
        String toParse = "JonasStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        Document doc = build(toParse, schema);

        assertEquals(1, doc.size());
        assertEquals("Jonas", doc.getLine(0).getCell("First name").getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());
    }

    @Test(expected=CellParseException.class)
    public void testBuild_error_throws() throws IOException {
        String toParse = "JonasAAA";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Shoe size", 3, new SchemaCellFormat(CellType.INTEGER)));
        schema.addSchemaLine(schemaLine);

        Document doc = build(toParse, schema);
    }

    @Test
    public void testBuild_error_list() throws IOException {
        String toParse = "JonasAAA";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Shoe size", 3, new SchemaCellFormat(CellType.INTEGER)));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        List<JSaParException> parseErrors = new ArrayList<>();
        TextParseTask parser = new TextParseTask(schema, reader);
        DocumentBuilder builder = new DocumentBuilder(parser);
        builder.addErrorEventListener(new RecordingErrorEventListener(parseErrors));
        Document doc = builder.build();
        Assert.assertEquals(1, parseErrors.size());
        Assert.assertEquals("Shoe size", ((CellParseException)parseErrors.get(0)).getCellName());
        Assert.assertEquals(1, ((CellParseException)parseErrors.get(0)).getLineNumber());
    }

    @Test(expected=MaxErrorsExceededException.class)
    public void testBuild_error_list_max() throws IOException {
        String toParse = "JonasAAA";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Shoe size", 3, new SchemaCellFormat(CellType.INTEGER)));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        TextParseTask parser = new TextParseTask(schema, reader);
        DocumentBuilder builder = new DocumentBuilder(parser);
        builder.addErrorEventListener(new ThresholdRecordingErrorEventListener(0));
        Document doc = builder.build();
    }
    
    
    @Test
    public void testBuild_fixed_twoLines() throws IOException {
        String toParse = "JonasStenberg" + System.getProperty("line.separator") + "FridaStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        Document doc = build(toParse, schema);

        assertEquals(2, doc.size());
        assertEquals("Jonas", doc.getLine(0).getCell("First name").getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Frida", doc.getLine(1).getCell("First name").getStringValue());
        assertEquals("Stenberg", doc.getLine(1).getCell("Last name").getStringValue());
    }

    private Document build(String toParse, FixedWidthSchema schema) throws IOException {
        Reader reader = new StringReader(toParse);
        TextParseTask parser = new TextParseTask(schema, reader);
        DocumentBuilder builder = new DocumentBuilder(parser);
        return builder.build();
    }

    @Test
    public void testBuild_fixed_twoLines_toLong() throws IOException {
        String toParse = "Jonas " + System.getProperty("line.separator") + "Frida";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schema.addSchemaLine(schemaLine);

        Document doc = build(toParse, schema);
        assertEquals(2, doc.size());
        assertEquals("Jonas", doc.getLine(0).getCell("First name").getStringValue());
        assertEquals("Frida", doc.getLine(1).getCell("First name").getStringValue());
    }

    @Test
    public void testBuild_fixed_twoLines_infiniteOccurs() throws IOException {
        String toParse = "Jonas" + System.getProperty("line.separator") + "Frida";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine();
        schemaLine.setOccursInfinitely();
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schema.addSchemaLine(schemaLine);

        Document doc = build(toParse, schema);

        assertEquals("Jonas", doc.getLine(0).getCell("First name").getStringValue());
        assertEquals("Frida", doc.getLine(1).getCell("First name").getStringValue());
    }
    
}
