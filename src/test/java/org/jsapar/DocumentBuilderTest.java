package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.parse.CellParseException;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class DocumentBuilderTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddErrorEventListener() throws Exception {

    }

    @Test
    public void testBuild() throws Exception {

    }

    @Test
    public void testBuild_fixed_oneLine() throws JSaParException, IOException {
        String toParse = "JonasStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        TextParser parser = new TextParser(schema, new StringReader(toParse));
        DocumentBuilder builder = new DocumentBuilder(parser);
        Document doc = builder.build();

        assertEquals(1, doc.size());
        assertEquals("Jonas", doc.getLine(0).getCell("First name").getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());
    }

    @Test(expected= CellParseException.class)
    public void testBuild_error_throws() throws JSaParException, IOException {
        String toParse = "JonasAAA";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Shoe size", 3, new SchemaCellFormat(CellType.INTEGER)));
        schema.addSchemaLine(schemaLine);

        TextParser parser = new TextParser(schema, new StringReader(toParse));
        DocumentBuilder builder = new DocumentBuilder(parser);
        Document doc = builder.build();
    }


}