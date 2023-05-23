package org.jsapar.text;

import org.jsapar.model.CellType;
import org.jsapar.text.format.DateTimeFormat;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import static org.junit.Assert.*;

public class DateTimeFormatTest {

    @Test
    public void parse() {
        DateTimeFormat format = new DateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US), CellType.INSTANT,
                ZoneId.of("UTC"));
        TemporalAccessor temporalValue = format.parse("2023-05-22 16:35");
        assertFalse(temporalValue.isSupported(ChronoField.OFFSET_SECONDS));
        assertEquals(Instant.ofEpochSecond(1684773300), LocalDateTime.from(temporalValue).atZone(format.getZoneId()).toInstant());
    }

    @Test
    public void format() {
        DateTimeFormat format = new DateTimeFormat(DateTimeFormatter.ISO_INSTANT, CellType.INSTANT,
                ZoneId.of("UTC"));
        assertEquals("2023-05-22T16:35:19Z", format.format(Instant.ofEpochSecond(1684773319)));
    }

    @Test
    public void format_pattern() {
        DateTimeFormat format = new DateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US), CellType.INSTANT,
                ZoneId.of("UTC"));
        assertEquals("2023-05-22 16:35", format.format(Instant.ofEpochSecond(1684773319)));
    }

}