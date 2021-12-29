package org.jsapar.parse.fixed;

import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class FixedWidthCellParserTest {

    private final int maxCacheSize=0;

    private ReadBuffer makeReadBuffer(String toParse){
        return new ReadBuffer("", new StringReader(toParse), 40, 40);
    }
    
    @Test
    public final void testBuild() throws IOException, JSaParException {
        String toParse = "   Jonas   ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.CENTER)
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        Cell<?>  cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());

        assertEquals("Jonas", cell.getStringValue());
    }

    @Test
    public final void testBuild_dont_trim() throws IOException, JSaParException {
        String toParse = "   Jonas   ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withPadCharacter('!')
                .withTrimLeadingSpaces(false)
                .withAlignment(FixedWidthSchemaCell.Alignment.CENTER)
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        Cell<?>  cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());

        assertEquals("   Jonas   ", cell.getStringValue());
    }

    @Test
    public final void testBuildEmptyMandatory() throws IOException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withMandatory(true)
                .build();

        try {
            FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
            cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
            fail("Should throw exception");

        } catch (JSaParException ex) {
            System.out.println(ex);
        }
    }

    @Test
    public final void testBuildEmptyOptional() throws IOException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .build();

        Cell<?>  cell;
        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        assertTrue(cell.isEmpty());
        assertEquals("", cell.getValue());
    }

    @Test
    public final void testBuildEmptyOptionalInteger() throws IOException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("ShoeSize", 11)
                .withType(CellType.INTEGER)
                .build();

        Cell<?>  cell;
        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        assertTrue(cell.isEmpty());
        assertEquals("", cell.getValue());
    }

    @Test
    public final void testBuildEmptyOptionalInteger_padZero() throws IOException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("ShoeSize", 11)
                .withType(CellType.INTEGER)
                .withPadCharacter('0')
                .withDefaultValue("42")
                .build();

        Cell<?>  cell;
        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        assertEquals(42L, cell.getValue());
    }

    /**
     * A cell should not be considerede empty if blanks are not removed by trimming.
     */
    @Test
    public final void testBuildEmptyMandatoryNoTrim() throws Exception {
        String toParse = "           ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withTrimLeadingSpaces(false)
                .withPadCharacter('!')
                .withMandatory(true)
                .build();

        Cell<?>  cell;
        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        assertEquals("           ", cell.getValue());
    }

    @Test
    public final void testBuild_empty() throws IOException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11).build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        Cell<?>  cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());

        assertEquals("", cell.getValue());
        assertEquals("", cell.getStringValue());
    }

    @Test
    public final void testBuild_date() throws IOException {
        String toParse = "2007-04-10 16:15";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Date", 16)
                .withType(CellType.DATE)
                .withPattern("yyyy-MM-dd HH:mm")
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        DateCell cell = (DateCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        java.util.Date date = cell.getValue();
        Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(2007, calendar.get(Calendar.YEAR));
        assertEquals(3, calendar.get(Calendar.MONTH));
        assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(16, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, calendar.get(Calendar.MINUTE));
    }

    @Test
    public final void testParse_integer_pad_zero() throws IOException {
        String toParse = "000000123";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Integer", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.RIGHT)
                .withPadCharacter('0')
                .withType(CellType.INTEGER)
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        IntegerCell cell = (IntegerCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        int value = cell.getValue().intValue();

        assertEquals(123, value);
    }

    @Test
    public final void testParse_integer_pad_zero_zero() throws IOException {
        String toParse = "000000000";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Integer", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.RIGHT)
                .withPadCharacter('0')
                .withType(CellType.INTEGER)
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        IntegerCell cell = (IntegerCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        int value = cell.getValue().intValue();

        assertEquals(0, value);
    }


    @Test
    public final void testBuild_decimal_sv() throws IOException {
        String toParse = "-123 456,78  ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Decimal", 11)
                .withType(CellType.DECIMAL)
                        .withPattern("#,###.#")
                        .withLocale(new Locale("sv", "SE"))
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        BigDecimalCell cell = (BigDecimalCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        BigDecimal value = cell.getBigDecimalValue();

        assertEquals(new BigDecimal("-123456.78"), value);
    }


    @Test
    public final void testBuild_decimal_uk() throws IOException, JSaParException {
        String toParse = "-123,456.78  ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Decimal", 11)
                .withType(CellType.DECIMAL)
                .withLocale( Locale.UK)
                .withPattern("#,###.#")
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        BigDecimalCell cell = (BigDecimalCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        BigDecimal value = cell.getBigDecimalValue();

        assertEquals(new BigDecimal("-123456.78"), value);
    }

    @Test
    public final void testBuild_int() throws IOException, JSaParException {
        String toParse = "123456";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Integer", 6)
                .withType(CellType.INTEGER).build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        IntegerCell cell = (IntegerCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        int value = cell.getValue().intValue();

        assertEquals(123456, value);
    }

    @Test
    public final void testBuild_float() throws IOException, JSaParException {
        String toParse = "1123.234";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Float", 6)
                .withType(CellType.FLOAT)
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        FloatCell cell = (FloatCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        double value = cell.getValue().doubleValue();

        assertEquals(1123.2, value, 0.001);
    }

    @Test
    public final void testBuild_floatExp() throws IOException, JSaParException {
        String toParse = "1.234E6 ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Float", 8)
                .withType(CellType.FLOAT)
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        FloatCell cell = (FloatCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        double value = cell.getValue().doubleValue();

        assertEquals(1.234e6, value, 0.001);
    }

    @Test
    public final void testBuild_floatExp_pattern() throws IOException, JSaParException {
        String toParse = "1.234E6 ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Float", 8)
                .withType(CellType.FLOAT)
                .withPattern("#.###E0")
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        FloatCell cell = (FloatCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        double value = cell.getValue().doubleValue();

        assertEquals(1.234e6, value, 0.001);
    }

    @Test
    public final void testBuild_floatExp_SE() throws IOException, JSaParException {
        String toParse = "1,234E6 ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Float", 8)
                .withType(CellType.FLOAT)
                .withLocale(new Locale("sv", "SE"))
                .withPattern("#.###E0")
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        FloatCell cell = (FloatCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        double value = cell.getValue().doubleValue();

        assertEquals(1.234e6, value, 0.001);
    }

    @Test
    public final void testBuild_boolean() throws IOException, JSaParException {
        String toParse = "true ";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("True", 5)
                .withType(CellType.BOOLEAN)
                .build();

        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        BooleanCell cell = (BooleanCell) cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        boolean value = cell.getValue();

        assertTrue(value);
    }

    @Test
    public final void testBuildZeroLength() throws IOException {
        String toParse = "Next";
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("DontRead", 0).build();

        Cell<?> cell;
        FixedWidthCellParser cellParser = new FixedWidthCellParser(schemaCell, maxCacheSize);
        cell = cellParser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer());
        Assert.assertNotNull(cell);
        Assert.assertTrue(cell.isEmpty());
        Assert.assertEquals("", cell.getStringValue());
    }
}