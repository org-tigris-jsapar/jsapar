package org.jsapar.parse.fixed;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.parse.CellParseError;
import org.jsapar.parse.LineEventListener;
import org.jsapar.model.CellType;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.error.ErrorEvent;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthLineParserTest {
    
    boolean foundError = false;

    @Before
    public void setUp() throws Exception {
        foundError = false;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParse() throws IOException, JSaParException {
        String toParse = "JonasStenbergSpiselvagen 19141 59Huddinge";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("City", 8));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine);
        boolean rc = parser.parse(reader, 1, new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell("Last name").getStringValue());
                assertEquals("Spiselvagen 19", line.getCell(2).getStringValue());
                assertEquals("141 59", line.getCell("Zip code").getStringValue());
                assertEquals("Huddinge", line.getCell(4).getStringValue());

                assertEquals("Last name", line.getCell(1).getName());
                assertEquals("Zip code", line.getCell(3).getName());
            }
        }, new ExceptionErrorEventListener() );

        assertEquals(true, rc);
    }

    @Test
    public void testParse_defaultLast() throws IOException, JSaParException, java.text.ParseException {
        String toParse = "JonasStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        FixedWidthSchemaCell cityCell = new FixedWidthSchemaCell("City", 8);
        cityCell.setDefaultValue("Falun");
        schemaLine.addSchemaCell(cityCell);
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine);
        boolean rc = parser.parse(reader, 1, new TestVerifyListener(), new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_default_and_mandatory() throws IOException, JSaParException, java.text.ParseException {
        String toParse = "JonasStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        FixedWidthSchemaCell cityCell = new FixedWidthSchemaCell("City", 8);
        cityCell.setDefaultValue("Falun");
        cityCell.setMandatory(true);
        schemaLine.addSchemaCell(cityCell);
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine);
        boolean rc = parser.parse(reader, 1, new TestVerifyListener(), new ErrorEventListener() {
            @Override
            public void errorEvent(ErrorEvent event) {
                CellParseError e = (CellParseError) event.getError();
                assertEquals("City", e.getCellName());
                foundError=true;
            }
        });

        assertEquals(true, rc);
        assertEquals(true, foundError);
    }

    @Test(expected = org.jsapar.parse.ParseException.class)
    public void testParse_parseError() throws IOException, JSaParException {
        String toParse = "JonasStenbergFortyone";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));

        FixedWidthSchemaCell shoeSizeSchema = new FixedWidthSchemaCell("Shoe size", 8);
        shoeSizeSchema.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaLine.addSchemaCell(shoeSizeSchema);

        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        @SuppressWarnings("unused")
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine);
        parser.parse(reader, 1, new NullParsingEventListener(), new ExceptionErrorEventListener());
    }
    
    
    private class NullParsingEventListener implements LineEventListener {

        @Override
        public void lineParsedEvent(LineParsedEvent event) {
        }
    }

    private class TestVerifyListener implements LineEventListener {

        @Override
        public void lineParsedEvent(LineParsedEvent event) {
            Line line = event.getLine();
            assertEquals("Jonas", line.getCell(0).getStringValue());
            assertEquals("Stenberg", line.getCell("Last name").getStringValue());
            assertEquals("Falun", line.getStringCellValue("City"));

            assertEquals("Last name", line.getCell(1).getName());

        }
    }
}