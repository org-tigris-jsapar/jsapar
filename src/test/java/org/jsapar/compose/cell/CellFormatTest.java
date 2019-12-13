package org.jsapar.compose.cell;

import org.jsapar.model.*;
import org.jsapar.parse.cell.DateCellFactory;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;
import org.junit.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CellFormatTest {

    /**
     * To be able to have a specific SchemaCell to test.
     *
     */
    private class TestSchemaCell extends SchemaCell {


        TestSchemaCell(String name) {
            super(name);
        }

        TestSchemaCell(String name, CellType type, String pattern, Locale locale) {
            super(name, type, pattern, locale);
        }
    }

    
    @Test
    public void testFormat_emptyString_DefaultValue() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = new StringCell("Test", "");
        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        assertEquals("TheDefault", format.format(cell));
    }

    @Test
    public void testFormat_empty_DefaultValue() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setDefaultValue("TheDefault");

        Cell cell = new EmptyCell("Test", CellType.STRING);
        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        assertEquals("TheDefault", format.format(cell));
    }

    @Test
    public void testFormat_empty_no_default()  {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        Cell cell = new EmptyCell("Test", CellType.STRING);
        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        assertEquals("", format.format(cell));
    }


    @Test
    public void testFormat_DefaultValue_float() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setLocale( new Locale("sv","SE"));
        schemaCell.setCellFormat(CellType.FLOAT, "#.00");
        schemaCell.setDefaultValue("123456,78901");

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(new EmptyCell("test", CellType.FLOAT));
        assertEquals("123456,78901", value);
    }

    @Test
    public void testFormat_empty_integer() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(CellType.INTEGER);

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(new EmptyCell("test", CellType.INTEGER));
        assertEquals("", value);
    }

    @Test
    public final void testFormat_date() throws ParseException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setCellFormat(CellType.DATE, "yyyy-MM-dd HH:mm");
        DateCellFactory cellFactory = new DateCellFactory();

        DateCell cell = (DateCell) cellFactory.makeCell("Name", "2007-10-01 14:13", schemaCell.getCellFormat().getFormat());

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(cell);


        assertEquals("2007-10-01 14:13", value);
    }


    @Test
    public void testFormat() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test");

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(new StringCell("test","A"));
        assertEquals("A", value);
    }

    @Test
    public void testFormat_Regexp() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test", CellType.STRING, "A|B", new Locale("sv","SE"));

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(new StringCell("test","A"));
        assertEquals("A", value);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFormat_Regexp_fail() throws SchemaException {
        TestSchemaCell schemaCell = new TestSchemaCell("test", CellType.STRING, "A|B", new Locale("sv","SE"));

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        format.format(new StringCell("test","C"));
        fail("Should throw exception");
    }

}