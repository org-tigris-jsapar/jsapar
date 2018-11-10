package org.jsapar.model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class ZonedDateTimeCellTest {

    @Test
    public void compareValueTo() {
        ZonedDateTimeCell c1 = new ZonedDateTimeCell("test1", ZonedDateTime.of(2018, 11, 9, 12, 45, 30, 0, ZoneId.of("UTC")));
        ZonedDateTimeCell c2 = new ZonedDateTimeCell("test2", ZonedDateTime.of(2018, 11, 9, 12, 45, 30, 0, ZoneId.of("UTC")));
        ZonedDateTimeCell c3 = new ZonedDateTimeCell("test3", ZonedDateTime.of(2018, 11, 9, 10,45,30, 0, ZoneId.of("UTC")));
        assertEquals(0, c1.compareValueTo(c2));
        assertTrue(c1.compareValueTo(c3) >0);
        assertTrue(c3.compareValueTo(c1) <0);
    }

    @Test
    public void emptyOf() {
        Cell cell = ZonedDateTimeCell.emptyOf("test");
        assertEquals("test", cell.getName());
        assertTrue(cell.isEmpty());
    }
}