package org.jsapar.compose.cell;

import org.jsapar.model.*;
import org.jsapar.parse.cell.DateCellFactory;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;
import org.jsapar.schema.StringSchemaCell;
import org.jsapar.text.Format;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CellFormatTest {


    @Test
    public void testFormat_emptyString_DefaultValue() throws SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withDefaultValue("TheDefault")
                .build();

        Cell<String> cell = new StringCell("Test", "");
        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        assertEquals("TheDefault", format.format(cell));
    }

    @Test
    public void testFormat_empty_DefaultValue() throws SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withDefaultValue("TheDefault")
                .build();

        Cell<String> cell = new EmptyCell<>("Test", CellType.STRING);
        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        assertEquals("TheDefault", format.format(cell));
    }

    @Test
    public void testFormat_empty_no_default()  {
        SchemaCell schemaCell = StringSchemaCell.builder("test").build();

        Cell<String> cell = new EmptyCell<>("Test", CellType.STRING);
        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        assertEquals("", format.format(cell));
    }


    @Test
    public void testFormat_DefaultValue_float() throws SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withLocale("sv","SE")
                .withType(CellType.FLOAT)
                .withPattern("#.00")
                .withDefaultValue("123456,78901")
                .build();

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(new EmptyCell<>("test", CellType.FLOAT));
        assertEquals("123456,78901", value);
    }

    @Test
    public void testFormat_empty_integer() throws SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.INTEGER)
                .build();

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(IntegerCell.emptyOf("test"));
        assertEquals("", value);
    }

    @Test
    public final void testFormat_date() throws ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.DATE)
                .withPattern("yyyy-MM-dd HH:mm")
                .build();

        DateCellFactory cellFactory = new DateCellFactory();
        DateCell cell = (DateCell) cellFactory.makeCell("Name", "2007-10-01 14:13", schemaCell.getFormat());
        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(cell);

        assertEquals("2007-10-01 14:13", value);
    }


    @Test
    public void testFormat() throws SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("test").build();

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(new StringCell("test","A"));
        assertEquals("A", value);
    }

    @Test
    public void testFormat_Regexp() throws SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withFormat(Format.ofStringInstance("A|B"))
                .build();

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        String value = format.format(new StringCell("test","A"));
        assertEquals("A", value);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFormat_Regexp_fail() throws SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.STRING)
                .withPattern("A|B")
                .withLocale("sv", "SE")
                .build();

        CellFormat format = CellFormat.ofSchemaCell(schemaCell);
        format.format(new StringCell("test","C"));
        fail("Should throw exception");
    }

}