package org.jsapar.compose;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.EmptyCell;
import org.jsapar.model.StringCell;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaCellFormat;
import org.jsapar.schema.SchemaException;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CellComposerTest {

    /**
     * To be able to have a specific SchemaCell to test.
     *
     * @author stejon0
     *
     */
    private class TestSchemaCell extends SchemaCell {


        public TestSchemaCell(String name) {
            super(name);
        }

    }

    
    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     */
    @Test
    public void testFormat_emptyString_DefaultValue() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = new StringCell("Test", "");
        CellComposer composer = new CellComposer();
        assertEquals("TheDefault", composer.format(cell, schemaCell));
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     */
    @Test
    public void testFormat_empty_DefaultValue() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = new EmptyCell("Test", CellType.STRING);
        CellComposer composer = new CellComposer();
        assertEquals("TheDefault", composer.format(cell, schemaCell));
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     */
    @Test
    public void testFormat_null_DefaultValue() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        CellComposer composer = new CellComposer();
        assertEquals("TheDefault", composer.format(null, schemaCell));
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     */
    @Test
    public void testFormat_empty_no_default()  {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = new EmptyCell("Test", CellType.STRING);
        CellComposer composer = new CellComposer();
        assertEquals("", composer.format(cell, schemaCell));
    }


    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     */
    @Test
    public void testFormat_null_no_default()  {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        CellComposer composer = new CellComposer();
        assertEquals("", composer.format(null, schemaCell));
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     * @throws SchemaException
     */
    @Test
    public void testFormat_DefaultValue_float() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setDefaultValue("123456,78901");

        CellComposer composer = new CellComposer();
        String value = composer.format(new EmptyCell("test", CellType.FLOAT), schemaCell);
        assertEquals("123456,78901", value);
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     * @throws SchemaException
     */
    @Test
    public void testFormat_empty_integer() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));

        CellComposer composer = new CellComposer();
        String value = composer.format(new EmptyCell("test", CellType.INTEGER), schemaCell);
        assertEquals("", value);
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     * @throws SchemaException
     */
    @Test
    public void testFormat() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        CellComposer composer = new CellComposer();
        String value = composer.format(new StringCell("test","A"), schemaCell);
        assertEquals("A", value);
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     * @throws SchemaException
     */
    @Test
    public void testFormat_Regexp() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "A|B", new Locale("sv","SE")));

        CellComposer composer = new CellComposer();
        String value = composer.format(new StringCell("test","A"), schemaCell);
        assertEquals("A", value);
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     *
     * 
     * @throws SchemaException
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFormat_Regexp_fail() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "A|B", new Locale("sv","SE")));

        CellComposer composer = new CellComposer();
        composer.format(new StringCell("test","C"), schemaCell);
        fail("Should throw exception");
    }

}