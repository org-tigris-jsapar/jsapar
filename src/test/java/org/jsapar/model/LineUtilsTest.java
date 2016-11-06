package org.jsapar.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        assertEquals("Nils", LineUtils.getStringCellValue(line,"FirstName"));
        assertEquals("Svensson", LineUtils.getStringCellValue(line,"LastName"));
        assertEquals("Svensson", LineUtils.getStringCellValue(line,"LastName", "Karlsson"));
        assertEquals("Karlsson", LineUtils.getStringCellValue(line,"Nothing", "Karlsson"));
    }
    

    private enum Testing{
        FIRST, SECOND
    }

    @Test
    public void testGetSetEnumCellValue() throws Exception {
        Line line = new Line("TestLine");
        LineUtils.setEnumCellValue(line, "Type", Testing.FIRST);
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line, "Type", Testing.SECOND));
    }
    
    @Test
    public void testGetEnumCellValue_default() throws Exception {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("Type", "FIRST"));
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line, "Type", Testing.SECOND));
        assertEquals(Testing.SECOND, LineUtils.getEnumCellValue(line, "Type does not exist", Testing.SECOND));
        assertNull( LineUtils.getEnumCellValue(line, "Type does not exist", (Testing)null));
    }


    @Test
    public void testGetEnumCellValue() {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("Type", "FIRST"));
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line,"Type", Testing.class));
    }
    
    @Test
    public void testGetSetIntCellValue() {
        Line line = new Line("TestLine");
        line.addCell(new IntegerCell("shoeSize", 42));
        line.addCell(new FloatCell("pi", 3.141));
        LineUtils.setStringCellValue(line, "aStringValue", "17");
        LineUtils.setIntCellValue(line, "anIntValue", 4711);

        assertEquals(42, LineUtils.getIntCellValue(line,"shoeSize"));
        assertEquals(42, LineUtils.getIntCellValue(line,"shoeSize", 55));
        assertEquals(4711, LineUtils.getIntCellValue(line,"doesNotExist", 4711));
        assertEquals(17, LineUtils.getIntCellValue(line,"aStringValue"));
        assertEquals(3, LineUtils.getIntCellValue(line,"pi"));
        assertEquals(4711, LineUtils.getIntCellValue(line,"anIntValue"));

    }

    @Test(expected=IllegalStateException.class)
    public void testGetIntCellValue_dont_exist() {
        Line line = new Line("TestLine");

        LineUtils.getIntCellValue(line,"shoeSize");
        Assert.fail("Should throw exception");
    }

    @Test(expected=NumberFormatException.class)
    public void testGetIntCellValue_not_parsable() {
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "ABC");

        LineUtils.getIntCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }

    @Test
    public void testGetSetLongCellValue() throws Exception {
        Line line = new Line("TestLine");
        LineUtils.setLongCellValue(line, "Value", 314159265358979L);
        assertEquals(314159265358979L, LineUtils.getLongCellValue(line,"Value"));
        assertEquals(314159265358979L, LineUtils.getLongCellValue(line,"Value"), 17L);
        assertEquals(17L, LineUtils.getLongCellValue(line,"DoesNotExist", 17L));
    }

    @Test
    public void testGetSetDoubleCellValue() throws Exception {
        Line line = new Line("TestLine");
        LineUtils.setDoubleCellValue(line, "Value", 3.14159265358979D);
        assertEquals(3.14159265358979D, LineUtils.getDoubleCellValue(line,"Value"), 0.0000000001);
        assertEquals(3.14159265358979D, LineUtils.getDoubleCellValue(line,"Value", 2.71D), 0.0000000001);
        assertEquals(2.718D, LineUtils.getDoubleCellValue(line,"DoesNotExist", 2.718D), 0.0000000001);
    }


    @Test
    public void testGetSetDecimalCellValue() throws Exception {
        Line line = new Line("TestLine");
        LineUtils.setDoubleCellValue(line, "pi", 3.14159265358979D);
        LineUtils.setDecimalCellValue(line, "e", new BigDecimal("2.718"));
        assertEquals(new BigDecimal(3.14159265358979D), LineUtils.getDecimalCellValue(line,"pi"));
        assertEquals(new BigDecimal("2.718"), LineUtils.getDecimalCellValue(line,"e"));
        assertEquals(new BigDecimal(3.14159265358979D), LineUtils.getDecimalCellValue(line,"pi", new BigDecimal(2.71D)));
        assertEquals(new BigDecimal("23.14"), LineUtils.getDecimalCellValue(line,"gelfond", new BigDecimal("23.14")));
    }

    @Test
    public void testSetBigIntegerCellValue() throws Exception {
        Line line = new Line("TestLine");
        LineUtils.setIntCellValue(line, "random", 4711);
        LineUtils.setBigIntegerCellValue(line, "douglas", BigInteger.valueOf(42L));
        assertEquals(new BigInteger("4711"), LineUtils.getBigIntegerCellValue(line,"random"));
        assertEquals(new BigInteger("42"), LineUtils.getBigIntegerCellValue(line,"douglas"));
        assertEquals(new BigInteger("42"), LineUtils.getBigIntegerCellValue(line,"douglas", BigInteger.valueOf(42L)));
        assertEquals(new BigInteger("23"), LineUtils.getBigIntegerCellValue(line,"pi", new BigInteger("23")));
    }

    @Test
    public void testGetSetDateCellValue() {
        Line line = new Line("TestLine");
        Date date = new Date();
        LineUtils.setDateCellValue(line,"aDateValue", date);
        assertEquals(date, LineUtils.getDateCellValue(line,"aDateValue"));
        assertSame(date, LineUtils.getDateCellValue(line,"aDateValue", new Date()));
        assertNotSame(date, LineUtils.getDateCellValue(line,"doesNotExist", new Date()));
        LineUtils.setDateCellValue(line,"aDateValue", null);
        assertFalse(line.isCell("aDateValue"));
    }

    @Test
    public void testSetBooleanCellValue() {
        Line line = new Line("TestLine");
        LineUtils.setBooleanCellValue(line,"aTrueValue", true);
        LineUtils.setBooleanCellValue(line,"aFalseValue", false);
        assertTrue(LineUtils.getBooleanCellValue(line,"aTrueValue"));
        assertTrue(LineUtils.getBooleanCellValue(line,"aTrueValue", false));
        assertFalse(LineUtils.getBooleanCellValue(line,"anotherValue", false));
        assertFalse(LineUtils.getBooleanCellValue(line,"aFalseValue"));
    }

    @Test
    public void testSetCharCellValue() {
        Line line = new Line("TestLine");
        char ch = 'A';
        LineUtils.setCharCellValue(line,"aCharValue", ch);
        assertEquals('A', LineUtils.getCharCellValue(line,"aCharValue"));
        assertEquals('A', LineUtils.getCharCellValue(line,"aCharValue", 'B'));
        assertEquals('B', LineUtils.getCharCellValue(line,"another", 'B'));
    }


    @Test
    public void testIsCellSet() throws Exception {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        assertTrue(LineUtils.isCellSet(line, "FirstName"));
        assertFalse(LineUtils.isCellSet(line, "LastName"));
    }

    @Test
    public void testIsCellSetType() throws Exception {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        assertTrue(LineUtils.isCellSet(line, "FirstName", CellType.STRING));
        assertFalse(LineUtils.isCellSet(line, "FirstName", CellType.INTEGER));
        assertFalse(LineUtils.isCellSet(line, "LastName", CellType.STRING));
    }
}