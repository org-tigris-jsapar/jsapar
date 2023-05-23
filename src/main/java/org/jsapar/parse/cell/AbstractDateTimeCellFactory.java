package org.jsapar.parse.cell;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 */
public abstract class AbstractDateTimeCellFactory implements CellFactory<TemporalAccessor> {

    private final Format<TemporalAccessor> defaultFormat;

    private AbstractDateTimeCellFactory(Format<TemporalAccessor> defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    AbstractDateTimeCellFactory(DateTimeFormatter defaultFormatter, CellType cellType) {
        this(Format.ofDateTimeInstance(defaultFormatter, cellType));
    }

    Format<TemporalAccessor> getDefaultFormat() {
        return defaultFormat;
    }

    @Override
    public Format<TemporalAccessor> makeFormat(Locale locale) {
        return defaultFormat;
    }

    @Override
    public Format<TemporalAccessor> makeFormat(Locale locale, String pattern) {
        if (pattern == null || pattern.isEmpty())
            return makeFormat(locale);
        return Format.ofDateTimeInstance(locale, pattern);
    }


}
