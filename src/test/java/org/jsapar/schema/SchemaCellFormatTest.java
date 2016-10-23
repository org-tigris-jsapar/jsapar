/**
 * 
 */
package org.jsapar.schema;

import org.jsapar.model.CellType;
import org.jsapar.text.BooleanFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testSetFormat_intPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.INTEGER, "0000", Locale.FRANCE);
        assertEquals("0042", format.getFormat().format(42));
    }

    @Test
    public void testSetFormat_int() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.INTEGER, "", Locale.FRANCE);
        assertEquals("42", format.getFormat().format(42));
    }

    @Test
    public void testSetFormat_floatPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.FLOAT, "0000.000", Locale.FRANCE);
        assertEquals("0042,300", format.getFormat().format(42.3));
    }

    @Test
    public void testSetFormat_float() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.FLOAT, "", Locale.FRANCE);
        assertEquals("42,3", format.getFormat().format(42.3));
    }
    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#getCellType()}.
     */
    @Test
    public void testGetCellType() {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER);
        assertEquals(CellType.INTEGER, format.getCellType());
    }


    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#toString()}.
     */
    @Test
    public void testToString() {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER);
        assertEquals("CellType=INTEGER", format.toString());
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#getPattern()}.
     * @throws SchemaException 
     */
    @Test
    public void testGetPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat();
        format.setFormat(CellType.INTEGER, "0000", Locale.FRANCE);
        assertEquals("0000", format.getPattern());
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCellFormat#getPattern()}.
     * @throws SchemaException 
     */
    @Test
    public void testSchemaCellFormat() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.BOOLEAN, "yes;");
        assertEquals(BooleanFormat.class, format.getFormat().getClass());
    }
    
}
