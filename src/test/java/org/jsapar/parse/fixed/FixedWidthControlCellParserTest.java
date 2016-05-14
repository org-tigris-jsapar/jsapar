package org.jsapar.parse.fixed;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jsapar.parse.*;
import org.jsapar.model.Document;
import org.jsapar.JSaParException;
import org.jsapar.schema.*;
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
     *
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse() throws JSaParException, IOException {
        String toParse = "NJonasStenbergAStorgatan 123 45NFred Bergsten";
        org.jsapar.schema.FixedWidthSchema schema = new FixedWidthSchema();
        schema.setLineSeparator("");

        addSchemaLinesOneCharControl(schema);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        checkResult(doc);
    }

    private void addSchemaLinesOneCharControl(FixedWidthSchema schema) {
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name", "N");
        FixedWidthSchemaCell typeN = new FixedWidthSchemaCell("Type", 1);
        typeN.setLineCondition(new MatchingCellValueCondition("N"));
        schemaLine.addSchemaCell(typeN);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        schemaLine = new FixedWidthSchemaLine("Address", "A");
        FixedWidthSchemaCell typeA = new FixedWidthSchemaCell("Type", 1);
        typeA.setLineCondition(new MatchingCellValueCondition("A"));
        schemaLine.addSchemaCell(typeA);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schema.addSchemaLine(schemaLine);
    }

    private void addSchemaLinesTwoCharControl(FixedWidthSchema schema) {
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name", "N");
        FixedWidthSchemaCell typeN = new FixedWidthSchemaCell("Type", 2);
        typeN.setLineCondition(new MatchingCellValueCondition("N"));
        schemaLine.addSchemaCell(typeN);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        schemaLine = new FixedWidthSchemaLine("Address", "AA");
        FixedWidthSchemaCell typeA = new FixedWidthSchemaCell("Type", 2);
        typeA.setLineCondition(new MatchingCellValueCondition("AA"));
        schemaLine.addSchemaCell(typeA);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        schema.addSchemaLine(schemaLine);
    }
    /**
     * Test method for
     *
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse_separatedLines() throws JSaParException, IOException {
        String toParse = "NJonasStenberg   \r\nAStorgatan 123 45          \r\nNFred Bergsten\r\n";
        org.jsapar.schema.FixedWidthSchema schema = new FixedWidthSchema();
        schema.setLineSeparator("\r\n");

        addSchemaLinesOneCharControl(schema);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        checkResult(doc);
    }

    private void checkResult(Document doc) {
        assertEquals("Jonas", doc.getLine(0).getCell("First name").getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Storgatan", doc.getLine(1).getCell("Street").getStringValue());
        assertEquals("123 45", doc.getLine(1).getCell("Zip code").getStringValue());

        assertEquals("Fred", doc.getLine(2).getCell("First name").getStringValue());
        assertEquals("Bergsten", doc.getLine(2).getCell("Last name").getStringValue());
    }

    /**
     * Test method for
     *
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse_spaceInLineType() throws JSaParException, IOException {
        String toParse = "N JonasStenberg   \r\nAAStorgatan 123 45          \r\nN Fred Bergsten";
        org.jsapar.schema.FixedWidthSchema schema = new FixedWidthSchema();
        schema.setLineSeparator("\r\n");

        addSchemaLinesTwoCharControl(schema);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        checkResult(doc);
    }

    /**
     * Test method for
     *
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test(expected = ParseException.class)
    public void testParse_errorOnUndefinedLineType() throws JSaParException, IOException {
        String toParse = "X JonasStenberg   ";
        org.jsapar.schema.FixedWidthSchema schema = new FixedWidthSchema();
        schema.setLineSeparator("\r\n");

        addSchemaLinesTwoCharControl(schema);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        builder.parse(reader, schema);
    }

    /**
     * Test method for
     *
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse_noErrorIfUndefinedLineType() throws JSaParException, IOException {
        String toParse = "N JonasStenberg   \r\nXXStorgatan 123 45          \r\n\r\nN Fred Bergsten";
        org.jsapar.schema.FixedWidthSchema schema = new FixedWidthSchema();
        schema.setLineSeparator("\r\n");
        schema.setErrorIfUndefinedLineType(false);

        addSchemaLinesTwoCharControl(schema);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        assertEquals(2, doc.getNumberOfLines());
        assertEquals("Jonas", doc.getLine(0).getCell("First name").getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Fred", doc.getLine(1).getCell("First name").getStringValue());
        assertEquals("Bergsten", doc.getLine(1).getCell("Last name").getStringValue());
    }

    private class DocumentBuilder {
        private Document          document = new Document();
        private LineEventListener listener;

        public DocumentBuilder() {
            listener = new LineEventListener() {

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

        public Document parse(java.io.Reader reader, FixedWidthSchema schema) throws JSaParException,
                IOException {
            SchemaParserFactory factory = new SchemaParserFactory();
            Parser parser = factory.makeParser(schema, reader);
            parser.parse(listener);
            return this.document;
        }
    }

}
