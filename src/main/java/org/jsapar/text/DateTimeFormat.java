package org.jsapar.text;

import org.jsapar.model.CellType;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

class DateTimeFormat implements Format<TemporalAccessor> {
    private final DateTimeFormatter formatter;
    private final CellType cellType;

    DateTimeFormat(DateTimeFormatter formatter, CellType cellType) {
        this.formatter = formatter;
        if(cellType!=null && !cellType.isTemporal())
            throw new IllegalArgumentException("Only temporal cell types are allowed in DateTimeFormat. " + cellType + " does not parse or" +
                    " compose objects that implements java.time.temporal.Temporal");
        this.cellType = cellType;
    }

    @Override
    public CellType cellType() {
        return cellType;
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
