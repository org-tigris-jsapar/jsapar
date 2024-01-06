package org.jsapar.parse.cell;

import org.jsapar.model.DurationCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;

public class DurationCellFactory implements CellFactory<Duration> {
    @Override
    public DurationCell makeCell(String name, String value, Format<Duration> format) throws ParseException {
        return new DurationCell(name, Duration.parse(value));
    }

    @Override
    public Format<? extends Duration> makeFormat(Locale locale) {
        return makeFormat(locale, null);
    }

    @Override
    public Format<? extends Duration> makeFormat(Locale locale, String pattern) {
        return Format.ofDurationInstance(locale, pattern);
    }
}
