package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.schema.*;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TextComposerTest {
    private Document document;

    @Before
    public void setUp() throws Exception {
        document = new Document();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date birthTime = dateFormat.parse("1971-03-25 23:04:24");

        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("FirstName", "Jonas"));
        line1.addCell(new StringCell("LastName", "Stenberg"));
        line1.addCell(new IntegerCell("ShoeSize", 42));
        line1.addCell(new DateCell("BirthTime", birthTime));
        line1.addCell(new IntegerCell("LuckyNumber", 123456787901234567L));
        // line1.addCell(new StringCell("NeverUsed", "Should not be assigned"));

        Line line2 = new Line("org.jsapar.TstPerson");
        line2.addCell(new StringCell("FirstName", "Frida"));
        line2.addCell(new StringCell("LastName", "Bergsten"));

        document.addLine(line1);
        document.addLine(line2);

    }

    @Test
    public final void testWrite() {
        String sExpected = "JonasStenberg" + System.getProperty("line.separator") + "FridaBergsten";
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLine(FixedWidthSchemaLine.builder("org.jsapar.TstPerson")
                        .withCell("FirstName", 5)
                        .withCell("LastName", 8)
                        .build())
                .build();

        Writer writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        assertSame(schema, composer.getSchema());
        composer.compose(document);

        assertEquals(sExpected, writer.toString());
    }

    @Test
    public final void testWriteCsv() {
        String sExpected = "Jonas;Stenberg" + System.getProperty("line.separator") + "Frida;Bergsten";
        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("org.jsapar.TstPerson")
                        .withCells("FirstName", "LastName")
                        .build())
                .build();

        Writer writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        composer.compose(document);

        assertEquals(sExpected, writer.toString());
    }

    @Test
    public void testOutputLine_FixedWidthControllCell()
            throws JSaParException {
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLineSeparator("")
                .withLine(FixedWidthSchemaLine.builder("Name")
                        .withCell(FixedWidthSchemaCell.builder("Type", 1)
                                .withDefaultValue("N")
                                .withLineCondition(new MatchingCellValueCondition("N"))
                                .build())
                        .withCell("First name", 5)
                        .withCell("Last name", 8)
                        .build())
                .build();

        Line line = new Line("Name");
        line.addCell(new StringCell("First name", "Jonas"));
        line.addCell(new StringCell("Last name", "Stenberg"));

        Writer writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        composer.writeLine(line);

        assertEquals("NJonasStenberg", writer.toString());
    }


    @Test
    public void testWriteLine_FixedWidthControllCell_minLength()
            throws JSaParException {
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLineSeparator("")
                .withLine("Name", l ->
                        l.withCell(FixedWidthSchemaCell.builder("Type", 1)
                            .withDefaultValue("N")
                            .withLineCondition(new MatchingCellValueCondition("N"))
                            .build())
                        .withCell("First name", 5)
                        .withCell("Last name", 8)
                        .withMinLength(20))
                .build();

        Line line = new Line("Name");
        line.addCell(new StringCell("First name", "Jonas"));
        line.addCell(new StringCell("Last name", "Stenberg"));

        Writer writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        composer.writeLine(line);

        String result = writer.toString();
        assertEquals(20, result.length());
        assertEquals("NJonasStenberg      ", result);
    }

    @Test
    public final void testWriteLine_csv() throws JSaParException {
        CsvSchema schema = CsvSchema.builder()
                .withLine("Header", line -> line.withOccurs(1).withCell("Header"))
                .withLine("Person", line -> line.withCells("First name", "Last name").withCellSeparator(";"))
                .build();

        Line line1 = new Line("Person");
        line1.addCell(new StringCell("First name", "Jonas"));
        line1.addCell(new StringCell("Last name", "Stenberg"));

        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        assertTrue(composer.writeLine(line1));

        String sExpected = "Jonas;Stenberg";

        assertEquals(sExpected, writer.toString());
    }

    @Test
    public final void testWriteLine_csv_first() throws JSaParException {
        CsvSchema schema = CsvSchema.builder()
                .withLine("Header", line -> line.withOccurs(1).withCell("Header"))
                .withLine("Person", line -> line.withCells("First name", "Last name").withCellSeparator(";"))
                .build();

        Line line1 = new Line("Header");
        line1.addCell(new StringCell("Header", "TheHeader"));
        line1.addCell(new StringCell("Something", "This should not be written"));

        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        assertTrue(composer.writeLine(line1));

        String sExpected = "TheHeader";

        assertEquals(sExpected, writer.toString());
    }

    @Test
    public final void testOutputLine_firstLineAsHeader()
            throws JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.addSchemaCell(new CsvSchemaCell("HeaderHeader"));
        schema.addSchemaLine(schemaLine);

        schemaLine = new CsvSchemaLine("Person");
        schemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last name"));

        schemaLine.addSchemaCell(CsvSchemaCell.builder("Shoe size")
                .withCellType(CellType.INTEGER)
                .withDefaultValue("41")
                .build());

        schemaLine.addSchemaCell(CsvSchemaCell.builder("Birth date")
                .withCellType(CellType.DATE).withPattern("yyyy-MM-dd")
                .build());

        schemaLine.setFirstLineAsSchema(true);
        schema.addSchemaLine(schemaLine);

        Line line1 = new Line("Person");
        line1.addCell(new StringCell("First name", "Jonas"));
        line1.addCell(new StringCell("Last name", "Stenberg"));

        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        assertTrue(composer.writeLine(line1));

        String sLineSep = System.getProperty("line.separator");
        String sExpected = "First name;Last name;Shoe size;Birth date" + sLineSep + "Jonas;Stenberg;41;";

        assertEquals(sExpected, writer.toString());
    }

}
