package org.jsapar.schema;

import org.jsapar.model.CellType;
import org.junit.Test;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class SchemaCellFormatTest {

    @Test
    public void testSetFormat_intPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER, "0000", Locale.FRANCE);
        assertEquals("0042", format.getFormat().format(42));
    }

    @Test
    public void testGetFormat_LocalDateTime() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.LOCAL_DATE_TIME, "yyyy-MM-dd HH:mm");
        assertEquals("2017-07-02 13:45", format.getFormat().format(LocalDateTime.of(2017, Month.JULY, 2, 13, 45)));
    }

    @Test
    public void testGetFormat_LocalDate() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.LOCAL_DATE, "yyyy-MM-dd");
        assertEquals("2017-07-02", format.getFormat().format(LocalDate.of(2017, Month.JULY, 2)));
    }

    @Test
    public void testSetFormat_int() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER, "", Locale.FRANCE);
        assertEquals("42", format.getFormat().format(42));
    }

    @Test
    public void testSetFormat_floatPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.FLOAT, "0000.000", Locale.FRANCE);
        assertEquals("0042,300", format.getFormat().format(42.3));
    }

    @Test
    public void testSetFormat_float() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.FLOAT, "", Locale.FRANCE);
        assertEquals("42,3", format.getFormat().format(42.3));
    }

    @Test
    public void testGetCellType() {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER);
        assertEquals(CellType.INTEGER, format.getCellType());
    }


    @Test
    public void testToString() {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER);
        assertEquals("CellType=INTEGER, Format={USIntegerFormat}", format.toString());
    }

    @Test
    public void testGetPattern() throws SchemaException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.INTEGER, "0000", Locale.FRANCE);
        assertEquals("0000", format.getPattern());
    }

    @Test
    public void testSchemaCellFormat() throws SchemaException, ParseException {
        SchemaCellFormat format = new SchemaCellFormat(CellType.BOOLEAN, "yes;");
        assertEquals(Boolean.TRUE, format.getFormat().parse("yes"));
    }
    
}
