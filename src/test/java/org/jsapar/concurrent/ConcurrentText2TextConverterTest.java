package org.jsapar.concurrent;

import org.jsapar.error.JSaParException;
import org.jsapar.schema.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 */
public class ConcurrentText2TextConverterTest {


    @Test
    public void testConvert() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg \nFrida Bergsten ";
        FixedWidthSchema inputSchema = FixedWidthSchema.builder()
                .withLine(FixedWidthSchemaLine.builder("Person")
                        .withCell("First name", 6)
                        .withCell("Last name", 9)
                        .build())
                .withLineSeparator("\n")
                .build();

        CsvSchema outputSchema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Person")
                        .withCells("First name", "Last name")
                        .withCellSeparator(";")
                        .build())
                .withLineSeparator("|")
                .build();

        try (StringWriter writer = new StringWriter(); StringReader reader = new StringReader(toParse)) {
            ConcurrentText2TextConverter converter = new ConcurrentText2TextConverter(inputSchema, outputSchema);
            converter.convert(reader, writer);
            String sResult = writer.getBuffer().toString();
            String sExpected = "Jonas;Stenberg|Frida;Bergsten";
            Assert.assertEquals(sExpected, sResult);
        }

    }

}