package org.jsapar.schema;

import org.jsapar.TstGender;
import org.jsapar.model.CellType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author stejon0
 *
 */
public class Schema2XmlExtractorTest {


    /**
     * Test method for {@link org.jsapar.schema.Schema2XmlExtractor#extractXml(java.io.Writer, org.jsapar.schema.Schema)}.
     */
    @Test
    public void testExtractXml_FixedWidth() throws SchemaException {
        StringWriter writer = new StringWriter();
        FixedWidthSchema schema = new FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schemaLine.setMinLength(240);
        schema.setLineSeparator("");

        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);
        
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
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(2);

        schemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        CsvSchemaCell schemaCell = new CsvSchemaCell("Gender", CellType.ENUM, TstGender.class.getName(), Locale.getDefault());

        schemaLine.addSchemaCell(schemaCell);
        schema.addSchemaLine(schemaLine);
        
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
