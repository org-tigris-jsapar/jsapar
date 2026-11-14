package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class DateTimeFormat implements Format<TemporalAccessor> {
    private final DateTimeFormatter formatter;
    private final CellType cellType;
    private final ZoneId zoneId;
    private final IsoDateTimeParser fastParser;
    private final boolean isoLocalDateFormatter;

    public DateTimeFormat(DateTimeFormatter formatter, CellType cellType, ZoneId zoneId) {
        this.formatter = formatter;
        this.zoneId = zoneId;
        if(cellType!=null && !cellType.isTemporal())
            throw new IllegalArgumentException("Only temporal cell types are allowed in DateTimeFormat. " + cellType + " does not parse or" +
                    " compose objects that implements java.time.temporal.Temporal");
        this.cellType = cellType;
        this.fastParser = IsoDateTimeParser.of(formatter, cellType);
        this.isoLocalDateFormatter = formatter == DateTimeFormatter.ISO_DATE || formatter == DateTimeFormatter.ISO_LOCAL_DATE;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public CellType cellType() {
        return cellType;
    }

    @Override
    public TemporalAccessor parse(String stringValue) {
        if (fastParser != null) {
            TemporalAccessor value = fastParser.parse(stringValue);
            if (value != null)
                return value;
        }
        return formatter.parse(stringValue);
    }

    @Override
    public String format(Object value) {
        if(value instanceof Instant)
            return formatter.format(((Instant) value).atZone(zoneId));
        if(isoLocalDateFormatter && value instanceof LocalDate)
            return value.toString(); // Same output as the ISO formatter but much faster.
        if(value instanceof TemporalAccessor)
            return formatter.format((TemporalAccessor) value);
        if(value instanceof Date)
            return formatter.format(((Date)value).toInstant().atZone(zoneId));
        throw new IllegalArgumentException("Unable to format a datetime value from " + value + ". Unsupported type: " + value.getClass());

    }
}
