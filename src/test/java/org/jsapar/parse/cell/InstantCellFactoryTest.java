package org.jsapar.parse.cell;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.model.CellType;
import org.jsapar.model.InstantCell;
import org.jsapar.text.Format;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import static org.junit.Assert.*;

public class InstantCellFactoryTest {
    final InstantCellFactory cellFactory = new InstantCellFactory();

    @Test
    public void makeCell() throws ParseException {
        InstantCell cell;
        Format<TemporalAccessor> format = Format.ofDateTimeInstance(DateTimeFormatter.ISO_INSTANT, CellType.INSTANT);
        cell = (InstantCell) cellFactory.makeCell("test", "2023-05-22T16:35:18Z", format);
        assertEquals(Instant.ofEpochSecond(1684773318), cell.getValue());
        assertEquals("2023-05-22T16:35:18Z", format.format(Instant.ofEpochSecond(1684773318)));
    }
}