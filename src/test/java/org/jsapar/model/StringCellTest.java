package org.jsapar.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class StringCellTest {

    @Test
    public void testGetSetStringValue() {
        StringCell cell = new StringCell("name", "value");
        assertEquals("value", cell.getStringValue());
    }

    @Test
    public void testIsEmpty() {
        StringCell c = new StringCell("empty", "");
        assertTrue(c.isEmpty());
    }

    @Test
    public void testCompareTo() {
        StringCell c1 = new StringCell("name1", "value");
        StringCell c2 = new StringCell("name1", "value");
        StringCell c3 = new StringCell("name3", "another");
        //noinspection EqualsWithItself
        assertEquals(0, c1.compareTo(c1));
        assertEquals(0, c1.compareTo(c2));
        assertEquals(0, c2.compareTo(c1));
        assertTrue(c1.compareTo(c3) < 0);
        assertTrue(c3.compareTo(c1) > 0);
    }

    @Test
    public void testHash() {
        StringCell c1 = new StringCell("name", "value");
        StringCell c2 = new StringCell("name", "value");
        StringCell c3 = new StringCell("name3", "value");
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

}