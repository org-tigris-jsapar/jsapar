package org.jsapar.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FloatCellTest {

    @Test
    public void compareTo() {
        FloatCell c1 = new FloatCell("test", 123.45f);
        FloatCell c2 = new FloatCell("test2", 123.45f);
        FloatCell c3 = new FloatCell("test2", 20.1d);
        assertEquals(0, c1.compareTo(c2));
        assertTrue(c1.compareTo(c3) >0);
        assertTrue(c3.compareTo(c1) <0);
    }
}