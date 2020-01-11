package org.jsapar.compose.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaException;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

public class FixedWidthLineComposerTest {

    @Test
    public void testOutput() throws JSaParException {
        Line line = new Line("");
        line.addCell(new StringCell("First name","Jonas"));
        line.addCell(new StringCell("Last name","Stenberg"));
        line.addCell(new StringCell("Street address", "Spiselvagen 19"));

        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("Street address", 14)
                .withCell("Zip code", 6)
                .withCell("City", 8, c->c.withDefaultValue("Huddinge"))
                .build();

        Writer writer = new StringWriter();
        FixedWidthLineComposer composer = new FixedWidthLineComposer(writer, schemaLine);
        composer.compose(line);
        String sResult = writer.toString();

        assertEquals("JonasStenbergSpiselvagen 19      Huddinge", sResult);
    }

    @Test
    public void testOutput_minLength() throws JSaParException {
        Line line = new Line("");
        line.addCell(new StringCell("First name","Jonas"));
        line.addCell(new StringCell("Last name","Stenberg"));
        line.addCell(new StringCell("Street address", "Hemvagen 19"));

        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        schemaLine.setMinLength(50);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
        FixedWidthSchemaCell cityCellSchema = new FixedWidthSchemaCell("City", 12);
        cityCellSchema.setDefaultValue("Storstaden");
        schemaLine.addSchemaCell(cityCellSchema);
        schema.addSchemaLine(schemaLine);

        java.io.Writer writer = new java.io.StringWriter();
        FixedWidthLineComposer composer = new FixedWidthLineComposer(writer, schemaLine);
        composer.compose(line);
        String sResult = writer.toString();

        assertEquals("JonasStenbergHemvagen 19         Storstaden       ", sResult);
    }

    @Test
    public void testOutput_ignorewrite() throws JSaParException {
        Line line = new Line("");
        line.addCell(new StringCell("First name","Jonas"));
        line.addCell(new StringCell("Last name","Stenberg"));
        line.addCell(new StringCell("Street address", "Spiselvägen 19"));

        org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
        FixedWidthSchemaCell firstNameSchema = new FixedWidthSchemaCell("First name", 5);
        firstNameSchema.setIgnoreWrite(true);
        schemaLine.addSchemaCell(firstNameSchema);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        java.io.Writer writer = new java.io.StringWriter();
        FixedWidthLineComposer composer = new FixedWidthLineComposer(writer, schemaLine);
        composer.compose(line);
        String sResult = writer.toString();

        assertEquals("     Stenberg", sResult);
    }


}