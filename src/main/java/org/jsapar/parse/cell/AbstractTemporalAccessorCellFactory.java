package org.jsapar.parse.cell;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 */
abstract class AbstractTemporalAccessorCellFactory implements CellFactory<TemporalAccessor> {

    private final Format<TemporalAccessor> defaultFormat;

    private AbstractTemporalAccessorCellFactory(Format<TemporalAccessor> defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    AbstractTemporalAccessorCellFactory(DateTimeFormatter defaultFormatter, CellType cellType) {
        this(Format.ofTemporalAccessorInstance(defaultFormatter, cellType));
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
        return Format.ofTemporalAccessorInstance(locale, pattern);
    }


}
