package org.jsapar.parse;

import org.jsapar.model.*;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaCellFormat;
import org.jsapar.schema.SchemaException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class CellParserTest {
    CellParser cellParser = new CellParser();

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

    @Test
    public void testMakeCell_String() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = cellParser.makeCell(schemaCell,"the value");
        assertEquals("the value", cell.getStringValue());
    }

    @Test
    public void testMakeCell_DefaultString() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultCell(new StringCell("test","TheDefault"));

        Cell cell = cellParser.makeCell(schemaCell,"");
        assertEquals("TheDefault", cell.getStringValue());
    }

    @Test
    public void testMakeCell_missing_no_default() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = cellParser.makeCell(schemaCell,"");
        assertEquals("", cell.getStringValue());
    }

    @Test
    public void testMakeCell_DefaultValue() throws java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = cellParser.makeCell(schemaCell,"");
        assertEquals("TheDefault", cell.getStringValue());
    }

    @Test
    public void testMakeCell_DefaultValue_float() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setDefaultValue("123456,78901");

        Cell cell = cellParser.makeCell(schemaCell,"");
        assertEquals(123456.78901, ((FloatCell)cell).getNumberValue().doubleValue(), 0.0001);
    }

    @Test
    public void testMakeCell_empty_pattern() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setEmptyPattern("NULL");

        Cell nonEmptyCell = cellParser.makeCell(schemaCell,"1,25");
        assertEquals(1.25, ((FloatCell)nonEmptyCell).getNumberValue().doubleValue(), 0.0001);

        Cell emptyCell = cellParser.makeCell(schemaCell,"NULL");
        assertTrue(emptyCell instanceof EmptyCell);

    }

    @Test
    public void testMakeCell_empty_pattern_default() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.00", new Locale("sv","SE")));
        schemaCell.setEmptyPattern("NULL");
        schemaCell.setDefaultValue("123456,78901");

        Cell cell = cellParser.makeCell(schemaCell,"NULL");
        assertEquals(123456.78901, ((FloatCell)cell).getNumberValue().doubleValue(), 0.0001);
    }


    @Test
    public void testMakeCell_RegExp() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "[A-Z]{3}[0-9]{0,3}de"));

        Cell cell = cellParser.makeCell(schemaCell,"ABC123de");
        assertEquals("ABC123de", cell.getStringValue());
    }

    @Test(expected=java.text.ParseException.class)
    public void testMakeCell_RegExp_fail() throws SchemaException, java.text.ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING, "[A-Z]{3}[0-9]{0,3}de"));

        @SuppressWarnings("unused")
        Cell cell;
        cell = cellParser.makeCell(schemaCell,"AB1C123de");
        fail("Should throw ParseException for invalid RegExp validation.");
    }

    @Test
    public void testMakeCell_CellTypeStringStringFormat() throws SchemaException,
            java.text.ParseException {
        Cell cell = CellParser.makeCell(CellType.STRING, "test", "the value", Locale.getDefault());
        assertEquals("the value", cell.getStringValue());
    }


    @Test
    public void testMakeCell_UnfinishedInteger() throws SchemaException {
        @SuppressWarnings("unused")
        Cell cell;
        try {
            cell = CellParser.makeCell(CellType.INTEGER, "number", "123A45", Locale.getDefault());
        } catch (java.text.ParseException e) {
            // e.printStackTrace();
            return;
        }
        fail("Method should throw exception.");
    }


    @Test
    public void testMakeCell_Integer() throws SchemaException, java.text.ParseException {
        Cell cell;
        cell = CellParser.makeCell(CellType.INTEGER, "number", "12345", Locale.getDefault());
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(12345, ((IntegerCell)cell).getNumberValue().intValue());
    }

    @Test
    public void testMakeCell_Integer_DefaultValue() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = new TestSchemaCell("A number");
        schemaCell.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
        schemaCell.setDefaultValue("42");
        Cell cell;
        cell = cellParser.makeCell(schemaCell,"");
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
            cell = cellParser.makeCell(CellType.FLOAT, "number", "12.3A45", locale);
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
        cell = cellParser.makeCell(CellType.FLOAT, "number", "12.345", locale);
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

        Cell cell = cellParser.makeCell(schemaCell,"12345");
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
        cellParser.makeCell(schemaCell,"12345");
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
        cellParser.makeCell(schemaCell,"12345");
    }


}