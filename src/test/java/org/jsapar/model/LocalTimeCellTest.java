package org.jsapar.model;

import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class LocalTimeCellTest {

    @Test
    public void compareValueTo() {
        LocalTimeCell c1 = new LocalTimeCell("test1", LocalTime.of(12,45));
        LocalTimeCell c2 = new LocalTimeCell("test2", LocalTime.of(12,45));
        LocalTimeCell c3 = new LocalTimeCell("test3", LocalTime.of(10,45));
        assertEquals(0, c1.compareValueTo(c2));
        assertTrue(c1.compareValueTo(c3) >0);
        assertTrue(c3.compareValueTo(c1) <0);

    }

    @Test
    public void emptyOf() {
        Cell cell = LocalTimeCell.emptyOf("test");
        assertEquals("test", cell.getName());
        assertTrue(cell.isEmpty());
    }
}