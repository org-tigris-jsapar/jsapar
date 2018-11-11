package org.jsapar.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Date;

import static org.junit.Assert.*;

public class LineUtilsTest {

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void testSetStringCellValue()  {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "ABC");
        assertEquals("ABC", LineUtils.getStringCellValue(line,"aStringValue"));
        LineUtils.setStringCellValue(line,"aStringValue", null);
        assertFalse(line.isCell("aStringValue"));
    }

    @Test
    public void testGetSetStringCellValue()  {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        LineUtils.setStringCellValue(line, "LastName", "Svensson");
        line.addCell(StringCell.emptyOf("empty"));
        assertEquals("Nils", LineUtils.getStringCellValue(line,"FirstName"));
        assertEquals("Svensson", LineUtils.getStringCellValue(line,"LastName"));
        assertEquals("Svensson", LineUtils.getNonEmptyStringCellValue(line,"LastName").orElse(null));
        assertEquals("", LineUtils.getStringCellValue(line,"empty"));
        assertEquals("other", LineUtils.getNonEmptyStringCellValue(line,"empty").orElse("other"));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetStringCellValue_notExists(){
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        LineUtils.getStringCellValue(line,"Nothing");
    }

    private enum Testing{
        FIRST, SECOND
    }

    @Test
    public void testGetSetEnumCellValue() {
        Line line = new Line("TestLine");
        LineUtils.setEnumCellValue(line, "Type", Testing.FIRST);
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line, "Type", Testing.SECOND));
    }
    
    @Test
    public void testGetEnumCellValue_default() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line, "Type", "FIRST");
        LineUtils.setEnumCellValue(line, "EnumSet", Testing.FIRST);
        line.addCell(StringCell.emptyOf("TypeEmpty"));
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line, "Type", Testing.SECOND));
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line, "EnumSet", Testing.SECOND));
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line, "EnumSet", Testing.class).orElse(Testing.SECOND));
        assertEquals(Testing.SECOND, LineUtils.getEnumCellValue(line, "TypeEmpty", Testing.SECOND));
        assertNull( LineUtils.getEnumCellValue(line, "TypeEmpty", (Testing)null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEnumCellValue_wrongValue() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line, "Type", "not value");
        LineUtils.getEnumCellValue(line, "Type", Testing.SECOND);
        fail("Should throw exception");
    }

    @Test
    public void testGetEnumCellValue() {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("Type", "FIRST"));
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line,"Type", Testing.class).orElse(Testing.SECOND));
    }
    
    @Test
    public void testGetSetIntCellValue() {
        Line line = new Line("TestLine");
        line.addCell(new IntegerCell("shoeSize", 42));
        line.addCell(new StringCell("stringInt", "17"));
        line.addCell(new EmptyCell("empty", CellType.INTEGER));
        line.addCell(new FloatCell("pi", 3.141));
        LineUtils.setIntCellValue(line, "anIntValue", 4711);

        assertEquals(42, LineUtils.getNumberCellValue(line,"shoeSize").orElse(-1).intValue());
        assertEquals(42, LineUtils.getIntCellValue(line,"shoeSize", 55));
        assertEquals(42L, LineUtils.getLongCellValue(line,"shoeSize", 55));
        assertEquals(17, LineUtils.getIntCellValue(line,"stringInt", 55));
        assertEquals(17L, LineUtils.getLongCellValue(line,"stringInt", 55));
        assertEquals(Integer.valueOf(42), LineUtils.getIntCellValue(line,"shoeSize").orElse(55));
        assertEquals(Long.valueOf(42), LineUtils.getLongCellValue(line,"shoeSize").orElse(55L));
        assertEquals(4711, LineUtils.getIntCellValue(line,"empty", 4711));
        assertEquals(3, LineUtils.getNumberCellValue(line,"pi").orElse(-1).intValue());
        assertEquals(4711, LineUtils.getNumberCellValue(line,"anIntValue").orElse(-1).intValue());

    }

    @Test(expected=NumberFormatException.class)
    public void testGetIntCellValue_not_parsable() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "seventeen");

        LineUtils.getIntCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }

    @Test(expected=NumberFormatException.class)
    public void testGetNumberCellValue_string() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "17");

        LineUtils.getNumberCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }

    @Test(expected=NumberFormatException.class)
    public void testGetLongCellValue_not_parsable() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "seventeen");

        LineUtils.getLongCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }

    @Test
    public void testGetSetLongCellValue(){
        Line line = new Line("TestLine");
        LineUtils.setLongCellValue(line, "Value", 314159265358979L);
        line.addCell(IntegerCell.emptyOf("empty"));
        assertEquals(314159265358979L, LineUtils.getNumberCellValue(line,"Value").orElse( -1.0).longValue());
        assertEquals(314159265358979L, LineUtils.getLongCellValue(line,"Value",  -1L));
        assertEquals(314159265358979L, LineUtils.getNumberCellValue(line,"Value").orElse( -1.0).longValue(), 17L);
        assertEquals(17L, LineUtils.getLongCellValue(line,"empty", 17L));
    }

    @Test
    public void testGetSetDoubleCellValue() {
        Line line = new Line("TestLine");
        LineUtils.setDoubleCellValue(line, "Value", 3.14159265358979D);
        line.addCell(new EmptyCell("empty", CellType.FLOAT));
        assertEquals(3.14159265358979D, LineUtils.getNumberCellValue(line,"Value").orElse( -1.0).doubleValue(), 0.0000000001);
        assertEquals(3.14159265358979D, LineUtils.getDoubleCellValue(line,"Value", 2.71D), 0.0000000001);
        assertEquals(3.14159265358979D, LineUtils.getDoubleCellValue(line,"Value").orElse(2.71D), 0.0000000001);
        assertEquals(2.718D, LineUtils.getDoubleCellValue(line,"empty", 2.718D), 0.0000000001);
    }

    @Test(expected=NumberFormatException.class)
    public void testGetDoubleCellValue_not_parsable() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "seventeen");

        LineUtils.getDoubleCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }

    @Test(expected=NumberFormatException.class)
    public void testGetDecimalCellValue_not_parsable() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "seventeen");

        LineUtils.getDecimalCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }

    @Test(expected=NumberFormatException.class)
    public void testGetBigIntegerCellValue_not_parsable() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "seventeen");

        LineUtils.getBigIntegerCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }

    @Test
    public void testGetSetDecimalCellValue() {
        Line line = new Line("TestLine");
        LineUtils.setDoubleCellValue(line, "pi", 3.14159265358979D);
        LineUtils.setDecimalCellValue(line, "e", new BigDecimal("2.718"));
        line.addCell(BigDecimalCell.emptyOf("empty"));
        assertEquals(new BigDecimal(3.14159265358979D), LineUtils.getDecimalCellValue(line,"pi").orElse(null));
        assertEquals(new BigDecimal("2.718"), LineUtils.getDecimalCellValue(line,"e").orElse(null));
        assertEquals(new BigDecimal(3.14159265358979D), LineUtils.getDecimalCellValue(line,"pi").orElse(new BigDecimal(2.71D)));
        assertEquals(new BigDecimal("23.14"), LineUtils.getDecimalCellValue(line,"empty").orElse(new BigDecimal("23.14")));
    }

    @Test
    public void testGetSetBigIntegerCellValue() {
        Line line = new Line("TestLine");
        LineUtils.setIntCellValue(line, "random", 4711);
        LineUtils.setBigIntegerCellValue(line, "douglas", BigInteger.valueOf(42L));
        line.addCell(BigDecimalCell.emptyOf("empty"));
        assertEquals(new BigInteger("4711"), LineUtils.getBigIntegerCellValue(line,"random").orElse(null));
        assertEquals(new BigInteger("42"), LineUtils.getBigIntegerCellValue(line,"douglas").orElse(null));
        assertEquals(new BigInteger("42"), LineUtils.getBigIntegerCellValue(line,"douglas").orElse(BigInteger.valueOf(42L)));
        assertEquals(new BigInteger("23"), LineUtils.getBigIntegerCellValue(line,"empty").orElse(new BigInteger("23")));
    }

    @Test
    public void testGetSetDateCellValue() {
        Line line = new Line("TestLine");
        Date date = new Date();
        LineUtils.setDateCellValue(line,"aDateValue", date);
        line.addCell(DateCell.emptyOf("empty"));
        assertEquals(date, LineUtils.getDateCellValue(line,"aDateValue").orElse(null));
        assertSame(date, LineUtils.getDateCellValue(line,"aDateValue").orElseGet(Date::new));
        assertNotSame(date, LineUtils.getDateCellValue(line,"empty").orElseGet(Date::new));
        LineUtils.setDateCellValue(line,"aDateValue", null);
        assertFalse(line.isCell("aDateValue"));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetDateCellValue_notADate() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "2018-04-07");
        LineUtils.getDateCellValue(line, "aStringValue").orElseGet(Date::new);
        fail("Should throw exception");
    }

    @Test
    public void testGetSetLocalDateCellValue() {
        Line line = new Line("TestLine");
        LocalDate date = LocalDate.of(2017, Month.JULY, 14);
        LineUtils.setLocalDateCellValue(line, "aDateValue", date);
        line.addCell(LocalDateCell.emptyOf("empty"));
        assertSame(date, LineUtils.getLocalDateCellValue(line,"aDateValue").orElseThrow(IllegalArgumentException::new));
        assertNotSame(date, LineUtils.getLocalDateCellValue(line,"empty").orElse(LocalDate.of(2017, Month.APRIL, 1)));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetSetLocalDate_notTemporal() {
        Line line = new Line("TestLine");
        LineUtils.setDateCellValue(line,"aDateValue", new Date());
        LineUtils.getLocalDateCellValue(line, "aDateValue");
        fail("Should throw exception");
    }

    @Test
    public void testGetSetLocalTimeCellValue() {
        Line line = new Line("TestLine");
        LocalTime date = LocalTime.of(12,10);
        LineUtils.setLocalTimeCellValue(line, "aDateValue", date);
        line.addCell(LocalTimeCell.emptyOf("empty"));
        assertSame(date, LineUtils.getLocalTimeCellValue(line,"aDateValue").orElseThrow(IllegalArgumentException::new));
        assertNotSame(date, LineUtils.getLocalTimeCellValue(line,"empty").orElse(LocalTime.of(10,14)));
    }

    @Test
    public void testGetSetLocalDateTimeCellValue() {
        Line line = new Line("TestLine");
        LocalDateTime date = LocalDateTime.of(2017, Month.JULY, 14, 10, 20);
        LineUtils.setLocalDateTimeCellValue(line, "aDateValue", date);
        line.addCell(LocalDateTimeCell.emptyOf("empty"));
        assertSame(date, LineUtils.getLocalDateTimeCellValue(line,"aDateValue").orElseThrow(IllegalArgumentException::new));
        assertNotSame(date, LineUtils.getLocalDateTimeCellValue(line,"empty").orElse(LocalDateTime.of(2017, Month.APRIL, 1, 13, 10)));
    }

    @Test
    public void testGetSetZonedDateTimeCellValue() {
        Line line = new Line("TestLine");
        ZonedDateTime date = ZonedDateTime.of(2017, 6, 14, 10, 20, 23, 0, ZoneId.of("UTC"));
        LineUtils.setZonedDateTimeCellValue(line, "aDateValue", date);
        line.addCell(ZonedDateTimeCell.emptyOf("empty"));
        assertSame(date, LineUtils.getZonedDateTimeCellValue(line,"aDateValue").orElseThrow(IllegalArgumentException::new));
        assertNotSame(date, LineUtils.getZonedDateTimeCellValue(line,"empty").orElse(ZonedDateTime.of(2017, 4, 1, 13, 10, 23, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testSetBooleanCellValue() {
        Line line = new Line("TestLine");
        LineUtils.setBooleanCellValue(line,"aTrueValue", true);
        LineUtils.setStringCellValue(line,"aStringTrue", "true");
        LineUtils.setStringCellValue(line,"aStringFalse", "anything but true");
        LineUtils.setBooleanCellValue(line,"aFalseValue", false);
        line.addCell(BooleanCell.emptyOf("empty"));
        assertTrue(LineUtils.getBooleanCellValue(line,"aTrueValue").orElseThrow(AssertionError::new));
        assertTrue(LineUtils.getBooleanCellValue(line,"aTrueValue", false));
        assertTrue(LineUtils.getBooleanCellValue(line,"aStringTrue").orElseThrow(AssertionError::new));
        assertFalse(LineUtils.getBooleanCellValue(line,"aStringFalse", true));
        assertFalse(LineUtils.getBooleanCellValue(line,"empty", false));
        assertFalse(LineUtils.getBooleanCellValue(line,"aFalseValue").orElseThrow(AssertionError::new));
    }

    @Test
    public void testSetCharCellValue() {
        Line line = new Line("TestLine");
        char ch = 'A';
        LineUtils.setCharCellValue(line,"aCharValue", ch);
        LineUtils.setStringCellValue(line,"aStringValue", "abc");
        LineUtils.setIntCellValue(line,"anIntValue", 4711);
        line.addCell(CharacterCell.emptyOf("empty"));
        assertEquals(Character.valueOf('A'), LineUtils.getCharCellValue(line,"aCharValue").orElse('B'));
        assertEquals('A', LineUtils.getCharCellValue(line,"aCharValue", 'B'));
        assertEquals('a', LineUtils.getCharCellValue(line,"aStringValue", 'B'));
        assertEquals(Character.valueOf('a'), LineUtils.getCharCellValue(line,"aStringValue").orElse('A'));
        assertEquals('4', LineUtils.getCharCellValue(line,"anIntValue", 'B'));
        assertEquals('B', LineUtils.getCharCellValue(line,"empty", 'B'));
    }


    @Test
    public void testIsCellSet() {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        assertTrue(line.isCellSet("FirstName"));
        assertFalse(line.isCellSet("LastName"));
    }

}