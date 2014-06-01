/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.schema;

import static org.junit.Assert.assertEquals;

import org.jsapar.CellType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonas
 * 
 */
public class Xml2SchemaBuilderTest {

    /**
     * Test method for {@link org.jsapar.schema.Xml2SchemaBuilder#build(java.io.Reader)} .
     * 
     * @throws SchemaException
     */
    @Test
    public final void testBuild_FixedWidth() throws SchemaException {

        String sXmlSchema = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<schema  xmlns='http://jsapar.tigris.org/JSaParSchema/1.0' >"
                + "<fixedwidthschema lineseparator='\\r\\n'>" 
                + "<line occurs='*' linetype='Person' minlength='240'>"
                + "<cell name='First name' length='5'/>" + "<cell name='Last name' length='8'/>"
                + "<cell name='Shoe size' length='8' alignment='right'><format type='integer' pattern='00000000'/></cell>"
                + "</line></fixedwidthschema></schema>";

        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        java.io.Reader reader = new java.io.StringReader(sXmlSchema);
        Schema schema = builder.build(reader);
        FixedWidthSchema fwSchema = (FixedWidthSchema) schema;

        assertEquals("\r\n", fwSchema.getLineSeparator());

        assertEquals(240, fwSchema.getFixedWidthSchemaLines().get(0).getMinLength());
        assertEquals("First name", fwSchema.getFixedWidthSchemaLines().get(0).getSchemaCells().get(0).getName());
        assertEquals("Last name", fwSchema.getFixedWidthSchemaLines().get(0).getSchemaCells().get(1).getName());

        assertEquals(5, fwSchema.getFixedWidthSchemaLines().get(0).getSchemaCells().get(0).getLength());
        assertEquals(8, fwSchema.getFixedWidthSchemaLines().get(0).getSchemaCells().get(1).getLength());

        assertEquals(CellType.INTEGER, fwSchema.getFixedWidthSchemaLines().get(0).getSchemaCells().get(2)
                .getCellFormat().getCellType());

        SchemaLine schemaLine = fwSchema.getSchemaLine("Person");
        Assert.assertNotNull(schemaLine);
        FixedWidthSchemaCell schemaCell = ((FixedWidthSchemaLine)schemaLine).getSchemaCells().get(2);
        assertEquals( FixedWidthSchemaCell.Alignment.RIGHT, schemaCell.getAlignment() );
        assertEquals( "00000000", schemaCell.getCellFormat().getPattern() );
    }

    @Test
    public final void testBuild_Csv() throws SchemaException {

        String sXmlSchema = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<schema  xmlns='http://jsapar.tigris.org/JSaParSchema/1.0' >" + "<csvschema><line occurs='4'>"
                + "<cell name='First name'/>" + "<cell name='Last name'/>" + "</line></csvschema></schema>";

        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        java.io.Reader reader = new java.io.StringReader(sXmlSchema);
        Schema schema = builder.build(reader);
        CsvSchema csvSchema = (CsvSchema) schema;

        assertEquals("First name", csvSchema.getCsvSchemaLines().get(0).getSchemaCells().get(0).getName());
        assertEquals("Last name", csvSchema.getCsvSchemaLines().get(0).getSchemaCells().get(1).getName());
    }

    @Test
    public final void testBuild_CsvControlCell() throws SchemaException {

        String sXmlSchema = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<schema  xmlns='http://jsapar.tigris.org/JSaParSchema/1.0' >"
                + "<csvcontrolcellschema writecontrolcell='false'><line occurs='4'>"
                + "<cell name='First name'/>" + "<cell name='Last name'/>"
                + "</line></csvcontrolcellschema></schema>";

        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        java.io.Reader reader = new java.io.StringReader(sXmlSchema);
        Schema schema = builder.build(reader);
        CsvControlCellSchema csvSchema = (CsvControlCellSchema) schema;

        assertEquals("First name", csvSchema.getCsvSchemaLines().get(0).getSchemaCells().get(0).getName());
        assertEquals("Last name", csvSchema.getCsvSchemaLines().get(0).getSchemaCells().get(1).getName());
        assertEquals(false, csvSchema.isWriteControlCell());

    }

    @Test
    public final void testBuild_Csv_firstlineasschema() throws SchemaException {

        String sXmlSchema = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<schema  xmlns='http://jsapar.tigris.org/JSaParSchema/1.0' >"
                + "<csvschema><line occurs='4' firstlineasschema='true' >" + "</line></csvschema></schema>";

        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        java.io.Reader reader = new java.io.StringReader(sXmlSchema);
        Schema schema = builder.build(reader);
        CsvSchema csvSchema = (CsvSchema) schema;

        assertEquals(true, csvSchema.getCsvSchemaLines().get(0).isFirstLineAsSchema());

    }

    @Test
    public final void testBuild_Csv_ignorereademptylines() throws SchemaException {

        String sXmlSchema = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<schema  xmlns='http://jsapar.tigris.org/JSaParSchema/1.0' >"
                + "<csvschema><line occurs='4' ignorereademptylines='false' >" + "</line></csvschema></schema>";

        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        java.io.Reader reader = new java.io.StringReader(sXmlSchema);
        Schema schema = builder.build(reader);
        CsvSchema csvSchema = (CsvSchema) schema;

        assertEquals(false, csvSchema.getCsvSchemaLines().get(0).isIgnoreReadEmptyLines());

    }

    @Test(expected = SchemaException.class)
    public final void testBuild_Csv_firstlineasschema_error() throws SchemaException {

        // "yes" is not a valid boolean value.
        String sXmlSchema = "<?xml version='1.0' encoding='UTF-8'?>\n"
                + "<schema  xmlns='http://jsapar.tigris.org/JSaParSchema/1.0' >\n"
                + "<csvschema><line occurs='4' firstlineasschema='yes' >\n" + "</line></csvschema></schema>";

        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        java.io.Reader reader = new java.io.StringReader(sXmlSchema);
        @SuppressWarnings("unused")
        Schema schema = builder.build(reader);
    }

}
