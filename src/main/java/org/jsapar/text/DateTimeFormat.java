package org.jsapar.text;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class DateTimeFormat implements Format<TemporalAccessor> {
    private final DateTimeFormatter formatter;

    public DateTimeFormat(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public TemporalAccessor parse(String stringValue) {
        return formatter.parse(stringValue);
    }

    @Override
    public String format(Object value) {
        if(value instanceof TemporalAccessor)
            return formatter.format((TemporalAccessor) value);
        if(value instanceof Date)
            return formatter.format(((Date)value).toInstant());
        throw new IllegalArgumentException("Unable to format a datetime value from " + value + ". Unsupported type: " + value.getClass());

    }
}
