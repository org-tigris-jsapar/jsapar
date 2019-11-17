package org.jsapar.parse.cell;

import org.jsapar.text.DateTimeFormat;
import org.jsapar.text.Format;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 */
public abstract class AbstractDateTimeCellFactory implements CellFactory {

    private final DateTimeFormat defaultFormat;

    private AbstractDateTimeCellFactory(DateTimeFormat defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    AbstractDateTimeCellFactory(DateTimeFormatter defaultFormatter) {
        this(new DateTimeFormat(defaultFormatter));
    }

    protected Format getDefaultFormat() {
        return defaultFormat;
    }

    @Override
    public Format makeFormat(Locale locale) {
        return defaultFormat;
    }

    @Override
    public org.jsapar.text.Format makeFormat(Locale locale, String pattern) {
        if (pattern == null || pattern.isEmpty())
            return makeFormat(locale);
        return new DateTimeFormat(DateTimeFormatter.ofPattern(pattern, locale));
    }


}
