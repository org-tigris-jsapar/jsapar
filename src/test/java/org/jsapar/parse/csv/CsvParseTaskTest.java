package org.jsapar.parse.csv;

import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.model.LineUtils;
import org.jsapar.parse.DocumentBuilderLineConsumer;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.MatchingCellValueCondition;
import org.jsapar.text.Format;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

public class CsvParseTaskTest {

    @Test
    public final void testParse_oneLine() throws IOException, JSaParException {
        CsvSchema schema = makeCsvSchema();
        String sToParse = "Jonas;Stenberg;Hemgatan 19;111 22;Stockholm";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,1);

        assertEquals(1, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
        assertEquals("Hemgatan 19", LineUtils.getStringCellValue(line, "2"));
        assertEquals("111 22", LineUtils.getStringCellValue(line, "3"));
        assertEquals("Stockholm", LineUtils.getStringCellValue(line, "4"));
    }

    public CsvSchema makeCsvSchema() {
        return CsvSchema.builder()
                    .withLine(CsvLineParserTest.makeCsvSchemaLine())
                    .build();
    }

    @Test
    public final void testParse_endingNewLine() throws IOException, JSaParException {
        CsvSchema schema = makeCsvSchema();
        String sToParse = "Jonas;Stenberg;Hemgatan 19;111 22;Stockholm" + System.getProperty("line.separator");
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,1);

        assertEquals(1, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));
        assertEquals("Hemgatan 19", LineUtils.getStringCellValue(line, "2"));
        assertEquals("111 22", LineUtils.getStringCellValue(line, "3"));
        assertEquals("Stockholm", LineUtils.getStringCellValue(line, "4"));
    }

    @Test
    public final void testParse_twoLines() throws IOException, JSaParException {
        CsvSchema schema = makeCsvSchema();
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader, 2);

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "1"));
    }

    @Test
    public final void testParse_emptyLine_ignore() throws IOException, JSaParException {
        CsvSchema schema = makeCsvSchema();
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator")
                + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader, 2);

        assertEquals(2, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "1"));
    }

    @Test
    public final void testParse_emptyLine_ignore_space() throws IOException, JSaParException {
        CsvSchema schema = makeCsvSchema();
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + " \t \t  "
                + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,2);

        assertEquals(2, doc.size());

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "1"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "0"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "1"));
    }


    @Test
    public final void testParse_firstLineAsHeader_booleanWithFormat()
            throws IOException, JSaParException {
        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Person")
                        .withFirstLineAsSchema(true)
                        .withCell("Shoe Size", c->c.withType(CellType.INTEGER).withDefaultValue("43"))
                        .withCell("HasDog", c->c.withFormat(Format.ofBooleanInstance("YES", "NO", true)).withDefaultValue("NO"))
                        .build())
                .build();


        String sLineSep = System.getProperty("line.separator");
        String sToParse = "First Name;Last Name;Shoe Size;HasDog" + sLineSep + "Jonas;Stenberg;41;Yes" + sLineSep
                + "Nils;Nilsson;";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader,3);

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "First Name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last Name"));
        assertEquals(41, LineUtils.getIntCellValue(line,"Shoe Size", -1));
        assertEquals(Boolean.TRUE, LineUtils.getBooleanCellValue(line, "HasDog").orElseThrow(AssertionError::new));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "First Name"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "Last Name"));
        assertEquals(43, LineUtils.getIntCellValue(line,"Shoe Size", -1));
        assertEquals(Boolean.FALSE, LineUtils.getBooleanCellValue(line, "HasDog").orElseThrow(AssertionError::new));
    }

    @Test
    public final void testParse_firstLineAsHeader_quoted() throws IOException, JSaParException {
        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("person")
                        .withFirstLineAsSchema(true)
                        .withQuoteChar('$')
                        .build())
                .build();

        String sLineSep = System.getProperty("line.separator");
        String sToParse = "$First Name$;$Last Name$" + sLineSep + "Jonas;$Stenberg$" + sLineSep + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader, 3);

        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "First Name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last Name"));

        line = doc.getLine(1);
        assertEquals("Nils", LineUtils.getStringCellValue(line, "First Name"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(line, "Last Name"));
    }

    @Test
    public void testParseControlCell() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLineSeparator("\n")
                .withLine(CsvSchemaLine.builder("Address")
                        .withCellSeparator(":")
                        .withCell(CsvSchemaCell.builder("type").withLineCondition(new MatchingCellValueCondition("Address")).build())
                        .withCells("street", "postcode", "post.town")
                        .build())
                .withLine(CsvSchemaLine.builder("Name")
                        .withCell(CsvSchemaCell.builder("type").withLineCondition(new MatchingCellValueCondition("Name")).build())
                        .withCells("first.name", "last.name")
                        .build())
                .build();

        String sToParse = "Name;Jonas;Stenberg\nAddress:Storgatan 4:12345:Storstan";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        Document doc = build(schema, reader, 2);

        assertEquals(2, doc.size());
        Line line = doc.getLine(0);
        assertEquals("Name", line.getLineType());
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "first.name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "last.name"));

        line = doc.getLine(1);
        assertEquals("Address", line.getLineType());
        assertEquals("Storgatan 4", LineUtils.getStringCellValue(line, "street"));
        assertEquals("12345", LineUtils.getStringCellValue(line, "postcode"));
        assertEquals("Storstan", LineUtils.getStringCellValue(line, "post.town"));
    }

    private Document build(CsvSchema schema, Reader reader, int actualRows) throws IOException {
        CsvParser parser = new CsvParser(reader, schema);
        DocumentBuilderLineConsumer builder = new DocumentBuilderLineConsumer();
        long rows = parser.parse(builder, new ExceptionErrorConsumer());
        assertEquals(actualRows, rows);
        return builder.getDocument();
    }

}
