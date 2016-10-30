package org.jsapar.model;

import org.jsapar.error.JSaParException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    }
    

    private enum Testing{
        FIRST, SECOND
    }

    @Test
    public void testSetEnumCellValue() throws Exception {

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
    public void testGetEnumCellValue() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("Type", "FIRST"));
        assertEquals(Testing.FIRST, LineUtils.getEnumCellValue(line,"Type", Testing.class));
    }
    
    @Test
    public void testSetIntCellValue() throws Exception {

    }

    @Test
    public void testSetLongCellValue() throws Exception {

    }

    @Test
    public void testSetDoubleCellValue() throws Exception {

    }


    @Test
    public void testSetDecimalCellValue() throws Exception {

    }

    @Test
    public void testSetBigIntegerCellValue() throws Exception {

    }

    @Test
    public void testGetStringCellValue() throws Exception {

    }

    @Test
    public void testGetStringCellValue1() throws Exception {

    }


    @Test
    public void testGetIntCellValue() throws JSaParException{
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
    public void testGetIntCellValue_not_parsable() throws JSaParException{
        Line line = new Line("TestLine");
        LineUtils.setStringCellValue(line,"aStringValue", "ABC");

        LineUtils.getIntCellValue(line,"aStringValue");
        Assert.fail("Should throw exception");
    }
    

    @Test
    public void testGetLongCellValue() throws Exception {

    }

    @Test
    public void testGetLongCellValue1() throws Exception {

    }

    @Test
    public void testGetCharCellValue() throws Exception {

    }

    @Test
    public void testGetCharCellValue1() throws Exception {

    }

    @Test
    public void testGetBooleanCellValue() throws Exception {

    }

    @Test
    public void testGetBooleanCellValue1() throws Exception {

    }

    @Test
    public void testGetDateCellValue() throws Exception {

    }

    @Test
    public void testGetDateCellValue1() throws Exception {

    }

    @Test
    public void testSetDateCellValue() {
        Line line = new Line("TestLine");
        Date date = new Date();
        LineUtils.setDateCellValue(line,"aDateValue", date);
        assertEquals(date, LineUtils.getDateCellValue(line,"aDateValue"));
        LineUtils.setDateCellValue(line,"aDateValue", null);
        assertFalse(line.isCell("aDateValue"));
    }

    @Test
    public void testSetBooleanCellValue() throws JSaParException{
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
    public void testGetEnumCellValue1() throws Exception {

    }

    @Test
    public void testGetDoubleCellValue() throws Exception {

    }

    @Test
    public void testGetDoubleCellValue1() throws Exception {

    }

    @Test
    public void testGetDecimalCellValue() throws Exception {

    }

    @Test
    public void testGetDecimalCellValue1() throws Exception {

    }

    @Test
    public void testGetBigIntegerCellValue() throws Exception {

    }

    @Test
    public void testGetBigIntegerCellValue1() throws Exception {

    }
}