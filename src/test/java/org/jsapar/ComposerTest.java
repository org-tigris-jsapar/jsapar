package org.jsapar;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.jsapar.model.DateCell;
import org.jsapar.model.Document;
import org.jsapar.model.IntegerCell;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.*;
import org.junit.Before;
import org.junit.Test;

public class ComposerTest {
    private Document       document;
    private java.util.Date birthTime;

    @Before
    public void setUp() throws Exception {
        document = new Document();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.birthTime = dateFormat.parse("1971-03-25 23:04:24");

        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("FirstName", "Jonas"));
        line1.addCell(new StringCell("LastName", "Stenberg"));
        line1.addCell(new IntegerCell("ShoeSize", 42));
        line1.addCell(new DateCell("BirthTime", this.birthTime));
        line1.addCell(new IntegerCell("LuckyNumber", 123456787901234567L));
        // line1.addCell(new StringCell("NeverUsed", "Should not be assigned"));

        Line line2 = new Line("org.jsapar.TstPerson");
        line2.addCell(new StringCell("FirstName", "Frida"));
        line2.addCell(new StringCell("LastName", "Bergsten"));

        document.addLine(line1);
        document.addLine(line2);

    }

    @Test
    public final void testWrite() throws JSaParException {
        String sExpected = "JonasStenberg" + System.getProperty("line.separator") + "FridaBergsten";
        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("FirstName", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("LastName", 8));
        schema.addSchemaLine(schemaLine);

        Composer composer = new Composer(schema);
        java.io.Writer writer = new java.io.StringWriter();
        composer.write(document, writer);

        assertEquals(sExpected, writer.toString());
    }

    @Test
    public final void testWriteCsv() throws JSaParException {
        String sExpected = "Jonas;Stenberg" + System.getProperty("line.separator") + "Frida;Bergsten";
        org.jsapar.schema.CsvSchema schema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(2);
        schemaLine.addSchemaCell(new CsvSchemaCell("FirstName"));
        schemaLine.addSchemaCell(new CsvSchemaCell("LastName"));
        schema.addSchemaLine(schemaLine);

        Composer composer = new Composer(schema);
        java.io.Writer writer = new java.io.StringWriter();
        composer.write(document, writer);

        assertEquals(sExpected, writer.toString());
    }

    @Test
    public void testOutputLine_FixedWidthControllCell() throws IOException, JSaParException {
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema(1);
        schema.setLineSeparator("");

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.setLineTypeControlValue("N");
        schema.addSchemaLine(schemaLine);

        Line line = new Line("Name");
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));

        StringWriter writer = new StringWriter();
        Composer composer = new Composer(schema);
        composer.writeLine(line, writer);

        assertEquals("NJonasStenberg", writer.toString());
    }

    
    @Test
    public void testOutputLine_FixedWidthControllCell_minLength() throws IOException, JSaParException {
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema(1);
        schema.setLineSeparator("");

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.setLineTypeControlValue("N");
        schemaLine.setMinLength(20);
        schema.addSchemaLine(schemaLine);

        Line line = new Line("Name");
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));

        StringWriter writer = new StringWriter();
        Composer composer = new Composer(schema);
        composer.writeLine(line, writer);

        String result = writer.toString();
        assertEquals(20, result.length());
        assertEquals("NJonasStenberg      ", result);
    }    

}
