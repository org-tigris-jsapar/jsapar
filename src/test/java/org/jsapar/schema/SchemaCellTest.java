/**
 * 
 */
package org.jsapar.schema;

import org.jsapar.model.*;
import org.jsapar.parse.CellParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author stejon0
 * 
 */
public class SchemaCellTest {

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
     * Test method for {@link org.jsapar.schema.SchemaCell#SchemaCell(java.lang.String)} .
     */
    @Test
    public void testSchemaCellString() {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        assertNotNull(schemaCell);
        assertEquals("test", schemaCell.getName());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     * 
     * 
     */
    @Test
    public void testMakeCell_String() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = schemaCell.makeCell("the value");
        assertEquals("the value", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     * 
     * 
     */
    @Test
    public void testMakeCell_DefaultString() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultCell(new StringCell("test","TheDefault"));

        Cell cell = schemaCell.makeCell("");
        assertEquals("TheDefault", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     * 
     * 
     */
    @Test
    public void testMakeCell_missing_no_default() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = schemaCell.makeCell("");
        assertEquals("", cell.getStringValue());
    }
    
    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     * 
     * 
     */
    @Test
    public void testMakeCell_DefaultValue() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = schemaCell.makeCell("");
        assertEquals("TheDefault", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     * 
     * 
     * @throws SchemaException 
     */
    @Test
    public void testMakeCell_DefaultValue_float() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setDefaultValue("123456,78901");

        Cell cell = schemaCell.makeCell("");
        assertEquals(123456.78901, ((FloatCell)cell).getNumberValue().doubleValue(), 0.0001);
    }
    
    @Test
    public void testMakeCell_empty_pattern() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setEmptyPattern("NULL");

        Cell nonEmptyCell = schemaCell.makeCell("1,25");
        assertEquals(1.25, ((FloatCell)nonEmptyCell).getNumberValue().doubleValue(), 0.0001);

        Cell emptyCell = schemaCell.makeCell("NULL");
        assertTrue(emptyCell instanceof EmptyCell);
        
    }
    
    @Test
    public void testMakeCell_empty_pattern_default() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setEmptyPattern("NULL");
        schemaCell.setDefaultValue("123456,78901");

        Cell cell = schemaCell.makeCell("NULL");
        assertEquals(123456.78901, ((FloatCell)cell).getNumberValue().doubleValue(), 0.0001);
    }
    

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     * 
     * 
     * @throws SchemaException
     */
    @Test
    public void testMakeCell_RegExp() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "[A-Z]{3}[0-9]{0,3}de"));

        Cell cell = schemaCell.makeCell("ABC123de");
        assertEquals("ABC123de", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String)}.
     * 
     * 
     * @throws SchemaException
     */
    @Test(expected=java.text.ParseException.class)
    public void testMakeCell_RegExp_fail() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "[A-Z]{3}[0-9]{0,3}de"));

        @SuppressWarnings("unused")
        Cell cell;
        cell = schemaCell.makeCell("AB1C123de");
        fail("Should throw ParseException for invalid RegExp validation.");
    }

    @Test
    public void testMakeCell_CellTypeStringStringFormat() throws SchemaException,
            java.text.ParseException {
        Cell cell = SchemaCell.makeCell(CellType.STRING, "test", "the value", Locale.getDefault());
        assertEquals("the value", cell.getStringValue());
    }


    @Test
    public void testMakeCell_UnfinishedInteger() throws SchemaException {
        @SuppressWarnings("unused")
        Cell cell;
        try {
            cell = SchemaCell.makeCell(CellType.INTEGER, "number", "123A45", Locale.getDefault());
        } catch (java.text.ParseException e) {
            // e.printStackTrace();
            return;
        }
        fail("Method should throw exception.");
    }


    @Test
    public void testMakeCell_Integer() throws SchemaException, java.text.ParseException {
        Cell cell;
        cell = SchemaCell.makeCell(CellType.INTEGER, "number", "12345", Locale.getDefault());
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(12345, ((IntegerCell)cell).getNumberValue().intValue());
    }

    @Test
    public void testMakeCell_Integer_DefaultValue() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = new TestSchemaCell("A number");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setDefaultValue("42");
        Cell cell;
        cell = schemaCell.makeCell("");
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(42, ((IntegerCell)cell).getNumberValue().intValue());
        assertEquals("A number", cell.getName());
    }

    @Test
    public void testMakeCell_UnfinishedFloat() throws SchemaException {
        @SuppressWarnings("unused")
        Cell cell;
        Locale locale = new Locale("UK_en");
        try {
            cell = SchemaCell.makeCell(CellType.FLOAT, "number", "12.3A45", locale);
        } catch (java.text.ParseException e) {
            // e.printStackTrace();
            return;
        }
        fail("Method should throw exception.");
    }

    @Test
    public void testMakeCell_Float() throws SchemaException, java.text.ParseException {
        Cell cell;
        Locale locale = new Locale("UK_en");
        cell = SchemaCell.makeCell(CellType.FLOAT, "number", "12.345", locale);
        assertEquals(12.345, cell.getValue());
    }

    /**
     * 
     */
    @Test
    public void testMakeCell_Integer_RangeValid() throws java.text.ParseException {

        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setMinValue(new IntegerCell("test",0));
        schemaCell.setMaxValue(new IntegerCell("test",54321));

        Cell cell = schemaCell.makeCell("12345");
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(12345, ((IntegerCell)cell).getNumberValue().intValue());

    }

    /**
     *  
     * 
     */
    @Ignore
    @Test(expected = CellParseException.class)
    public void testMakeCell_Integer_MinRangeNotValid() throws java.text.ParseException {

        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setMinValue(new IntegerCell("test",54321));
        schemaCell.setMaxValue(new IntegerCell("test",54322));
        schemaCell.makeCell("12345");
    }

    /**
     * 
     * 
     */
    @Ignore
    @Test(expected = CellParseException.class)
    public void testMakeCell_Integer_MaxRangeNotValid() throws java.text.ParseException {

        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setMinValue(new IntegerCell("test",0));
        schemaCell.setMaxValue(new IntegerCell("test",100));
        schemaCell.makeCell("12345");
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCell#clone()}.
     * 
     * @throws CloneNotSupportedException
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        TestSchemaCell clone = (TestSchemaCell) schemaCell.clone();
        assertNotNull(clone);
        assertEquals(schemaCell.getName(), clone.getName());
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCell#toString()}.
     */
    @Test
    public void testToString() {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        String s = schemaCell.toString();
        assertNotNull(s);
    }

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

}
