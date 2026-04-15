package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.DateCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Parses date values into {@link Cell} objects
 */
public class
DateCellFactory implements CellFactory<Date> {

    public DateCellFactory() {}

    private static final SimpleDateFormat ISO_DATE_FORMAT = DateCell.ISO_DATE_FORMAT;

    @Override
    public Cell<Date> makeCell(String name, String value, Format<Date> format) throws ParseException {
        return new DateCell(name, format.parse(value));
    }

    @Override
    public Format<Date> makeFormat(Locale locale) {
        // If pattern is not specified we always use ISO format because Java default format sucks.
        return Format.ofJavaTextFormat(ISO_DATE_FORMAT);
    }

    @Override
    public Format<Date> makeFormat(Locale locale, String pattern) {
        if(pattern == null || pattern.isEmpty())
            return makeFormat(locale);
        return Format.ofJavaTextFormat(new SimpleDateFormat(pattern));
    }
}
