package org.jsapar.parse.csv;

import org.jsapar.JSaParException;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.parse.LineErrorEvent;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseException;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.MatchingCellValueCondition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CsvControlCellParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.CsvSchema#parse()}
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse() throws JSaParException, IOException {
        CsvSchema schema = new CsvSchema();
        schema.setLineSeparator("\n");
        CsvSchemaLine schemaLine = new CsvSchemaLine("Address");
        schemaLine.setCellSeparator(":");
        CsvSchemaCell schemaCell = new CsvSchemaCell("type");
        schemaCell.setLineCondition(new MatchingCellValueCondition("Address"));
        schemaLine.addSchemaCell(schemaCell);
        schemaLine.addSchemaCell(new CsvSchemaCell("street"));
        schemaLine.addSchemaCell(new CsvSchemaCell("postcode"));
        schemaLine.addSchemaCell(new CsvSchemaCell("post.town"));
        schema.addSchemaLine(schemaLine);

        schemaLine = new CsvSchemaLine("Name");
        CsvSchemaCell nameTypeSchemaCell = new CsvSchemaCell("type");
        nameTypeSchemaCell.setLineCondition(new MatchingCellValueCondition("Name"));
        schemaLine.addSchemaCell(nameTypeSchemaCell);
        schemaLine.addSchemaCell(new CsvSchemaCell("first.name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("last.name"));
        schema.addSchemaLine(schemaLine);

        String sToParse = "Name;Jonas;Stenberg\nAddress:Storgatan 4:12345:Storstan";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        assertEquals(2, doc.getNumberOfLines());
        Line line = doc.getLine(0);
        assertEquals("Name", line.getLineType());
        assertEquals("Jonas", line.getCell("first.name").getStringValue());
        assertEquals("Stenberg", line.getCell("last.name").getStringValue());

        line = doc.getLine(1);
        assertEquals("Address", line.getLineType());
        assertEquals("Storgatan 4", line.getCell("street").getStringValue());
        assertEquals("12345", line.getCell("postcode").getStringValue());
        assertEquals("Storstan", line.getCell("post.town").getStringValue());
    }

    private class DocumentBuilder {
        private Document document = new Document();
        private LineEventListener listener;

        public DocumentBuilder() {
            listener = new LineEventListener() {

                @Override
                public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                    throw new ParseException(event.getParseError());
                }

                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    document.addLine(event.getLine());
                }
            };
        }

        public Document build(java.io.Reader reader, CsvSchema schema) throws JSaParException, IOException {
            CsvParser parser = new CsvParser(reader, schema);
            parser.parse(listener, );
            return this.document;
        }
    }
    
}
