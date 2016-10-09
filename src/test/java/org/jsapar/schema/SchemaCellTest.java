/**
 * 
 */
package org.jsapar.schema;

import static org.junit.Assert.*;

import java.util.Locale;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.EmptyCell;
import org.jsapar.model.FloatCell;
import org.jsapar.model.IntegerCell;
import org.jsapar.model.StringCell;
import org.jsapar.parse.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testMakeCell_String() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = schemaCell.makeCell("the value", listener, nLineNumber);
        assertEquals("the value", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testMakeCell_DefaultString() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultCell(new StringCell("test","TheDefault"));

        Cell cell = schemaCell.makeCell("", listener, nLineNumber);
        assertEquals("TheDefault", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testMakeCell_missing_no_default() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = schemaCell.makeCell("", listener, nLineNumber);
        assertEquals("", cell.getStringValue());
    }
    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testMakeCell_DefaultValue() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = schemaCell.makeCell("", listener, nLineNumber);
        assertEquals("TheDefault", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException 
     */
    @Test
    public void testMakeCell_DefaultValue_float() throws ParseException, SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setDefaultValue("123456,78901");

        Cell cell = schemaCell.makeCell("", listener, nLineNumber);
        assertEquals(123456.78901, ((FloatCell)cell).getNumberValue().doubleValue(), 0.0001);
    }
    
    @Test
    public void testMakeCell_empty_pattern() throws ParseException, SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setEmptyPattern("NULL");

        Cell nonEmptyCell = schemaCell.makeCell("1,25", listener, nLineNumber);
        assertEquals(1.25, ((FloatCell)nonEmptyCell).getNumberValue().doubleValue(), 0.0001);

        Cell emptyCell = schemaCell.makeCell("NULL", listener, nLineNumber);
        assertTrue(emptyCell instanceof EmptyCell);
        
    }
    
    @Test
    public void testMakeCell_empty_pattern_default() throws ParseException, SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setEmptyPattern("NULL");
        schemaCell.setDefaultValue("123456,78901");

        Cell cell = schemaCell.makeCell("NULL", listener, nLineNumber);
        assertEquals(123456.78901, ((FloatCell)cell).getNumberValue().doubleValue(), 0.0001);
    }
    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testFormat_emptyString_DefaultValue() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = new StringCell("Test", "");
        assertEquals("TheDefault", schemaCell.format(cell));
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testFormat_empty_DefaultValue() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = new EmptyCell("Test", CellType.STRING);
        assertEquals("TheDefault", schemaCell.format(cell));
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testFormat_null_DefaultValue() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        assertEquals("TheDefault", schemaCell.format(null));
    }
    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testFormat_empty_no_default() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = new EmptyCell("Test", CellType.STRING);
        assertEquals("", schemaCell.format(cell));
    }

    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     */
    @Test
    public void testFormat_null_no_default() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        assertEquals("", schemaCell.format(null));
    }
    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException 
     */
    @Test
    public void testFormat_DefaultValue_float() throws ParseException, SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setDefaultValue("123456,78901");

        String value = schemaCell.format(new EmptyCell("test", CellType.FLOAT));
        assertEquals("123456,78901", value);
    }
    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException 
     */
    @Test
    public void testFormat_empty_integer() throws ParseException, SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));

        String value = schemaCell.format(new EmptyCell("test", CellType.INTEGER));
        assertEquals("", value);
    }    

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException 
     */
    @Test
    public void testFormat() throws ParseException, SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        String value = schemaCell.format(new StringCell("test","A"));
        assertEquals("A", value);
    }    

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException 
     */
    @Test
    public void testFormat_Regexp() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "A|B", new Locale("sv","SE")));

        String value = schemaCell.format(new StringCell("test","A"));
        assertEquals("A", value);
    }
    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException 
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFormat_Regexp_fail() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "A|B", new Locale("sv","SE")));

        schemaCell.format(new StringCell("test","C"));
        fail("Should throw exception");
    }    
    
    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException
     */
    @Test
    public void testMakeCell_RegExp() throws ParseException, SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "[A-Z]{3}[0-9]{0,3}de"));

        Cell cell = schemaCell.makeCell("ABC123de", listener, nLineNumber);
        assertEquals("ABC123de", cell.getStringValue());
    }

    /**
     * Test method for {@link SchemaCell#makeCell(String, ErrorEventListener, long)}.
     * 
     * @throws ParseException
     * @throws SchemaException
     */
    @Test(expected=ParseException.class)
    public void testMakeCell_RegExp_fail() throws SchemaException, ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "[A-Z]{3}[0-9]{0,3}de"));

        @SuppressWarnings("unused")
        Cell cell;
        cell = schemaCell.makeCell("AB1C123de", listener, nLineNumber);
        fail("Should throw ParseException for invalid RegExp validation.");
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.SchemaCell#makeCell(Cell.CellType, java.lang.String, java.lang.String, java.text.Format)}
     * .
     * 
     * @throws java.text.ParseException
     * @throws SchemaException
     * @throws ParseException
     */
    @Test
    public void testMakeCell_CellTypeStringStringFormat() throws ParseException, SchemaException,
            java.text.ParseException {
        Cell cell = SchemaCell.makeCell(CellType.STRING, "test", "the value", Locale.getDefault());
        assertEquals("the value", cell.getStringValue());
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.SchemaCell#makeCell(Cell.CellType, java.lang.String, java.lang.String, java.text.Format)}
     * .
     */
    @Test
    public void testMakeCell_UnfinishedInteger() throws SchemaException, ParseException {
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

    /**
     * Test method for
     * {@link org.jsapar.schema.SchemaCell#makeCell(Cell.CellType, java.lang.String, java.lang.String, java.text.Format)}
     * .
     * 
     * @throws java.text.ParseException
     */
    @Test
    public void testMakeCell_Integer() throws SchemaException, ParseException, java.text.ParseException {
        Cell cell;
        cell = SchemaCell.makeCell(CellType.INTEGER, "number", "12345", Locale.getDefault());
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(12345, ((IntegerCell)cell).getNumberValue().intValue());
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.SchemaCell#makeCell(Cell.CellType, java.lang.String, java.lang.String, java.text.Format)}
     * .
     * 
     * @throws java.text.ParseException
     */
    @Test
    public void testMakeCell_Integer_DefaultValue() throws SchemaException, ParseException, java.text.ParseException {
        SchemaCell schemaCell = new TestSchemaCell("A number");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setDefaultValue("42");
        Cell cell;
        cell = schemaCell.makeCell("", listener, nLineNumber);
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(42, ((IntegerCell)cell).getNumberValue().intValue());
        assertEquals("A number", cell.getName());
    }

    /**
     * Test method for
     * {@link org.jsapar.schema.SchemaCell#makeCell(Cell.CellType, java.lang.String, java.lang.String, java.text.Format)}
     * .
     */
    @Test
    public void testMakeCell_UnfinishedFloat() throws SchemaException, ParseException {
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

    /**
     * Test method for
     * {@link org.jsapar.schema.SchemaCell#makeCell(Cell.CellType, java.lang.String, java.lang.String, java.text.Format)}
     * .
     * 
     * @throws java.text.ParseException
     */
    @Test
    public void testMakeCell_Float() throws SchemaException, ParseException, java.text.ParseException {
        Cell cell;
        Locale locale = new Locale("UK_en");
        cell = SchemaCell.makeCell(CellType.FLOAT, "number", "12.345", locale);
        assertEquals(12.345, cell.getValue());
    }

    /**
     * @throws ParseException
     */
    @Test
    public void testMakeCell_Integer_RangeValid() throws ParseException {

        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setMinValue(new IntegerCell("test",0));
        schemaCell.setMaxValue(new IntegerCell("test",54321));

        Cell cell = schemaCell.makeCell("12345", listener, nLineNumber);
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(12345, ((IntegerCell)cell).getNumberValue().intValue());

    }

    /**
     * @throws ParseException 
     * @throws ParseException
     */
    @Test(expected = ParseException.class)
    public void testMakeCell_Integer_MinRangeNotValid() throws ParseException {

        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setMinValue(new IntegerCell("test",54321));
        schemaCell.setMaxValue(new IntegerCell("test",54322));
        schemaCell.makeCell("12345", listener, nLineNumber);
    }

    /**
     * @throws ParseException
     * @throws ParseException
     */
    @Test(expected = ParseException.class)
    public void testMakeCell_Integer_MaxRangeNotValid() throws ParseException {

        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setMinValue(new IntegerCell("test",0));
        schemaCell.setMaxValue(new IntegerCell("test",100));
        schemaCell.makeCell("12345", listener, nLineNumber);
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
