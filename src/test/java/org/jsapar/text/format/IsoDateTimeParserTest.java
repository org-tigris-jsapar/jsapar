package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IsoDateTimeParserTest {

    private static final List<String> DATES = List.of(
            "2024-01-31", "2024-02-29", "2023-02-29", "2024-13-01", "2024-00-10", "2024-04-31", "0000-01-01",
            "9999-12-31", "2024-1-01", "2024/01/01", "20240101", "2024-01-0a", "+12024-01-01", "-0001-01-01",
            "2024-01-01+01:00", "2024-01-01Z", " 2024-01-01", "2024-01-01 ", "", "٢٠٢٤-٠١-٠١");

    private static final List<String> TIMES = List.of(
            "00:00", "23:59", "24:00", "12:60", "12:30:00", "12:30:59", "12:30:60", "12:30:15.1", "12:30:15.123456789",
            "12:30:15.1234567890", "12:30:15.", "12:30:1", "1:30", "12:3", "12-30", "12:30Z", "12:30:15+01:00",
            "12:30:15.5Z", "12:30:15,5", "ab:cd", "");

    @Test
    void localDate_equivalentToFormatter() {
        assertEquivalent(DateTimeFormatter.ISO_DATE, CellType.LOCAL_DATE, DATES, LocalDate::from);
    }

    @Test
    void localTime_equivalentToFormatter() {
        assertEquivalent(DateTimeFormatter.ISO_TIME, CellType.LOCAL_TIME, TIMES, LocalTime::from);
    }

    @Test
    void localDateTime_equivalentToFormatter() {
        List<String> values = new java.util.ArrayList<>();
        for (String date : DATES)
            for (String time : TIMES)
                values.add(date + "T" + time);
        values.add("2024-01-01 12:00");
        values.add("2024-01-01t12:00");
        values.add("2024-01-01T");
        assertEquivalent(DateTimeFormatter.ISO_DATE_TIME, CellType.LOCAL_DATE_TIME, values, LocalDateTime::from);
    }

    @Test
    void notUsedForOtherFormatters() {
        assertNull(IsoDateTimeParser.of(DateTimeFormatter.ofPattern("yyyy-MM-dd"), CellType.LOCAL_DATE));
        assertNull(IsoDateTimeParser.of(DateTimeFormatter.ISO_DATE, CellType.ZONED_DATE_TIME));
        assertNull(IsoDateTimeParser.of(DateTimeFormatter.ISO_OFFSET_DATE_TIME, CellType.ZONED_DATE_TIME));
    }

    @Test
    void format_localDate_sameAsFormatter() {
        DateTimeFormat format = new DateTimeFormat(DateTimeFormatter.ISO_DATE, CellType.LOCAL_DATE, ZoneId.of("UTC"));
        for (int year : new int[]{-12345, -1, 0, 1, 999, 1000, 2024, 9999, 10000, 123456}) {
            LocalDate date = LocalDate.of(year, 3, 7);
            assertEquals(DateTimeFormatter.ISO_DATE.format(date), format.format(date));
            assertEquals(DateTimeFormatter.ISO_LOCAL_DATE.format(date),
                    new DateTimeFormat(DateTimeFormatter.ISO_LOCAL_DATE, CellType.LOCAL_DATE, ZoneId.of("UTC")).format(date));
        }
    }

    /**
     * For every value: if the fast parser accepts it, the result must equal what the formatter produces. Also verifies
     * that the full {@link DateTimeFormat} gives the same value or the same kind of failure as the formatter alone.
     */
    private static <T> void assertEquivalent(DateTimeFormatter formatter, CellType cellType, List<String> values,
                                             TemporalQuery<T> query) {
        IsoDateTimeParser fast = IsoDateTimeParser.of(formatter, cellType);
        assertNotNull(fast);
        DateTimeFormat format = new DateTimeFormat(formatter, cellType, ZoneId.systemDefault());
        int fastHits = 0;
        for (String value : values) {
            T expected;
            try {
                expected = query.queryFrom(formatter.parse(value));
            } catch (DateTimeException e) {
                expected = null;
            }
            TemporalAccessor fastResult = fast.parse(value);
            if (fastResult != null) {
                fastHits++;
                assertEquals(expected, query.queryFrom(fastResult), "Fast parser differs for [" + value + "]");
            }
            if (expected == null) {
                assertThrows(DateTimeException.class, () -> query.queryFrom(format.parse(value)), value);
            } else {
                assertEquals(expected, query.queryFrom(format.parse(value)), value);
            }
        }
        assertTrue(fastHits > 0, "Fast parser was never used");
    }
}
