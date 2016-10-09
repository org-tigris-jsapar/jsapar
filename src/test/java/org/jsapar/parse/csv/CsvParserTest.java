package org.jsapar.parse.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jsapar.error.ErrorEvent;
import org.jsapar.parse.LineEventListener;
import org.jsapar.model.BooleanCell;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseException;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testParse_oneLine() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg;Hemgatan 19;111 22;Stockholm";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        assertEquals(1, doc.getNumberOfLines());

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());
        assertEquals("Hemgatan 19", line.getCell(2).getStringValue());
        assertEquals("111 22", line.getCell(3).getStringValue());
        assertEquals("Stockholm", line.getCell(4).getStringValue());
    }

    @Test
    public final void testParse_endingNewLine() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg;Hemgatan 19;111 22;Stockholm" + System.getProperty("line.separator");
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        assertEquals(1, doc.getNumberOfLines());

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());
        assertEquals("Hemgatan 19", line.getCell(2).getStringValue());
        assertEquals("111 22", line.getCell(3).getStringValue());
        assertEquals("Stockholm", line.getCell(4).getStringValue());
    }

    @Test
    public final void testParse_twoLines() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell(0).getStringValue());
        assertEquals("Nilsson", line.getCell(1).getStringValue());
    }

    @Test
    public final void testParse_emptyLine_ignore() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator")
                + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        assertEquals(2, doc.getNumberOfLines());

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell(0).getStringValue());
        assertEquals("Nilsson", line.getCell(1).getStringValue());
    }

    @Test
    public final void testParse_emptyLine_ignore_space() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + " \t \t  "
                + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        assertEquals(2, doc.getNumberOfLines());

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell(0).getStringValue());
        assertEquals("Nilsson", line.getCell(1).getStringValue());
    }


    @Test
    public final void testParse_firstLineAsHeader() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        CsvSchemaCell shoeSizeCell = new CsvSchemaCell("Shoe Size", new SchemaCellFormat(CellType.INTEGER));
        shoeSizeCell.setDefaultValue("43");
        schemaLine.addSchemaCell(shoeSizeCell);
        CsvSchemaCell hasDogCell = new CsvSchemaCell("HasDog", new SchemaCellFormat(CellType.BOOLEAN));
        hasDogCell.setDefaultValue("false");
        schemaLine.addSchemaCell(hasDogCell);
        schemaLine.setFirstLineAsSchema(true);
        schema.addSchemaLine(schemaLine);

        String sLineSep = System.getProperty("line.separator");
        String sToParse = "First Name;Last Name;Shoe Size" + sLineSep + "Jonas;Stenberg;41" + sLineSep
                + "Nils;Nilsson;";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell("First Name").getStringValue());
        assertEquals("Stenberg", line.getCell("Last Name").getStringValue());
        assertEquals(41, line.getIntCellValue("Shoe Size"));
        assertEquals(Boolean.FALSE, ((BooleanCell) line.getCell("HasDog")).getBooleanValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell("First Name").getStringValue());
        assertEquals("Nilsson", line.getCell("Last Name").getStringValue());
        assertEquals(43, line.getIntCellValue("Shoe Size"));
        assertEquals(Boolean.FALSE, ((BooleanCell) line.getCell("HasDog")).getBooleanValue());
    }

    @Test
    public final void testParse_firstLineAsHeader_quoted() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        schemaLine.setFirstLineAsSchema(true);
        schemaLine.setQuoteChar('$');
        schema.addSchemaLine(schemaLine);

        String sLineSep = System.getProperty("line.separator");
        String sToParse = "$First Name$;$Last Name$" + sLineSep + "Jonas;$Stenberg$" + sLineSep + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell("First Name").getStringValue());
        assertEquals("Stenberg", line.getCell("Last Name").getStringValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell("First Name").getStringValue());
        assertEquals("Nilsson", line.getCell("Last Name").getStringValue());
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

        public Document build(java.io.Reader reader, CsvSchema schema) throws JSaParException, IOException {
            CsvParser parser = new CsvParser(reader, schema, parseConfig);
            parser.parse(listener, );
            return this.document;
        }
    }

}
