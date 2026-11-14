package org.jsapar.text.format;

import org.jsapar.model.CellType;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Fast parsing of the most common ISO-8601 shapes without going through {@link DateTimeFormatter}, which allocates a lot
 * for formatters with optional sections such as {@link DateTimeFormatter#ISO_DATE}.
 * <p>
 * Only handles a strict subset of what the corresponding formatter accepts. Returns null for anything else, including
 * invalid values, so that the caller can fall back to the formatter which then produces the result or the error exactly
 * as before.
 */
abstract class IsoDateTimeParser {

    /**
     * @return The value or null if the value could not be parsed by this fast parser.
     */
    abstract TemporalAccessor parse(String value);

    /**
     * @return A fast parser that is equivalent to the formatter for the values it accepts, or null if there is none.
     */
    static IsoDateTimeParser of(DateTimeFormatter formatter, CellType cellType) {
        if (formatter == DateTimeFormatter.ISO_DATE && cellType == CellType.LOCAL_DATE)
            return new LocalDateParser();
        if (formatter == DateTimeFormatter.ISO_TIME && cellType == CellType.LOCAL_TIME)
            return new LocalTimeParser();
        if (formatter == DateTimeFormatter.ISO_DATE_TIME && cellType == CellType.LOCAL_DATE_TIME)
            return new LocalDateTimeParser();
        return null;
    }

    private static final class LocalDateParser extends IsoDateTimeParser {
        @Override
        TemporalAccessor parse(String value) {
            return value.length() == 10 ? parseDate(value) : null;
        }
    }

    private static final class LocalTimeParser extends IsoDateTimeParser {
        @Override
        TemporalAccessor parse(String value) {
            return parseTime(value, 0);
        }
    }

    private static final class LocalDateTimeParser extends IsoDateTimeParser {
        @Override
        TemporalAccessor parse(String value) {
            if (value.length() < 16 || value.charAt(10) != 'T')
                return null;
            LocalDate date = parseDate(value);
            if (date == null)
                return null;
            LocalTime time = parseTime(value, 11);
            return time == null ? null : LocalDateTime.of(date, time);
        }
    }

    /**
     * Parses yyyy-MM-dd at the beginning of the value.
     */
    private static LocalDate parseDate(String value) {
        if (value.charAt(4) != '-' || value.charAt(7) != '-')
            return null;
        int year = digits(value, 0, 4);
        int month = digits(value, 5, 2);
        int day = digits(value, 8, 2);
        if (year < 0 || month < 0 || day < 0)
            return null;
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return null;
        }
    }

    /**
     * Parses HH:mm, HH:mm:ss or HH:mm:ss.f where f is 1-9 digits, starting at offset and ending at end of value.
     */
    private static LocalTime parseTime(String value, int offset) {
        final int length = value.length() - offset;
        if (length < 5 || length == 6 || length == 7 || length == 9 || length > 18)
            return null;
        if (value.charAt(offset + 2) != ':')
            return null;
        int hour = digits(value, offset, 2);
        int minute = digits(value, offset + 3, 2);
        int second = 0;
        int nano = 0;
        if (length >= 8) {
            if (value.charAt(offset + 5) != ':')
                return null;
            second = digits(value, offset + 6, 2);
            if (length > 8) {
                if (value.charAt(offset + 8) != '.')
                    return null;
                int fractionDigits = length - 9;
                int fraction = digits(value, offset + 9, fractionDigits);
                if (fraction < 0)
                    return null;
                nano = fraction;
                for (int i = fractionDigits; i < 9; i++)
                    nano *= 10;
            }
        }
        if (hour < 0 || minute < 0 || second < 0)
            return null;
        try {
            return LocalTime.of(hour, minute, second, nano);
        } catch (DateTimeException e) {
            return null;
        }
    }

    /**
     * @return The non-negative integer value of count ASCII digits starting at offset, or -1 if any character is not
     * an ASCII digit.
     */
    private static int digits(String value, int offset, int count) {
        int result = 0;
        for (int i = offset; i < offset + count; i++) {
            final int digit = value.charAt(i) - '0';
            if (digit < 0 || digit > 9)
                return -1;
            result = result * 10 + digit;
        }
        return result;
    }
}
