package org.jsapar.concurrent;

import org.jsapar.Converter;
import org.jsapar.error.JSaParException;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by stejon0 on 2016-11-12.
 */
public class ConcurrentText2TextConverterTest {

    @Test
    public void testConvert() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg \nFrida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Person");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);
        inputSchema.setLineSeparator("\n");

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Person");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);
        outputSchema.setLineSeparator("|");

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new ConcurrentText2TextConverter(inputSchema, reader, outputSchema, writer);
        converter.convert();
        reader.close();
        writer.close();
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg|Frida;Bergsten|";

        Assert.assertEquals(sExpected, sResult);

    }

}