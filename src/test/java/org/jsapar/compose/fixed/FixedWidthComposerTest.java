package org.jsapar.compose.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.FixedWidthSchema;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FixedWidthComposerTest {

    @Test
    public final void testOutput_Flat() throws JSaParException {
        String sExpected = "JonasStenbergFridaBergsten";
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLineSeparator("")
                .withLine("Person", line->line
                        .withOccurs(2)
                        .withCell("First name", 5)
                        .withCell("Last name", 8)
                ).build();

        Line line1 = new Line("Person");
        line1.addCell(new StringCell("First name","Jonas"));
        line1.addCell(new StringCell("Last name","Stenberg"));

        Line line2 = new Line("Person");
        line2.addCell(new StringCell("First name","Frida"));
        line2.addCell(new StringCell("Last name","Bergsten"));

        Document doc = new Document();
        doc.addLine(line1);
        doc.addLine(line2);

        java.io.Writer writer = new java.io.StringWriter();
        FixedWidthComposer composer = new FixedWidthComposer(writer, schema);
        composer.compose(doc.iterator());

        assertEquals(sExpected, writer.toString());
    }

}