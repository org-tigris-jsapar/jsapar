package org.jsapar;

import org.jsapar.schema.CsvSchema;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class Xml2TextConverterTest {

    @Test
    public void convert() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLineSeparator("\n")
                .withLine("test-line", line->line.withCells("c1", "c2"))
                .build();

        Xml2TextConverter converter = new Xml2TextConverter(schema);
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<document xmlns='http://jsapar.tigris.org/XMLDocumentFormat/2.0'>" +
                "  <line linetype='test-line'>" +
                "    <cell name='c1' type='string'>Value 1:1</cell>" +
                "    <cell name='c2' type='string'>Value 1:2</cell>" +
                "  </line>" +
                "  <line linetype='test-line'>" +
                "    <cell name='c1'>Value 2:1</cell>" +
                "    <cell name='c2'>Value 2:2</cell>" +
                "  </line>" +
                "</document>";
        String result;
        try(Reader r = new StringReader(xml); StringWriter w=new StringWriter()) {
            converter.convert(r, w);
            result = w.toString();
        }
        String[] lines = result.split("\n");
        assertEquals(2, lines.length);
        assertEquals("Value 1:1;Value 1:2", lines[0]);
        assertEquals("Value 2:1;Value 2:2", lines[1]);
    }
}