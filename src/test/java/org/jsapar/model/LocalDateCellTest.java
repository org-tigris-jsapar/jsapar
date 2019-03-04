package org.jsapar.model;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class LocalDateCellTest {

    @Test
    public void compareValueTo() {
        LocalDateCell c1 = new LocalDateCell("test1", LocalDate.of(2018, 11, 10));
        LocalDateCell c2 = new LocalDateCell("test1", LocalDate.of(2018, 11, 11));
        assertTrue(c1.compareValueTo(c2)<0);
    }

    @Test
    public void emptyOf() {
        Cell c = LocalDateCell.emptyOf("test");
        assertTrue(c.isEmpty());
    }
}