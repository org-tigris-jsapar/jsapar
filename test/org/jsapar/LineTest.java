package org.jsapar;

import static org.junit.Assert.*;

import org.junit.After;
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
	assertEquals(0, line.getNumberOfCells());
	assertEquals(null, line.getLineType());
    }

    @Test
    public void testLineInt() {
	Line line = new Line(3);
	assertEquals(0, line.getNumberOfCells());
	assertEquals(null, line.getLineType());
    }

    @Test
    public void testLineString() {
	Line line = new Line("Shoe");
	assertEquals(0, line.getNumberOfCells());
	assertEquals("Shoe", line.getLineType());
    }

    @Test
    public void testLineStringInt() {
	Line line = new Line("Shoe", 3);
	assertEquals(0, line.getNumberOfCells());
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
	assertNotNull( i);
    }

    @Test
    public void testAddCellCell() throws JSaParException {
	Line line = new Line("TestLine");
	line.addCell(new StringCell("FirstName", "Nils"));
	line.addCell(new StringCell("LastName", "Svensson"));
	assertEquals("Nils", line.getCell("FirstName").getStringValue());
	assertEquals("Svensson", line.getCell(1).getStringValue());
    }

    @Test(expected=JSaParException.class)
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

    @Test
    public void testGetNumberOfCells() throws JSaParException {
	Line line = new Line("TestLine");
	line.addCell(new StringCell("FirstName", "Nils"));
	line.addCell(new StringCell("LastName", "Svensson"));
	assertEquals(2, line.getNumberOfCells());
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

}
