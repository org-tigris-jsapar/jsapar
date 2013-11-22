package org.jsapar.io;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FilterConverterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConvert() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg " + System.getProperty("line.separator") + "Frida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Names");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Names");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        FilterConverter converter = new FilterConverter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        reader.close();
        writer.close();
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg" + System.getProperty("line.separator") + "Frida;Bergsten"
                + System.getProperty("line.separator");

        Assert.assertEquals(sExpected, sResult);
    }

    @Test
    public void testConvert_twoKindOfLinesIn_OneKindOut() throws IOException, JSaParException {
        String toParse = "This file contains names" + System.getProperty("line.separator") + "Jonas Stenberg "
                + System.getProperty("line.separator") + "Frida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchemaLine.setLineType("Header");
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchemaLine = new FixedWidthSchemaLine("Names");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine;

        outputSchemaLine = new CsvSchemaLine("Names");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new FilterConverter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg" + System.getProperty("line.separator") + "Frida;Bergsten"
                + System.getProperty("line.separator");

        Assert.assertEquals(sExpected, sResult);

    }


    @Test
    public void testConvert_filter() throws IOException, JSaParException {
        String toParse = "This file contains names" + System.getProperty("line.separator") + "Jonas Stenberg "
                + System.getProperty("line.separator") + "Frida Bergsten "
        + System.getProperty("line.separator") + "Tomas Stornos  ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchemaLine.setLineType("Header");
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchemaLine = new FixedWidthSchemaLine("Names");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine;

        outputSchemaLine = new CsvSchemaLine("Names");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        FilterConverter converter = new FilterConverter(inputSchema, outputSchema);
        converter.setLineFilter(new LineFilter(){
            @Override
            public boolean shouldWrite(Line line) throws JSaParException {
                if(line.getLineType().equals("Names") && line.getCell("First name").getStringValue().equals("Tomas"))
                    return false;
                else
                    return true;
            }});
        
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg" + System.getProperty("line.separator") + "Frida;Bergsten"
                + System.getProperty("line.separator");

        Assert.assertEquals(sExpected, sResult);

    }

}
