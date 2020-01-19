package org.jsapar.parse.csv;

import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.LineUtils;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineParseException;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.QuoteSyntax;
import org.jsapar.schema.SchemaException;
import org.jsapar.text.TextParseConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class CsvLineParserTest {

    private boolean foundError = false;

    @Before
    public void setUp() {
        foundError = false;
    }

    @Test
    public void testParse() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        String sLine = "Jonas;Stenberg;Hemvägen 19;111 22;Stockholm";
        CsvLineReader csvLineReader = makeCsvLineReaderForString(sLine);
        boolean rc = new CsvLineParser(schemaLine).parse(csvLineReader, line -> {
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
        }, new ExceptionErrorConsumer());
        assertTrue(rc);
    }

    static CsvSchemaLine makeCsvSchemaLine() {
        return CsvSchemaLine.builder("testLine")
                .withCells("0","1","2","3","4","5","6")
                .build();
    }

    private CsvLineReader makeCsvLineReaderForString(String sLine) {
        return new CsvLineReaderStates("\n", new StringReader(sLine), true, 8 * 1024, QuoteSyntax.FIRST_LAST);
    }

    @Test
    public void testParse_2byte_unicode() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setCellSeparator("\uFFD0");
        String sLine = "Jonas\uFFD0Stenberg\uFFD0Hemvägen 19\uFFD0111 22\uFFD0Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"\";\"Hemvägen ;19\";\"\"111 22\"\";Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("", LineUtils.getStringCellValue(line, "2"));
            assertEquals("Hemvägen ;19", LineUtils.getStringCellValue(line, "3"));
            assertEquals("\"111 22\"", LineUtils.getStringCellValue(line, "4"));
            assertEquals("Stockholm", LineUtils.getStringCellValue(line, "5"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted_last() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("Hemvägen ;19", LineUtils.getStringCellValue(line, "2"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted_after_empty() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("", LineUtils.getStringCellValue(line, "2"));
            assertEquals("Hemvägen ;19", LineUtils.getStringCellValue(line, "3"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_one_unquoted_empty_between_quoted() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;\"Stenberg\";;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("", LineUtils.getStringCellValue(line, "2"));
            assertEquals("Hemvägen ;19", LineUtils.getStringCellValue(line, "3"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted_after_unquoted() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;\"Stenberg\";Not quoted;\"Hemvägen ;19\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("Not quoted", LineUtils.getStringCellValue(line, "2"));
            assertEquals("Hemvägen ;19", LineUtils.getStringCellValue(line, "3"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted_last_cellsep() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19\";";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("Hemvägen ;19", LineUtils.getStringCellValue(line, "2"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted_missing_end() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19;111 22;Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("\"Hemvägen ", LineUtils.getStringCellValue(line, "2"));
            assertEquals("19", LineUtils.getStringCellValue(line, "3"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test(expected = JSaParException.class)
    public void testParse_quoted_line_break() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"";
        TextParseConfig config = new TextParseConfig();
        config.setOnLineInsufficient(ValidationAction.EXCEPTION);
        boolean rc = new CsvLineParser(schemaLine, config).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("\"", LineUtils.getStringCellValue(line, "2"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted_miss_placed_start() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;H\"emvägen ;19;111 \"22\";\"Stoc\"kholm\"";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("H\"emvägen ", LineUtils.getStringCellValue(line, "2"));
            assertEquals("19", LineUtils.getStringCellValue(line, "3"));
            assertEquals("111 \"22\"", LineUtils.getStringCellValue(line, "4"));
            assertEquals("Stoc\"kholm", LineUtils.getStringCellValue(line, "5"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_quoted_miss_placed_end() throws IOException {
        CsvSchemaLine schemaLine = makeCsvSchemaLine();
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;1\"9;111 22;Stockholm";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals(7, line.size());
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
            assertEquals("\"Hemvägen ;1\"9", LineUtils.getStringCellValue(line, "2"));
            assertEquals("111 22", LineUtils.getStringCellValue(line, "3"));
        }, new ExceptionErrorConsumer());

        assertTrue(rc);
    }

    @Test
    public void testParse_withNames() throws IOException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCells("0","1")
                .build();

        String sLine = "Jonas;-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
        }, new ExceptionErrorConsumer());
        assertTrue(rc);

    }

    @Test
    public void testParse_maxLength() throws IOException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";")
                .withCell("0", c->c.withMaxLength(15))
                .withCell("1", c->c.withMaxLength(5))
                .build();

        String sLine = "Jonas;Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("Stenb", LineUtils.getStringCellValue(line, "1"));
        }, new ExceptionErrorConsumer());
        assertTrue(rc);

    }

    @Test
    public void testParse_withDefault() throws IOException, SchemaException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCell("0")
                .withCell("1", c->c.withDefaultValue("yes"))
                .withCell("2")
                .build();

        String sLine = "Jonas;-);-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
            assertEquals("yes", LineUtils.getStringCellValue(line, "1"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "2"));
        }, new ExceptionErrorConsumer());
        assertTrue(rc);

    }

    @Test
    public void testParse_default_and_mandatory() throws IOException, SchemaException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCell("First Name")
                .withCell("Happy", c->c.withDefaultValue("yes").withMandatory(true))
                .withCell("Last Name")
                .build();

        String sLine = "Jonas;-);-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "First Name"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last Name"));
            assertEquals("yes", LineUtils.getStringCellValue(line, "Happy"));
        }, error -> {
            assertEquals("Happy", ((CellParseException) error).getCellName());
            foundError = true;
        });
        assertTrue(rc);
        assertTrue(foundError);
    }

    @Test
    public void testParse_withDefaultLast() throws IOException, SchemaException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCell("First Name")
                .withCell("Last Name")
                .withCell("Happy", c->c.withDefaultValue("yes"))
                .build();

        String sLine = "Jonas;-)Stenberg";
        boolean rc = new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> {
            
            assertEquals("Jonas", LineUtils.getStringCellValue(line, "First Name"));
            assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last Name"));
            assertEquals("yes", LineUtils.getStringCellValue(line, "Happy"));
        }, new ExceptionErrorConsumer());
        assertTrue(rc);

    }

    @Test(expected = LineParseException.class)
    public void testParse_exceptionOnInsufficient() throws IOException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCells("First Name", "Last Name", "Happy")
                .build();

        String sLine = "Jonas;-)Stenberg";
        TextParseConfig config = new TextParseConfig();
        config.setOnLineInsufficient(ValidationAction.EXCEPTION);
        new CsvLineParser(schemaLine, config)
                .parse(makeCsvLineReaderForString(sLine), line -> fail("Should throw exception"),
                        line -> fail("Should throw exception"));
        fail("Should throw exception");
    }

    @Test(expected = LineParseException.class)
    public void testParse_exceptionOnOverflow() throws IOException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCells("First Name", "Last Name")
                .build();

        String sLine = "Jonas;-)Stenberg;-)Some other";
        TextParseConfig config = new TextParseConfig();
        config.setOnLineOverflow(ValidationAction.EXCEPTION);
        new CsvLineParser(schemaLine, config)
                .parse(makeCsvLineReaderForString(sLine), line -> fail("Should throw exception"),
                        line -> fail("Should throw exception"));
        fail("Should throw exception");

    }

    @Test(expected = CellParseException.class)
    public void testParse_withMandatoryLast() throws IOException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCell("First Name")
                .withCell("Last Name")
                .withCell("Happy", c->c.withMandatory(true))
                .build();

        String sLine = "Jonas;-)Stenberg";
        new CsvLineParser(schemaLine).parse(makeCsvLineReaderForString(sLine), line -> fail("Expects an error"), new ExceptionErrorConsumer());
        fail("Expects an error");

    }

}
