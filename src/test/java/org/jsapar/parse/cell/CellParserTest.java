package org.jsapar.parse.cell;

import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.parse.CollectingConsumer;
import org.jsapar.schema.MatchingCellValueCondition;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;
import org.jsapar.schema.StringSchemaCell;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.*;
import java.util.Locale;

import static org.junit.Assert.*;

public class CellParserTest {
    StringSchemaCell schemaCell;

    @Before
    public void before() {
        schemaCell = StringSchemaCell.builder("test").build();
    }
    /**
     * To be able to have a specific SchemaCell to test.
     *
     */

    @Test
    public void testMakeCell_String() throws java.text.ParseException {
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("the value");
        assertEquals("the value", cell.getStringValue());
    }

    @Test
    public void testMakeCell_DefaultString() throws java.text.ParseException {
        schemaCell.setDefaultValue("TheDefault");
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("");
        assertEquals("TheDefault", cell.getStringValue());
    }

    @Test
    public void testMakeCell_missing_no_default() throws java.text.ParseException {
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("");
        assertEquals("", cell.getStringValue());
    }

    @Test
    public void testMakeCell_DefaultValue() throws java.text.ParseException, SchemaException {
        schemaCell.setDefaultValue("TheDefault");
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("");
        assertEquals("TheDefault", cell.getStringValue());
    }

    @Test
    public void testMakeCell_DefaultValue_float() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test").withType(CellType.FLOAT).withPattern("#.00").withLocale("sv","SE").build();
        schemaCell.setDefaultValue("123456,78901");
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("");
        assertEquals(123456.78901, ((FloatCell)cell).getValue().doubleValue(), 0.0001);
    }

    @Test
    public void testMakeCell_empty_pattern() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test").withType(CellType.FLOAT).withPattern("#.00").withLocale("sv","SE").build();
        schemaCell.setEmptyCondition(new MatchingCellValueCondition("NULL"));
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> nonEmptyCell = cellParser.makeCell("1,25");
        assertEquals(1.25, ((FloatCell)nonEmptyCell).getValue().doubleValue(), 0.0001);

        Cell<?> emptyCell = cellParser.makeCell("NULL");
        assertTrue(emptyCell instanceof EmptyCell);

    }

    @Test
    public void testMakeCell_empty_pattern_default() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test").withType(CellType.FLOAT).withPattern("#.00").withLocale("sv","SE").build();
        schemaCell.setEmptyPattern("NULL");
        schemaCell.setDefaultValue("123456,78901");
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("NULL");
        assertEquals(123456.78901, ((FloatCell)cell).getValue().doubleValue(), 0.0001);
    }


    @Test
    public void testMakeCell_RegExp() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withPattern("[A-Z]{3}[0-9]{0,3}de")
                .build();
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("ABC123de");
        assertEquals("ABC123de", cell.getStringValue());
    }

    @Test(expected=java.text.ParseException.class)
    public void testMakeCell_RegExp_fail() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withPattern("[A-Z]{3}[0-9]{0,3}de")
                .build();
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        cellParser.makeCell("AB1C123de");
        fail("Should throw ParseException for invalid RegExp validation.");
    }

    @Test
    public void testMakeCell_CellTypeStringStringFormat() throws SchemaException,
            java.text.ParseException {
        Cell<?> cell = CellParser.makeCell(CellType.STRING, "test", "the value", Locale.US);
        assertEquals("the value", cell.getStringValue());
    }


    @Test(expected = ParseException.class)
    public void testMakeCell_UnfinishedInteger() throws ParseException, SchemaException {
        CellParser.makeCell(CellType.INTEGER, "number", "123A45", Locale.getDefault());
        fail("Method should throw exception.");
    }


    @Test
    public void testMakeCell_Integer() throws java.text.ParseException {
        Cell<?> cell;
        cell = CellParser.makeCell(CellType.INTEGER, "number", "12345", Locale.getDefault());
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(12345, ((IntegerCell)cell).getValue().intValue());
    }

    @Test
    public void testMakeCell_Integer_DefaultValue() throws java.text.ParseException, SchemaException {
        SchemaCell schemaCell = StringSchemaCell.builder("A number")
                .withType(CellType.INTEGER)
                .build();
        schemaCell.setDefaultValue("42");
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);
        Cell<?> cell;
        cell = cellParser.makeCell("");
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(42, ((IntegerCell)cell).getValue().intValue());
        assertEquals("A number", cell.getName());
    }

    @Test(expected = ParseException.class)
    public void testMakeCell_UnfinishedFloat() throws ParseException {
        Locale locale = Locale.UK;
        CellParser.makeCell(CellType.FLOAT, "number", "12.3A45", locale);
        fail("Method should throw exception.");
    }

    @Test
    public void testMakeCell_Float() throws SchemaException, java.text.ParseException {
        Cell<?> cell;
        Locale locale = Locale.UK;
        cell = CellParser.makeCell(CellType.FLOAT, "number", "12.345", locale);
        assertEquals(12.345, cell.getValue());
    }

    @Test
    public void testMakeCell_Decimal_spaces() throws SchemaException, java.text.ParseException {
        Cell<?> cell;
        Locale locale = new Locale("sv", "SE");
        cell = CellParser.makeCell(CellType.DECIMAL, "number", "12 345,66", locale);
        assertEquals(new BigDecimal("12345.66"), cell.getValue());
    }

    @Test
    public void testMakeCell_Float_spaces() throws SchemaException, java.text.ParseException {
        Cell<?> cell;
        Locale locale = new Locale("sv", "SE");
        cell = CellParser.makeCell(CellType.FLOAT, "number", "12 345,66", locale);
        assertEquals(12345.66D, cell.getValue());
    }

    @Test
    public void testMakeCell_Int_spaces() throws SchemaException, java.text.ParseException {
        Locale locale = new Locale("sv", "SE");
        assertEquals(12345L, CellParser.makeCell(CellType.INTEGER, "number", "12 345", locale).getValue());
        assertEquals(12345L, CellParser.makeCell(CellType.INTEGER, "number", "12\u00A0345", locale).getValue());
    }
    @Test
    public void testMakeCell_LocalTime() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.LOCAL_TIME)
                .withPattern("HH:mm")
                .withLocale("sv","SE")
                .withDefaultValue("00:00")
                .build();
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("15:45");
        assertEquals(LocalTime.of(15, 45), ((LocalTimeCell)cell).getValue());

        cell = cellParser.makeCell("");
        assertEquals(LocalTime.of(0, 0), ((LocalTimeCell)cell).getValue());
    }


    @Test
    public void testMakeCell_LocalDateTime() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.LOCAL_DATE_TIME)
                .withPattern("yyyy-MM-dd HH:mm")
                .withLocale("sv","SE")
                .withDefaultValue("2000-01-01 00:00")
                .build();
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("2017-03-27 15:45");
        assertEquals(LocalDateTime.of(2017, Month.MARCH, 27, 15, 45), ((LocalDateTimeCell)cell).getValue());

        cell = cellParser.makeCell("");
        assertEquals(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), ((LocalDateTimeCell)cell).getValue());
    }

    @Test
    public void testMakeCell_LocalDate() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.LOCAL_DATE)
                .withPattern("yyyy-MM-dd")
                .withLocale("sv","SE")
                .withDefaultValue("2000-01-01")
                .build();
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("2017-03-27");
        assertEquals(LocalDate.of(2017, Month.MARCH, 27), ((LocalDateCell)cell).getValue());

        cell = cellParser.makeCell("");
        assertEquals(LocalDate.of(2000, Month.JANUARY, 1), ((LocalDateCell)cell).getValue());
    }

    @Test
    public void testMakeCell_ZonedDateTime() throws SchemaException, java.text.ParseException {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.ZONED_DATE_TIME)
                .withPattern("yyyy-MM-dd HH:mmX")
                .withLocale("sv","SE")
                .withDefaultValue("2000-01-01 00:00+00")
                .build();
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);

        Cell<?> cell = cellParser.makeCell("2017-03-27 15:45+02");
        assertEquals(ZonedDateTime.of(2017, 3, 27, 15, 45, 0, 0, ZoneId.of("+02:00")), ((ZonedDateTimeCell)cell).getValue());

        cell = cellParser.makeCell("");
        assertEquals(ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("+00:00")), ((ZonedDateTimeCell)cell).getValue());
    }

    /**
     *
     */
    @Test
    public void testMakeCell_Integer_RangeValid() throws java.text.ParseException {

        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withType(CellType.INTEGER)
                .build();
        schemaCell.setMinValue(new IntegerCell("test",0));
        schemaCell.setMaxValue(new IntegerCell("test",54321));

        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);
        Cell<?> cell = cellParser.makeCell("12345");
        assertEquals(IntegerCell.class, cell.getClass());
        assertEquals(12345, ((IntegerCell)cell).getValue().intValue());

    }

    /**
     *
     *
     */
    @Test
    public void testParse_Integer_MinRangeNotValid() {

        SchemaCell schemaCell = StringSchemaCell.builder("test").withType(CellType.INTEGER).build();
        schemaCell.setMinValue(new IntegerCell("test",54321));
        schemaCell.setMaxValue(new IntegerCell("test",54322));
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);
        CollectingConsumer<JSaParException> errorListener = new CollectingConsumer<>();
        cellParser.parse("12345", errorListener);
        assertEquals(1, errorListener.getCollected().size());
        assertEquals("Cell='test' Value='12345' Expected: CellType=INTEGER, Format={USIntegerFormat} - The value is below minimum range limit (54321).", errorListener.getCollected().get(0).getMessage());
    }

    /**
     *
     *
     */
    @Test
    public void testMakeCell_Integer_MaxRangeNotValid() {

        SchemaCell schemaCell = StringSchemaCell.builder("test").withType(CellType.INTEGER).withMinValue("0").withMaxValue("100").build();
        CellParser<?> cellParser = new CellParser<>(schemaCell, 0);
        CollectingConsumer<JSaParException> errorListener = new CollectingConsumer<>();
        cellParser.parse("12345", errorListener);
        assertEquals(1, errorListener.getCollected().size());
        assertEquals("Cell='test' Value='12345' Expected: CellType=INTEGER, Format={USIntegerFormat} - The value is above maximum range limit (100).", errorListener.getCollected().get(0).getMessage());
    }



}