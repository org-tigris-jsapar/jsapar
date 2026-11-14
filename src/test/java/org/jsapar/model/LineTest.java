package org.jsapar.model;

import org.jsapar.parse.CellParseException;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LineTest {

    @Test
    public void testLine() {
        Line line = new Line("");
        assertEquals(0, line.size());
        assertEquals("", line.getLineType());
    }

    @Test
    public void testLineInt() {
        Line line = new Line("");
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
        Line line = new Line("Shoe");
        assertEquals(0, line.size());
        assertEquals("Shoe", line.getLineType());
    }


    @Test
    public void testGetCells() {
        Line line = makeTestLine();
        java.util.List<Cell<?>> cells = line.getCells();
        assertEquals(2, cells.size());
        assertEquals("Svensson", cells.get(1).getStringValue());
    }

    @Test
    public void testClone() {
        Line line = makeTestLine();
        Line clone = line.clone();
        assertEquals(2, clone.size());
        assertNotSame(line.getCells(), clone.getCells());
        assertEquals(line.getCells(), clone.getCells());
    }

    @Test
    public void testGetProperty() {
        Line line = makeTestLine();
        assertEquals("Svensson", line.getProperty("LastName"));
    }

    @Test
    public void testGetCellIterator() {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        java.util.Iterator<Cell<?>> i = line.iterator();
        assertNotNull(i);
    }

    @Test
    public void testAddCellCell() {
        Line line = makeTestLine();
        assertEquals("Nils", LineUtils.getStringCellValue(line, "FirstName"));
        assertEquals("Svensson", LineUtils.getStringCellValue(line, "LastName"));
    }

    @Test
    public void testAddCell_twice() {
        assertThrows(IllegalStateException.class, () -> {
            Line line = new Line("TestLine");
            line.addCell(new StringCell("FirstName", "Nils"));
            line.addCell(new StringCell("FirstName", "Svensson"));
            fail("Should throw exception for duplicate cell names.");
        });
    }


    @Test
    public void testReplaceCell() {
        Line line = makeTestLine();

        line.putCell(new StringCell("FirstName", "Sven"));
        assertEquals(2, line.size());
        assertEquals("Sven", LineUtils.getStringCellValue(line, "FirstName"));
        assertEquals("Svensson", LineUtils.getStringCellValue(line, "LastName"));
    }


    @Test
    public void testGetCellString() {
        Line line = makeTestLine();
        assertEquals("Nils", LineUtils.getStringCellValue(line, "FirstName"));
    }


    @Test
    public void testGetNumberOfCells() {
        Line line = makeTestLine();
        assertEquals(2, line.size());
    }

    @Test
    public void testGetLineType() {
        Line line = new Line("TestLine");
        assertEquals("TestLine", line.getLineType());
    }

    @Test
    public void testRemoveCell() {
        Line line = makeTestLine();

        assertEquals("Nils", line.removeCell("FirstName").map(Cell::getStringValue).orElse(""));
        assertEquals(1, line.size());
        assertEquals("Svensson", LineUtils.getStringCellValue(line, "LastName"));
    }

    @Test
    public void testToString() {
        Line line = makeTestLine();
        assertEquals("Line type=[TestLine] number=1 {FirstName=Nils [STRING], LastName=Svensson [STRING]}", line.toString());
    }

    private Line makeTestLine() {
        Line line = new Line("TestLine", 3, 1);
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(new StringCell("LastName", "Svensson"));
        return line;
    }

    @Test
    public void testAddCellError() {
        Line line = makeTestLine();
        assertFalse(line.hasCellErrors());
        assertEquals(0, line.getCellErrors().size());
        CellParseException theError = new CellParseException(17, "FirstName", "some value",
                new SchemaCellFormat<>(CellType.STRING), "Testing error");
        line.addCellError(theError);
        assertTrue(line.hasCellErrors());
        assertEquals(1, line.getCellErrors().size());
        assertSame(theError, line.getCellError("FirstName").orElseThrow(()-> new AssertionError("fail")));
        assertFalse(line.getCellError("LastName").isPresent());
    }


    @Test
    public void testIsCellOfType() {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        assertTrue(line.containsNonEmptyCell("FirstName", CellType.STRING));
        assertFalse(line.containsNonEmptyCell("FirstName", CellType.INTEGER));
        assertFalse(line.containsNonEmptyCell("LastName", CellType.STRING));
    }

    @Test
    public void testGetNonEmptyCell() {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(StringCell.emptyOf("LastName"));

        assertEquals("Nils", line.getNonEmptyCell("FirstName").map(Cell::getStringValue).orElse(""));
        assertFalse(line.getNonEmptyCell("LastName").isPresent());
        assertFalse(line.getNonEmptyCell("DoesNotExist").isPresent());
    }

    @Test
    public void testGetExistingCell() {
        Line line = new Line("TestLine");
        line.addCell(new StringCell("FirstName", "Nils"));
        line.addCell(StringCell.emptyOf("LastName"));

        assertEquals("Nils", line.getExistingCell("FirstName").getStringValue());
        assertEquals("", line.getExistingCell("LastName").getStringValue());
    }

    @Test
    public void testGetExistingCell_nonExisting() {
        assertThrows(IllegalStateException.class, () -> {
            Line line = new Line("TestLine");

            line.getExistingCell("NonExisting");
            fail("Should throw exception");
        });
    }


    @Test
    public void testGetCellOrDefault() {
        Line line = new Line("TestLine");
        StringCell cell = new StringCell("a", "value");
        line.addCell(cell);
        StringCell defaultCell = new StringCell("b", "default");
        assertSame(cell, line.getCellOrDefault("a", defaultCell));
        assertSame(defaultCell, line.getCellOrDefault("b", defaultCell));
        assertNull(line.getCellOrDefault("b", null));
    }

    @Test
    public void testCellErrors_noErrors() {
        Line line = new Line("TestLine");
        assertFalse(line.hasCellErrors());
        assertTrue(line.getCellErrors().isEmpty());
        assertFalse(line.getCellError("a").isPresent());
        Line clone = line.clone();
        assertFalse(clone.hasCellErrors());
        assertTrue(clone.getCellErrors().isEmpty());
    }

    @Test
    public void testCellErrors_cloneIsIndependent() {
        Line line = new Line("TestLine");
        line.addCellError(new CellParseException("a", "x", null, "error a"));
        Line clone = line.clone();
        clone.addCellError(new CellParseException("b", "y", null, "error b"));
        assertEquals(1, line.getCellErrors().size());
        assertEquals(2, clone.getCellErrors().size());
        assertTrue(line.getCellError("a").isPresent());
    }

    @Test
    public void testAddCell_duplicateKeepsOriginal() {
        Line line = new Line("TestLine");
        StringCell first = new StringCell("a", "first");
        line.addCell(first);
        assertThrows(IllegalStateException.class, () -> line.addCell(new StringCell("a", "second")));
        assertSame(first, line.getExistingCell("a"));
    }
}
