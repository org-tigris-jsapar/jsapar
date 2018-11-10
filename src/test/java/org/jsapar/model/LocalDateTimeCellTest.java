package org.jsapar.model;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class LocalDateTimeCellTest {

    @Test
    public void compareValueTo() {
        LocalDateTimeCell c1 = new LocalDateTimeCell("test1", LocalDateTime.of(2018, 11, 9, 12,45));
        LocalDateTimeCell c2 = new LocalDateTimeCell("test2", LocalDateTime.of(2018, 11, 9, 12,45));
        LocalDateTimeCell c3 = new LocalDateTimeCell("test3", LocalDateTime.of(2018, 11, 9, 10,45));
        assertEquals(0, c1.compareValueTo(c2));
        assertTrue(c1.compareValueTo(c3) >0);
        assertTrue(c3.compareValueTo(c1) <0);
    }

    @Test
    public void emptyOf() {
        Cell cell = LocalDateTimeCell.emptyOf("test");
        assertEquals("test", cell.getName());
        assertTrue(cell.isEmpty());
    }
}