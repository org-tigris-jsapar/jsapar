package org.jsapar.schema;

import org.jsapar.TstGender;
import org.jsapar.model.CellType;
import org.jsapar.text.EnumFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Schema2XmlExtractorTest {


    /**
     * Test method for {@link org.jsapar.schema.Schema2XmlExtractor#extractXml(java.io.Writer, org.jsapar.schema.Schema)}.
     */
    @Test
    public void testExtractXml_FixedWidth() throws SchemaException {
        StringWriter writer = new StringWriter();
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLineSeparator("")
                .withLine( FixedWidthSchemaLine.builder("Person")
                    .withOccurs(2)
                    .withMinLength(240)
                    .withCell("First name", 5)
                    .withCell("Last name", 8)
                    .build())
                .build();

        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, schema);
        
        String sXml = writer.toString();
//        System.out.println(sXml);
        
        assertNotNull(sXml);
        Xml2SchemaBuilder xml2SchemaBuilder = new Xml2SchemaBuilder();
        // Validating while building
        Schema builtSchema = xml2SchemaBuilder.build(new StringReader(sXml));
        assertEquals(FixedWidthSchema.class, builtSchema.getClass());
    }


    /**
     * Test method for {@link org.jsapar.schema.Schema2XmlExtractor#extractXml(java.io.Writer, org.jsapar.schema.Schema)}.
     */
    @Test
    public void testExtractXml_Csv() throws SchemaException {
        StringWriter writer = new StringWriter();
        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Person")
                        .withOccurs(2)
                        .withCells("First name", "Last name")
                        .withCell(CsvSchemaCell.builder("Gender")
                                .withLocale(Locale.getDefault())
                                .withEnumFormat(TstGender.class, true)
                                .build())
                        .build())
                .build();

        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, schema);
        
        String sXml = writer.toString();
//        System.out.println(sXml);
        
        assertNotNull(sXml);
        Xml2SchemaBuilder xml2SchemaBuilder = new Xml2SchemaBuilder();
        // Validating while building
        Schema builtSchema = xml2SchemaBuilder.build(new StringReader(sXml));
        assertEquals(CsvSchema.class, builtSchema.getClass());
    }


    
}
