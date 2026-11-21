package org.jsapar.model;

import org.jsapar.parse.CellParseException;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void testDetails_none() {
        Line line = new Line("Order");
        assertFalse(line.hasDetails());
        assertTrue(line.getDetails().isEmpty());
        assertTrue(line.getDetails("OrderLine").isEmpty());
        assertFalse(line.toString().contains("details"));
    }

    @Test
    public void testAddDetail_keepsOrderAcrossLineTypes() {
        Line order = new Line("Order");
        Line item1 = new Line("OrderLine");
        Line log = new Line("HistoryLog");
        Line item2 = new Line("OrderLine");
        assertSame(order, order.addDetail(item1).addDetail(log).addDetail(item2));

        assertTrue(order.hasDetails());
        assertEquals(List.of(item1, log, item2), order.getDetails());
        assertEquals(List.of(item1, item2), order.getDetails("OrderLine"));
        assertEquals(List.of(log), order.getDetails("HistoryLog"));
        assertTrue(order.getDetails("Unknown").isEmpty());
        assertTrue(order.toString().endsWith(" details=3"), order.toString());
    }

    @Test
    public void testAddDetail_nested() {
        Line order = new Line("Order");
        Line item = new Line("OrderLine");
        Line discount = new Line("Discount");
        order.addDetail(item);
        item.addDetail(discount);
        assertEquals(List.of(discount), order.getDetails().getFirst().getDetails());
    }

    @Test
    public void testAddDetail_null() {
        assertThrows(NullPointerException.class, () -> new Line("Order").addDetail(null));
    }

    @Test
    public void testGetDetails_unmodifiable() {
        Line order = new Line("Order").addDetail(new Line("OrderLine"));
        assertThrows(UnsupportedOperationException.class, () -> order.getDetails().add(new Line("OrderLine")));
        assertThrows(UnsupportedOperationException.class, () -> new Line("Order").getDetails().add(new Line("OrderLine")));
    }

    @Test
    public void testClone_details() {
        Line item = new Line("OrderLine");
        Line order = new Line("Order").addDetail(item);
        Line clone = order.clone();
        assertEquals(List.of(item), clone.getDetails());
        assertSame(item, clone.getDetails().getFirst());

        clone.addDetail(new Line("OrderLine"));
        assertEquals(1, order.getDetails().size());
        assertEquals(2, clone.getDetails().size());

        assertFalse(new Line("Order").clone().hasDetails());
    }

    @Test
    void removeDetailsIf() {
        Line master = new Line("Order");
        Line a = new Line("OrderLine");
        Line b = new Line("HistoryLog");
        Line c = new Line("OrderLine");
        a.addDetail(new Line("Discount"));
        assertFalse(master.removeDetailsIf(d -> true), "No details");
        master.addDetail(a).addDetail(b).addDetail(c);
        assertTrue(master.removeDetailsIf(d -> d.getLineType().equals("OrderLine")));
        assertEquals(List.of(b), master.getDetails());
        assertFalse(master.removeDetailsIf(d -> false));
        assertEquals(1, a.getDetails().size(), "Detail lines of removed lines are not changed");
    }
}
