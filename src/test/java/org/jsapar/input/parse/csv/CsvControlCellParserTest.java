package org.jsapar.input.parse.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jsapar.input.LineEventListener;
import org.jsapar.model.Document;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.schema.CsvControlCellSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvControlCellParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.CsvControlCellSchema#parse(java.io.Reader, LineEventListener)}
     * .
     * 
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public void testParse() throws JSaParException, IOException {
        CsvControlCellSchema schema = new CsvControlCellSchema();
        schema.setControlCellSeparator(":->");
        CsvSchemaLine schemaLine = new CsvSchemaLine("Address");
        schemaLine.setCellSeparator(":");
        schema.addSchemaLine(schemaLine);

        schemaLine = new CsvSchemaLine("Name");
        schema.addSchemaLine(schemaLine);

        String sToParse = "Name:->Jonas;Stenberg" + System.getProperty("line.separator")
                + "Address:->Storgatan 4:12345:Storstan";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        assertEquals(2, doc.getNumberOfLines());
        Line line = doc.getLine(0);
        assertEquals("Name", line.getLineType());
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());

        line = doc.getLine(1);
        assertEquals("Address", line.getLineType());
        assertEquals("Storgatan 4", line.getCell(0).getStringValue());
        assertEquals("12345", line.getCell(1).getStringValue());
        assertEquals("Storstan", line.getCell(2).getStringValue());
    }

    private class DocumentBuilder {
        private Document document = new Document();
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

        public Document build(java.io.Reader reader, CsvControlCellSchema schema) throws JSaParException, IOException {
            CsvControlCellParser parser = new CsvControlCellParser(reader, schema);
            parser.parse(listener);
            return this.document;
        }
    }
    
}
