package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

import org.jsapar.BigDecimalCell;
import org.jsapar.BooleanCell;
import org.jsapar.Cell;
import org.jsapar.CellType;
import org.jsapar.DateCell;
import org.jsapar.JSaParException;
import org.jsapar.NumberCell;
import org.jsapar.StringCell;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthSchemaCellTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private class TestParsingEventListener implements ParsingEventListener{

        @Override
        public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            throw new ParseException(event.getCellParseError());
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
        }
        
    }
    
    @Test
    public final void testBuild() throws IOException, JSaParException {
        String toParse = "   Jonas   ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 11);

        Reader reader = new StringReader(toParse);
        Cell cell = schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);

        assertEquals("Jonas", cell.getStringValue());
    }

    @Test
    public final void testBuild_dont_trim() throws IOException, JSaParException {
        String toParse = "   Jonas   ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 11);

        Reader reader = new StringReader(toParse);
        Cell cell = schemaElment.build(reader, false, ' ', new TestParsingEventListener(), 1);

        assertEquals("   Jonas   ", cell.getStringValue());
    }

    @Test
    public final void testBuildEmptyMandatory() throws IOException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaElement = new FixedWidthSchemaCell("First name", 11);
        schemaElement.setMandatory(true);

        Reader reader = new StringReader(toParse);
        @SuppressWarnings("unused")
        Cell cell;
        try {
            cell = schemaElement.build(reader, true, ' ', new TestParsingEventListener(), 1);
            fail("Should throw exception");

        } catch (ParseException ex) {
            System.out.println(ex);
        }
    }

    @Test
    public final void testBuildEmptyOptional() throws IOException, ParseException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 11);

        Reader reader = new StringReader(toParse);
        Cell cell;
        cell = schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        assertEquals(null, cell.getValue());
    }

    @Test
    public final void testBuildEmptyOptionalInteger() throws IOException, ParseException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("ShoeSize", 11);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.INTEGER));

        Reader reader = new StringReader(toParse);
        Cell cell;
        cell = schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        assertEquals(null, cell.getValue());
    }

    /**
     * A cell should not be considerede empty if blanks are not removed by trimming.
     */
    @Test
    public final void testBuildEmptyMandatoryNoTrim() throws IOException, Exception {
        String toParse = "           ";
        FixedWidthSchemaCell schemaElement = new FixedWidthSchemaCell("First name", 11);
        schemaElement.setMandatory(true);

        Reader reader = new StringReader(toParse);
        Cell cell;
        cell = schemaElement.build(reader, false, ' ', new TestParsingEventListener(), 1);
        assertEquals("           ", cell.getValue());
    }

    @Test
    public final void testBuild_empty() throws IOException, JSaParException {
        String toParse = "           ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 11);

        Reader reader = new StringReader(toParse);
        Cell cell = schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);

        assertEquals(null, cell.getValue());
        assertEquals("", cell.getStringValue());
    }

    @Test
    public final void testBuild_date() throws IOException, JSaParException, SchemaException {
        String toParse = "2007-04-10 16:15";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Date", 16);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.DATE, "yyyy-MM-dd HH:mm"));

        Reader reader = new StringReader(toParse);
        DateCell cell = (DateCell) schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        java.util.Date date = cell.getDateValue();
        Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(2007, calendar.get(Calendar.YEAR));
        assertEquals(3, calendar.get(Calendar.MONTH));
        assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(16, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, calendar.get(Calendar.MINUTE));
    }

    @Test
    public final void testBuild_decimal_sv() throws IOException, JSaParException, SchemaException {
        String toParse = "-123 456,78  ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Decimal", 11);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.DECIMAL, "#,###.#", new Locale("sv", "SE")));
        // schemaElment.setCellFormat(new SchemaCellFormat(CellType.DECIMAL));

        Reader reader = new StringReader(toParse);
        BigDecimalCell cell = (BigDecimalCell) schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        BigDecimal value = cell.getBigDecimalValue();

        assertEquals(new BigDecimal("-123456.78"), value);
    }

    @Test
    public final void testBuild_decimal_sv_dont_trim() throws IOException, JSaParException, SchemaException {
        String toParse = "-123 456,78  ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Decimal", 11);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.DECIMAL, "#,###.#", new Locale("sv", "SE")));

        Reader reader = new StringReader(toParse);
        BigDecimalCell cell = (BigDecimalCell) schemaElment.build(reader, false, ' ', new TestParsingEventListener(), 1);
        BigDecimal value = cell.getBigDecimalValue();

        assertEquals(new BigDecimal("-123456.78"), value);
    }

    @Test
    public final void testBuild_decimal_uk() throws IOException, JSaParException, SchemaException {
        String toParse = "-123,456.78  ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Decimal", 11);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.DECIMAL, "#,###.#", new Locale("en", "UK")));
        // schemaElment.setCellFormat(new SchemaCellFormat(CellType.DECIMAL));

        Reader reader = new StringReader(toParse);
        BigDecimalCell cell = (BigDecimalCell) schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        BigDecimal value = cell.getBigDecimalValue();

        assertEquals(new BigDecimal("-123456.78"), value);
    }

    @Test
    public final void testBuild_int() throws IOException, JSaParException, SchemaException {
        String toParse = "123456";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Integer", 6);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.INTEGER));

        Reader reader = new StringReader(toParse);
        NumberCell cell = (NumberCell) schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        int value = cell.getNumberValue().intValue();

        assertEquals(123456, value);
    }

    @Test
    public final void testBuild_float() throws IOException, JSaParException, SchemaException {
        String toParse = "1123,234";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Float", 6);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.FLOAT));

        Reader reader = new StringReader(toParse);
        NumberCell cell = (NumberCell) schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        double value = cell.getNumberValue().doubleValue();

        assertEquals(1123, 234, value);
    }

    @Test
    public final void testBuild_floatExp() throws IOException, JSaParException, SchemaException {
        String toParse = "1,234E6 ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Float", 8);
        schemaElment.setLocale(new Locale("sv", "SE"));
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.FLOAT, "#.###E0", schemaElment.getLocale()));

        Reader reader = new StringReader(toParse);
        NumberCell cell = (NumberCell) schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        double value = cell.getNumberValue().doubleValue();

        assertEquals(1.234e6, value, 0.001);
    }

    @Test
    public final void testBuild_boolean() throws IOException, JSaParException, SchemaException {
        String toParse = "true ";
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("True", 5);
        schemaElment.setCellFormat(new SchemaCellFormat(CellType.BOOLEAN));

        Reader reader = new StringReader(toParse);
        BooleanCell cell = (BooleanCell) schemaElment.build(reader, true, ' ', new TestParsingEventListener(), 1);
        boolean value = cell.getBooleanValue();

        assertEquals(true, value);
    }

    @Test
    public final void testOutput_Center() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 11);
        schemaElment.setAlignment(FixedWidthSchemaCell.Alignment.CENTER);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("Jonas");
        schemaElment.output(cell, writer, ' ');

        assertEquals("   Jonas   ", writer.toString());
    }

    @Test
    public final void testOutput_Center_overflow_even() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 7);
        schemaElment.setAlignment(FixedWidthSchemaCell.Alignment.CENTER);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("000Jonas000");
        schemaElment.output(cell, writer, ' ');

        assertEquals("0Jonas0", writer.toString());
    }

    /**
     * Overflow is an odd number
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public final void testOutput_Center_overflow_odd() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 6);
        schemaElment.setAlignment(FixedWidthSchemaCell.Alignment.CENTER);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("000Jonas000");
        schemaElment.output(cell, writer, ' ');

        assertEquals("0Jonas", writer.toString());
    }
    
    @Test
    public final void testOutput_Left() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 11);
        schemaElment.setAlignment(FixedWidthSchemaCell.Alignment.LEFT);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("Jonas");
        schemaElment.output(cell, writer, ' ');

        assertEquals("Jonas      ", writer.toString());
    }

    @Test
    public final void testOutput_Exact() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 5);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("Jonas");
        schemaElment.output(cell, writer, ' ');

        assertEquals("Jonas", writer.toString());
    }
    
    @Test
    public final void testOutput_Rigth() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 11);
        schemaElment.setAlignment(FixedWidthSchemaCell.Alignment.RIGHT);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("Jonas");
        schemaElment.output(cell, writer, '*');

        assertEquals("******Jonas", writer.toString());
    }

    @Test
    public final void testOutput_Rigth_overflow() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("First name", 6);
        schemaElment.setAlignment(FixedWidthSchemaCell.Alignment.RIGHT);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("0000Jonas");
        schemaElment.output(cell, writer, '*');

        assertEquals("0Jonas", writer.toString());
    }
    
    @Test
    public final void testOutput_Default() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaElment = new FixedWidthSchemaCell("Size", 11);
        schemaElment.setDefaultValue("10");

        Writer writer = new StringWriter();
        schemaElment.output(null, writer, ' ');

        assertEquals("10         ", writer.toString());
    }    
    
    @Test
    public final void testClone() throws CloneNotSupportedException {
        FixedWidthSchemaCell schemaCell = new FixedWidthSchemaCell("First name", 11);
        schemaCell.setAlignment(FixedWidthSchemaCell.Alignment.RIGHT);

        FixedWidthSchemaCell clone = schemaCell.clone();

        assertEquals(schemaCell.getName(), clone.getName());
        assertEquals(schemaCell.getAlignment(), clone.getAlignment());

        // Does not clone strings values yet. Might do that in the future.
        assertTrue(schemaCell.getName() == clone.getName());
    }

    @Test
    public final void testBuildZeroLength() throws IOException, ParseException {
        String toParse = "Next";
        FixedWidthSchemaCell schemaCell = new FixedWidthSchemaCell("DontRead", 0);

        Reader reader = new StringReader(toParse);
        Cell cell;
        cell = schemaCell.build(reader, true, ' ', new TestParsingEventListener(), 1);
        Assert.assertNotNull(cell);
        Assert.assertNull(cell.getValue());
        Assert.assertEquals("", cell.getStringValue());
    }
}
