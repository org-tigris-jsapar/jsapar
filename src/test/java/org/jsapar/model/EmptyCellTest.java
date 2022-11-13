package org.jsapar.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class EmptyCellTest {

    @Test
    public void getStringValue() {
        EmptyCell<String> c = new EmptyCell<>("test", CellType.STRING);
        assertEquals("", c.getStringValue());
    }

    @Test
    public void compareValueTo() {
        EmptyCell<String> c1 = new EmptyCell<>("test1", CellType.STRING);
        EmptyCell<String> c2 = new EmptyCell<>("test2", CellType.STRING);
        StringCell stringCell = new StringCell("string", "value");
        StringCell emptyStringCell = new StringCell("string", "");
        assertEquals(0, c1.compareValueTo(c2));
        assertEquals(0, c1.compareValueTo(emptyStringCell));
        assertEquals(0, emptyStringCell.compareValueTo(c1));
        assertTrue(c1.compareValueTo(stringCell) < 0);
    }

    @Test
    public void isEmpty() {
        Cell<String> c = StringCell.emptyOf("test");
        assertTrue(c.isEmpty());
        assertNull(c.getValue());
        assertEquals("", c.getStringValue());
    }
}