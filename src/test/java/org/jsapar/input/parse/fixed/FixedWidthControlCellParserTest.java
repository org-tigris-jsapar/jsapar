package org.jsapar.input.parse.fixed;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.schema.FixedWidthControlCellSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthControlCellParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.FixedWidthControlCellSchema#parse(java.io.Reader, org.jsapar.input.ParsingEventListener)}
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse() throws JSaParException, IOException {
        String toParse = "NJonasStenbergAStorgatan 123 45NFred Bergsten";
        org.jsapar.schema.FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
        schema.setLineSeparator("");
        schema.setControlCellLength(1);

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name", "N");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        schemaLine = new FixedWidthSchemaLine("Address", "A");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Storgatan", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("123 45", doc.getLine(1).getCell("Zip code").getStringValue());

        assertEquals("Fred", doc.getLine(2).getCell(0).getStringValue());
        assertEquals("Bergsten", doc.getLine(2).getCell("Last name").getStringValue());
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.FixedWidthControlCellSchema#parse(java.io.Reader, org.jsapar.input.ParsingEventListener)}
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse_separatedLines() throws JSaParException, IOException {
        String toParse = "NJonasStenberg   \r\nAStorgatan 123 45          \r\nNFred Bergsten\r\n";
        org.jsapar.schema.FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
        schema.setLineSeparator("\r\n");
        schema.setControlCellLength(1);

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name", "N");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        schemaLine = new FixedWidthSchemaLine("Address", "A");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Storgatan", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("123 45", doc.getLine(1).getCell("Zip code").getStringValue());

        assertEquals("Fred", doc.getLine(2).getCell(0).getStringValue());
        assertEquals("Bergsten", doc.getLine(2).getCell("Last name").getStringValue());
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.FixedWidthControlCellSchema#parse(java.io.Reader, org.jsapar.input.ParsingEventListener)}
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse_spaceInLineType() throws JSaParException, IOException {
        String toParse = "N JonasStenberg   \r\nAAStorgatan 123 45          \r\nN Fred Bergsten";
        org.jsapar.schema.FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
        schema.setLineSeparator("\r\n");
        schema.setControlCellLength(2);

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name", "N");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        schemaLine = new FixedWidthSchemaLine("Address", "AA");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Storgatan", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("123 45", doc.getLine(1).getCell("Zip code").getStringValue());

        assertEquals("Fred", doc.getLine(2).getCell(0).getStringValue());
        assertEquals("Bergsten", doc.getLine(2).getCell("Last name").getStringValue());
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.FixedWidthControlCellSchema#parse(java.io.Reader, org.jsapar.input.ParsingEventListener)}
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test(expected = ParseException.class)
    public void testParse_errorOnUndefinedLineType() throws JSaParException, IOException {
        String toParse = "N JonasStenberg   ";
        org.jsapar.schema.FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
        schema.setLineSeparator("\r\n");
        schema.setControlCellLength(2);

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Address", "AA");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        builder.parse(reader, schema);
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.FixedWidthControlCellSchema#parse(java.io.Reader, org.jsapar.input.ParsingEventListener)}
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse_noErrorIfUndefinedLineType() throws JSaParException, IOException {
        String toParse = "N JonasStenberg   \r\nAAStorgatan 123 45          \r\n\r\nN Fred Bergsten";
        org.jsapar.schema.FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
        schema.setLineSeparator("\r\n");
        schema.setControlCellLength(2);
        schema.setErrorIfUndefinedLineType(false);

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name", "N");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        assertEquals(2, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Fred", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("Bergsten", doc.getLine(1).getCell("Last name").getStringValue());
    }

    private class DocumentBuilder {
        private Document             document = new Document();
        private ParsingEventListener listener;

        public DocumentBuilder() {
            listener = new ParsingEventListener() {

                @Override
                public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                    throw new ParseException(event.getCellParseError());
                }

                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    document.addLine(event.getLine());
                }
            };
        }

        public Document parse(java.io.Reader reader, FixedWidthControlCellSchema schema) throws JSaParException,
                IOException {
            FixedWidthControlCellParser parser = new FixedWidthControlCellParser(reader, schema);
            parser.parse(listener);
            return this.document;
        }
    }

}
