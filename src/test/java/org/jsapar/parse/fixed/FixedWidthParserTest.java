package org.jsapar.parse.fixed;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jsapar.parse.LineEventListener;
import org.jsapar.model.Document;
import org.jsapar.JSaParException;
import org.jsapar.error.ErrorEvent;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseException;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testParse_Flat() throws JSaParException, IOException {
        String toParse = "JonasStenbergFridaStenberg";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schema.setLineSeparator("");

        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        Reader reader = new StringReader(toParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.parse(reader, schema);

        assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

        assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
        assertEquals("Stenberg", doc.getLine(1).getCell("Last name").getStringValue());
    }


    private class DocumentBuilder {
        private Document             document = new Document();
        private LineEventListener listener;

        public DocumentBuilder() {
            listener = new LineEventListener() {

                @Override
                public void lineErrorEvent(ErrorEvent event) throws ParseException {
                    throw new ParseException(event.getError());
                }

                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    document.addLine(event.getLine());
                }
            };
        }

        public Document parse(java.io.Reader reader, FixedWidthSchema schema) throws JSaParException, IOException {

            FixedWidthParser parser = new FixedWidthParserFlat(reader, schema);
            parser.parse(listener, );
            return this.document;
        }
    }

}
