package org.jsapar.parse.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.DocumentBuilder;
import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.parse.LineEventListener;
import org.jsapar.model.BooleanCell;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseException;
import org.jsapar.schema.*;
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
        Document doc = build(schema, reader);

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
        Document doc = build(schema, reader);

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
        Document doc = build(schema, reader);

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
        Document doc = build(schema, reader);

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
        Document doc = build(schema, reader);

        assertEquals(2, doc.getNumberOfLines());

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell(0).getStringValue());
        assertEquals("Nilsson", line.getCell(1).getStringValue());
    }


    @Test
    public final void testParse_firstLineAsHeader() throws IOException, JSaParException, java.text.ParseException {
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
        Document doc = build(schema, reader);

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
        Document doc = build(schema, reader);

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell("First Name").getStringValue());
        assertEquals("Stenberg", line.getCell("Last Name").getStringValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell("First Name").getStringValue());
        assertEquals("Nilsson", line.getCell("Last Name").getStringValue());
    }

    @Test
    public void testParseControlCell() throws JSaParException, IOException {
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
        Document doc = build(schema, reader);

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

    private Document build(CsvSchema schema, Reader reader) throws IOException {
        CsvParser parser = new CsvParser(reader, schema);
        DocumentBuilderLineEventListener builder = new DocumentBuilderLineEventListener();
        parser.parse(builder, new ExceptionErrorEventListener());
        return builder.getDocument();
    }

}
