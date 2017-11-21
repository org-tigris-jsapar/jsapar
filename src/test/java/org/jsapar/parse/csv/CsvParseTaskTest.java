package org.jsapar.parse.csv;

import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.model.LineUtils;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.schema.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

public class CsvParseTaskTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testParse_oneLine() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = CsvLineParseTaskTest.makeCsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg;Hemgatan 19;111 22;Stockholm";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,1);

        assertEquals(1, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1").orElse("fail"));
        assertEquals("Hemgatan 19", LineUtils.getStringCellValue(line, "2").orElse("fail"));
        assertEquals("111 22", LineUtils.getStringCellValue(line, "3").orElse("fail"));
        assertEquals("Stockholm", LineUtils.getStringCellValue(line, "4").orElse("fail"));
    }

    @Test
    public final void testParse_endingNewLine() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = CsvLineParseTaskTest.makeCsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg;Hemgatan 19;111 22;Stockholm" + System.getProperty("line.separator");
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,1);

        assertEquals(1, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1").orElse("fail"));
        assertEquals("Hemgatan 19", LineUtils.getStringCellValue(line, "2").orElse("fail"));
        assertEquals("111 22", LineUtils.getStringCellValue(line, "3").orElse("fail"));
        assertEquals("Stockholm", LineUtils.getStringCellValue(line, "4").orElse("fail"));
    }

    @Test
    public final void testParse_twoLines() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = CsvLineParseTaskTest.makeCsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader, 2);

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1").orElse("fail"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "1").orElse("fail"));
    }

    @Test
    public final void testParse_emptyLine_ignore() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = CsvLineParseTaskTest.makeCsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator")
                + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader, 2);

        assertEquals(2, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1").orElse("fail"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "1").orElse("fail"));
    }

    @Test
    public final void testParse_emptyLine_ignore_space() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = CsvLineParseTaskTest.makeCsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + " \t \t  "
                + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,2);

        assertEquals(2, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1").orElse("fail"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "0").orElse("fail"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "1").orElse("fail"));
    }


    @Test
    public final void testParse_firstLineAsHeader()
            throws IOException, JSaParException, java.text.ParseException, SchemaException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        CsvSchemaCell shoeSizeCell = new CsvSchemaCell("Shoe Size", CellType.INTEGER);
        shoeSizeCell.setDefaultValue("43");
        schemaLine.addSchemaCell(shoeSizeCell);
        CsvSchemaCell hasDogCell = new CsvSchemaCell("HasDog",CellType.BOOLEAN);
        hasDogCell.setDefaultValue("false");
        schemaLine.addSchemaCell(hasDogCell);
        schemaLine.setFirstLineAsSchema(true);
        schema.addSchemaLine(schemaLine);

        String sLineSep = System.getProperty("line.separator");
        String sToParse = "First Name;Last Name;Shoe Size" + sLineSep + "Jonas;Stenberg;41" + sLineSep
                + "Nils;Nilsson;";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,3);

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "First Name").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last Name").orElse("fail"));
        assertEquals(41, LineUtils.getIntCellValue(line,"Shoe Size", -1));
        assertEquals(Boolean.FALSE, LineUtils.getBooleanCellValue(line, "HasDog").orElseThrow(AssertionError::new));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "First Name").orElse("fail"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "Last Name").orElse("fail"));
        assertEquals(43, LineUtils.getIntCellValue(line,"Shoe Size", -1));
        assertEquals(Boolean.FALSE, LineUtils.getBooleanCellValue(line, "HasDog").orElseThrow(AssertionError::new));
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
        Document doc = build(schema, reader, 3);

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "First Name").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last Name").orElse("fail"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "First Name").orElse("fail"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "Last Name").orElse("fail"));
    }

    @Test
    public void testParseControlCell() throws IOException {
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
        Document doc = build(schema, reader, 2);

        assertEquals(2, doc.size());
        Line line = doc.getLine(0);
        assertEquals("Name", line.getLineType());
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "first.name").orElse("fail"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "last.name").orElse("fail"));

        line = doc.getLine(1);
        assertEquals("Address", line.getLineType());
        assertEquals("Storgatan 4", LineUtils.getStringCellValue(line, "street").orElse("fail"));
        assertEquals("12345", LineUtils.getStringCellValue(line, "postcode").orElse("fail"));
        assertEquals("Storstan", LineUtils.getStringCellValue(line, "post.town").orElse("fail"));
    }

    private Document build(CsvSchema schema, Reader reader, int actualRows) throws IOException {
        CsvParser parser = new CsvParser(reader, schema);
        DocumentBuilderLineEventListener builder = new DocumentBuilderLineEventListener();
        long rows = parser.parse(builder, new ExceptionErrorEventListener());
        assertEquals(actualRows, rows);
        return builder.getDocument();
    }

}
