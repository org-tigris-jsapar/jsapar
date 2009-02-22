/**
 * 
 */
package org.jsapar.schema;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Assert;

import org.jsapar.StringCell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stejon0
 *
 */
public class Schema2XmlExtractorTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }


    /**
     * Test method for {@link org.jsapar.schema.Schema2XmlExtractor#extractXml(java.io.Writer, org.jsapar.schema.Schema)}.
     * @throws SchemaException 
     */
    @Test
    public void testExtractXml_FixedWidth() throws SchemaException {
        StringWriter writer = new StringWriter();
        FixedWidthSchema schema = new FixedWidthSchema();
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
        schema.setLineSeparator("");

        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);
        
        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, schema);
        
        String sXml = writer.toString();
//        System.out.println(sXml);
        
        Assert.assertNotNull(sXml);
        // TODO Add more accurate tests.
    }

    /**
     * Test method for {@link org.jsapar.schema.Schema2XmlExtractor#extractXml(java.io.Writer, org.jsapar.schema.Schema)}.
     * @throws SchemaException 
     */
    @Test
    public void testExtractXml_FixedWidthControlCell() throws SchemaException {
        StringWriter writer = new StringWriter();
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
//        schema.setLineSeparator("");
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Names", "N");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);
        
        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, schema);
        
        String sXml = writer.toString();
//        System.out.println(sXml);
        
        Assert.assertNotNull(sXml);
        // TODO Add more accurate tests.
        
    }

    /**
     * Test method for {@link org.jsapar.schema.Schema2XmlExtractor#extractXml(java.io.Writer, org.jsapar.schema.Schema)}.
     * @throws SchemaException 
     */
    @Test
    public void testExtractXml_Csv() throws SchemaException {
        StringWriter writer = new StringWriter();
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(2);

        schemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        schema.addSchemaLine(schemaLine);
        
        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, schema);
        
        String sXml = writer.toString();
//        System.out.println(sXml);
        
        Assert.assertNotNull(sXml);
        // TODO Add more accurate tests.
    }

    /**
     * Test method for {@link org.jsapar.schema.Schema2XmlExtractor#extractXml(java.io.Writer, org.jsapar.schema.Schema)}.
     * @throws SchemaException 
     */
    @Test
    public void testExtractXml_CsvControlCell() throws SchemaException {
        StringWriter writer = new StringWriter();
        CsvControlCellSchema schema = new CsvControlCellSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine("Names", "N");
        CsvSchemaCell firstNameCell = new CsvSchemaCell("First name");
        firstNameCell.setDefaultValue(new StringCell("Default", "John"));
        schemaLine.addSchemaCell(firstNameCell);
        schemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        schema.addSchemaLine(schemaLine);
        
        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, schema);
        
        String sXml = writer.toString();
        System.out.println(sXml);
        
        Assert.assertNotNull(sXml);

        Reader reader = new StringReader(sXml);
        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        Schema schema2 = builder.build(reader);
        
        Assert.assertEquals(schema.getLineSeparator(), schema2.getLineSeparator());
        // TODO Add more accurate tests.
        
    }
    
}
