package org.jsapar.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FloatCellTest {

    @Test
    public void compareValueTo() {
        FloatCell c1 = new FloatCell("test", 123.45f);
        FloatCell c2 = new FloatCell("test2", 123.45f);
        FloatCell c3 = new FloatCell("test2", 20.1d);
        assertEquals(0, c1.compareValueTo(c2));
        assertTrue(c1.compareValueTo(c3) >0);
        assertTrue(c3.compareValueTo(c1) <0);
    }

    @Test
    public void compareTo() {
        FloatCell c1 = new FloatCell("test", 123.45f);
        FloatCell c2 = new FloatCell("test2", 123.45f);
        FloatCell c3 = new FloatCell("test2", 20.1d);
        assertTrue(c1.compareTo(c2) < 0);
        assertTrue(c1.compareTo(c3) < 0);
        assertTrue(c3.compareTo(c1) > 0);
        assertTrue(c2.compareTo(c3) > 0);
        assertTrue(c3.compareTo(c2) < 0);
        assertEquals(0, c1.compareTo(c1));
    }
}