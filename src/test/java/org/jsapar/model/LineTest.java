package org.jsapar.model;

import static org.junit.Assert.*;

import java.util.Date;

import org.jsapar.JSaParException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LineTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLine() {
        Line line = new Line();
        assertEquals(0, line.size());
        assertEquals("", line.getLineType());
    }

    @Test
    public void testLineInt() {
        Line line = new Line(3);
        assertEquals(0, line.size());
        assertEquals("", line.getLineType());
    }

    @Test
    public void testLineString() {
        Line line = new Line("Shoe");
        assertEquals(0, line.size());
        assertEquals("Shoe", line.getLineType());
    }

    @Test
    public void testLineStringInt() {
        Line line = new Line("Shoe", 3);
        assertEquals(0, line.size());
        assertEquals("Shoe", line.getLineType());
    }

    @Test
    public void testGetCells() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));
        java.util.List<Cell> cells = line.getCells();
        assertEquals(2, cells.size());
        assertEquals("Svensson", cells.get(1).getStringValue());
    }

    @Test
    public void testGetCellIterator() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        java.util.Iterator<Cell> i = line.getCellIterator();
        assertNotNull(i);
    }

    @Test
    public void testAddCellCell() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));
        assertEquals("Nils", line.getCell("FirstName").getStringValue());
        assertEquals("Svensson", line.getCell(1).getStringValue());
    }

    @Test(expected = JSaParException.class)
    public void testAddCell_twice() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("FirstName", "Svensson"));
        fail("Should throw exception for duplicate cell names.");
    }

    @Test
    public void testAddCellCellInt() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"), 0);
        assertEquals("Nils", line.getCell(1).getStringValue());
        assertEquals("Svensson", line.getCell(0).getStringValue());
        assertEquals("Nils", line.getCell("FirstName").getStringValue());
        assertEquals("Svensson", line.getCell("LastName").getStringValue());
    }

    @Test(expected = JSaParException.class)
    public void testAddCellCellInt_twice() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"), 0);
        line.addCell(new StringCell("FirstName", "Svensson"), 0);
        fail("Should throw exception for duplicate cell names.");
    }

    
    @Test
    public void testReplaceCell() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));

        line.replaceCell(new StringCell("FirstName", "Sven"), 0);
        assertEquals("Sven", line.getCell(0).getStringValue());
        assertEquals("Svensson", line.getCell(1).getStringValue());
        assertEquals("Sven", line.getCell("FirstName").getStringValue());
        assertEquals("Svensson", line.getCell("LastName").getStringValue());
    }

    @Test
    public void testReplaceCell_twoRemoved() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));

        line.replaceCell(new StringCell("LastName", "Sven"), 0);
        assertEquals(1, line.size());
        assertEquals("Sven", line.getCell(0).getStringValue());
        assertEquals("Sven", line.getCell("LastName").getStringValue());
    }

    @Test
    public void testGetCellInt() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));
        assertEquals("Nils", line.getCell(0).getStringValue());
    }

    @Test
    public void testGetCellString() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));
        assertEquals("Nils", line.getCell("FirstName").getStringValue());
    }

    enum Testing{
        FIRST, SECOND, THIRD
    }
    
    @Test
    public void testGetCellValue_Enum() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("Type", "FIRST"));
        assertEquals(Testing.FIRST, line.getEnumCellValue("Type", Testing.SECOND));
        assertEquals(Testing.SECOND, line.getEnumCellValue("Type does not exist", Testing.SECOND));
        assertNull( line.getEnumCellValue("Type does not exist", (Testing)null));
    }

    @Test
    public void testGetEnumCellValue() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("Type", "FIRST"));
        assertEquals(Testing.FIRST, line.getEnumCellValue("Type", Testing.class));
    }
    
    @Test
    public void testGetSetStringCellValue() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.setStringCellValue("LastName", "Svensson");
        assertEquals("Nils", line.getStringCellValue("FirstName"));
        assertEquals("Svensson", line.getStringCellValue("LastName"));
    }

    @Test
    public void testGetNumberOfCells() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));
        assertEquals(2, line.size());
    }

    @Test
    public void testGetLineType() {
        Line line = new Line("TestLine");
        assertEquals("TestLine", line.getLineType());
    }

    @Test
    public void testSetLineType() {
        Line line = new Line("TestLine");
        line.setLineType("Rocking");
        assertEquals("Rocking", line.getLineType());
    }

    /**
     * @throws JSaParException
     */
    @Test
    public void testRemoveCell() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));

        line.removeCell("FirstName");
        assertEquals(1, line.size());
        assertEquals("Svensson", line.getCell(0).getStringValue());
        assertEquals("Svensson", line.getCell("LastName").getStringValue());
    }

    /**
     * @throws JSaParException
     */
    @Test
    public void testRemoveCell_ByIndex() throws JSaParException {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));

        line.removeCell(0);
        assertEquals(1, line.size());
        assertEquals("Svensson", line.getCell(0).getStringValue());
        assertEquals("Svensson", line.getCell("LastName").getStringValue());
    }

    @Test
    public void testGetIntCellValue() throws JSaParException{
        Line line = new Line("TestLine");
        line.addCell(new IntegerCell("shoeSize", 42));
        line.addCell(new FloatCell("pi", 3.141));
        line.setStringCellValue("aStringValue", "17");
        line.setIntCellValue("anIntValue", 4711);
        
        assertEquals(42, line.getIntCellValue("shoeSize"));
        assertEquals(17, line.getIntCellValue("aStringValue"));
        assertEquals(3, line.getIntCellValue("pi"));
        assertEquals(4711, line.getIntCellValue("anIntValue"));
        
    }
    
    @Test(expected=JSaParException.class)
    public void testGetIntCellValue_dont_exist() throws JSaParException{
        Line line = new Line("TestLine");
        
        line.getIntCellValue("shoeSize");
        Assert.fail("Should throw exception");
    }

    @Test(expected=NumberFormatException.class)
    public void testGetIntCellValue_not_parsable() throws JSaParException{
        Line line = new Line("TestLine");
        line.setStringCellValue("aStringValue", "ABC");
        
        line.getIntCellValue("aStringValue");
        Assert.fail("Should throw exception");
    }
    
    @Test
    public void testSetCellValueString_null(){
        Line line = new Line("TestLine");
        line.setStringCellValue("aStringValue", "ABC");
        assertEquals("ABC", line.getStringCellValue("aStringValue"));
        line.setStringCellValue("aStringValue", (String)null);
        assertFalse(line.isCell("aStringValue"));
    }
    
    @Test
    public void testSetCellValueString_date() throws JSaParException{
        Line line = new Line("TestLine");
        Date date = new Date();
        line.setDateCellValue("aDateValue", date);
        assertEquals(date, line.getDateCellValue("aDateValue"));
        line.setDateCellValue("aDateValue", (Date)null);
        assertFalse(line.isCell("aDateValue"));
    }

    @Test
    public void testSetCellValue_boolean() throws JSaParException{
        Line line = new Line("TestLine");
        boolean t = true;
        boolean f = false;
        line.setBooleanCellValue("aTrueValue", t);
        line.setBooleanCellValue("aFalseValue", f);
        assertTrue(line.getBooleanCellValue("aTrueValue"));
        assertTrue(line.getBooleanCellValue("aTrueValue", false));
        assertFalse(line.getBooleanCellValue("anotherValue", false));
        assertFalse(line.getBooleanCellValue("aFalseValue"));
    }

    @Test
    public void testSetCellValue_char() throws JSaParException{
        Line line = new Line("TestLine");
        char ch = 'A';
        line.setCharCellValue("aCharValue", ch);
        assertEquals('A', line.getCharCellValue("aCharValue"));
        assertEquals('A', line.getCharCellValue("aCharValue", 'B'));
        assertEquals('B', line.getCharCellValue("another", 'B'));
    }
    
}
