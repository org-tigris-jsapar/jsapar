package org.jsapar.parse.csv;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CsvLineParserTest {

    boolean foundError = false;

    @Before
    public void setUp() throws Exception {
        foundError = false;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParse() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        String sLine = "Jonas;Stenberg;Hemvägen 19;111 22;Stockholm";
        CsvLineReader csvLineReader = makeCsvLineReaderForString(sLine);
        boolean rc = new CsvLineParser(schemaLine).parse(csvLineReader, new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
            }
        }, new ExceptionErrorEventListener());
        assertEquals(true, rc);
    }

    public static  CsvSchemaLine makeCsvSchemaLine() {
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        for(int i=0; i<7; i++){
            schemaLine.addSchemaCell(new CsvSchemaCell(String.valueOf(i)));
        }
        return schemaLine;
    }

    private CsvLineReader makeCsvLineReaderForString(String sLine) {
        return new CsvLineReader("\n", new StringReader(sLine));
    }

    @Test
    public void testParse_2byte_unicode() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setCellSeparator("\uFFD0");
        String sLine = "Jonas\uFFD0Stenberg\uFFD0Hemvägen 19\uFFD0111 22\uFFD0Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"\";\"Hemvägen ;19\";\"\"111 22\"\";Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("", line.getCell("2").getStringValue());
                assertEquals("Hemvägen ;19", line.getCell("3").getStringValue());
                assertEquals("\"111 22\"", line.getCell("4").getStringValue());
                assertEquals("Stockholm", line.getCell("5").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted_last() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("Hemvägen ;19", line.getCell("2").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted_after_empty() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("", line.getCell("2").getStringValue());
                assertEquals("Hemvägen ;19", line.getCell("3").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_one_unquoted_empty_between_quoted() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;\"Stenberg\";;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("", line.getCell("2").getStringValue());
                assertEquals("Hemvägen ;19", line.getCell("3").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted_after_unquoted() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;\"Stenberg\";Not quoted;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("Not quoted", line.getCell("2").getStringValue());
                assertEquals("Hemvägen ;19", line.getCell("3").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted_last_cellsep() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19\";";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("Hemvägen ;19", line.getCell("2").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test(expected = JSaParException.class)
    public void testParse_quoted_missing_end() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19;111 22;Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(6, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("\"Hemvägen ", line.getCell("2").getStringValue());
                assertEquals("19", line.getCell("3").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test(expected = JSaParException.class)
    public void testParse_quoted_line_break() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(3, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("\"", line.getCell("2").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted_miss_placed_start() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;H\"emvägen ;19;111 \"22\";\"Stoc\"kholm\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("H\"emvägen ", line.getCell("2").getStringValue());
                assertEquals("19", line.getCell("3").getStringValue());
                assertEquals("111 \"22\"", line.getCell("4").getStringValue());
                assertEquals("Stoc\"kholm", line.getCell("5").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test(expected = JSaParException.class)
    public void testParse_quoted_miss_placed_end() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;1\"9;111 22;Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(7, line.size());
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
                assertEquals("\"Hemvägen ", line.getCell("2").getStringValue());
                assertEquals("1\"9", line.getCell("3").getStringValue());
                assertEquals("111 22", line.getCell("4").getStringValue());
            }
        }, new ExceptionErrorEventListener());

        assertEquals(true, rc);
    }

    @Test
    public void testParse_withNames() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("0"));
        schemaLine.addSchemaCell(new CsvSchemaCell("1"));

        String sLine = "Jonas;-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenberg", line.getCell("1").getStringValue());
            }
        }, new ExceptionErrorEventListener());
        assertEquals(true, rc);

    }

    @Test
    public void testParse_maxLength() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";");
        CsvSchemaCell schemaFirstName = new CsvSchemaCell("0");
        schemaFirstName.setMaxLength(15);
        schemaLine.addSchemaCell(schemaFirstName);
        CsvSchemaCell schemaLastName = new CsvSchemaCell("1");
        schemaLastName.setMaxLength(5);
        schemaLine.addSchemaCell(schemaLastName);

        String sLine = "Jonas;Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Stenb", line.getCell("1").getStringValue());
            }
        }, new ExceptionErrorEventListener());
        assertEquals(true, rc);

    }

    @Test
    public void testParse_withDefault() throws JSaParException, IOException, java.text.ParseException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("0"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setDefaultValue("yes");
        schemaLine.addSchemaCell(happyCell);
        schemaLine.addSchemaCell(new CsvSchemaCell("2"));

        String sLine = "Jonas;-);-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell("0").getStringValue());
                assertEquals("Jonas", line.getStringCellValue("0"));
                assertEquals("Stenberg", line.getCell("2").getStringValue());
                assertEquals("yes", line.getStringCellValue("Happy"));
            }
        }, new ExceptionErrorEventListener());
        assertEquals(true, rc);

    }

    @Test
    public void testParse_default_and_mandatory() throws JSaParException, IOException, java.text.ParseException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setDefaultValue("yes");
        happyCell.setMandatory(true);
        schemaLine.addSchemaCell(happyCell);
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

        String sLine = "Jonas;-);-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getStringCellValue("First Name"));
                assertEquals("Stenberg", line.getCell("Last Name").getStringValue());
                assertEquals("yes", line.getStringCellValue("Happy"));
            }
        }, new ErrorEventListener() {
            @Override
            public void errorEvent(ErrorEvent event) {
                assertEquals("Happy", ((CellParseException)event.getError()).getCellName());
                foundError = true;
            }
        });
        assertEquals(true, rc);
        assertEquals(true, foundError);
    }

    @Test
    public void testParse_withDefaultLast() throws JSaParException, IOException, java.text.ParseException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setDefaultValue("yes");
        schemaLine.addSchemaCell(happyCell);

        String sLine = "Jonas;-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getStringCellValue("First Name"));
                assertEquals("Stenberg", line.getCell("Last Name").getStringValue());
                assertEquals("yes", line.getStringCellValue("Happy"));
            }
        }, new ExceptionErrorEventListener());
        assertEquals(true, rc);

    }

    @Test(expected = CellParseException.class)
    public void testParse_withMandatoryLast() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setMandatory(true);
        schemaLine.addSchemaCell(happyCell);

        String sLine = "Jonas;-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                fail("Expects an error");
            }
        }, new ExceptionErrorEventListener());
        fail("Expects an error");

    }

}
