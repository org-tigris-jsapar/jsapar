/**
 * 
 */
package org.jsapar.schema;

import java.util.Locale;

import junit.framework.Assert;

import org.jsapar.Cell.CellType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stejon0
 *
 */
public class SchemaCellFormatTest {

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
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#setFormat(org.jsapar.Cell.CellType, java.lang.String, java.util.Locale)}.
     * @throws SchemaException 
     */
    @Test
    public void testSetFormat_intPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.INTEGER, "0000", Locale.FRANCE);
        Assert.assertEquals("0042", format.getFormat().format(42));
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#setFormat(org.jsapar.Cell.CellType, java.lang.String, java.util.Locale)}.
     * @throws SchemaException 
     */
    @Test
    public void testSetFormat_int() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.INTEGER, "", Locale.FRANCE);
        Assert.assertEquals("42", format.getFormat().format(42));
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#setFormat(org.jsapar.Cell.CellType, java.lang.String, java.util.Locale)}.
     * @throws SchemaException 
     */
    @Test
    public void testSetFormat_floatPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.FLOAT, "0000.000", Locale.FRANCE);
        Assert.assertEquals("0042,300", format.getFormat().format(42.3));
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#setFormat(org.jsapar.Cell.CellType, java.lang.String, java.util.Locale)}.
     * @throws SchemaException 
     */
    @Test
    public void testSetFormat_float() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.FLOAT, "", Locale.FRANCE);
        Assert.assertEquals("42,3", format.getFormat().format(42.3));
    }
    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#getCellType()}.
     */
    @Test
    public void testGetCellType() {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER);
        Assert.assertEquals(CellType.INTEGER, format.getCellType());
    }


    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#toString()}.
     */
    @Test
    public void testToString() {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER);
        Assert.assertEquals("CellType=INTEGER", format.toString());
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#getPattern()}.
     * @throws SchemaException 
     */
    @Test
    public void testGetPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.INTEGER, "0000", Locale.FRANCE);
        Assert.assertEquals("0000", format.getPattern());
    }

}
