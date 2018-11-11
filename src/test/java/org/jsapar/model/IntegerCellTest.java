package org.jsapar.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerCellTest {

    @Test
    public void compareTo() {
        IntegerCell c1 = new IntegerCell("test", 123L);
        IntegerCell c2 = new IntegerCell("test2", 123);
        IntegerCell c3 = new IntegerCell("test2", (short)20);
        IntegerCell c4 = new IntegerCell("test2", (byte)20);
        assertEquals(0, c1.compareTo(c2));
        assertEquals(0, c3.compareTo(c4));
        assertTrue(c1.compareTo(c3) >0);
        assertTrue(c3.compareTo(c1) <0);
    }

    @Test
    public void emptyOf() {
        Cell cell = IntegerCell.emptyOf("test");
        assertEquals("test", cell.getName());
        assertTrue(cell.isEmpty());
    }

    @Test
    public void compareValueTo() {
        IntegerCell c1 = new IntegerCell("test", 123L);
        IntegerCell c2 = new IntegerCell("test2", 123);
        IntegerCell c3 = new IntegerCell("test2", (short)20);
        IntegerCell c4 = new IntegerCell("test2", (byte)20);
        FloatCell c5 = new FloatCell("test3", 123.0);
        assertEquals(0, c1.compareValueTo(c2));
        assertEquals(0, c3.compareValueTo(c4));
        assertEquals(0, c1.compareValueTo(c5));
        assertTrue(c1.compareValueTo(c3) >0);
        assertTrue(c3.compareValueTo(c1) <0);
    }

}