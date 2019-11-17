package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.schema.SchemaCellFormat;
import org.jsapar.text.Format;
import org.jsapar.text.NumberFormat;

import java.text.ParseException;
import java.util.Locale;

/**
 * Abstract base class for parsing number values into {@link Cell} objects
 */
public abstract class NumberCellFactory implements CellFactory{

    @Override
    public Format makeFormat(Locale locale) {
        return new NumberFormat(locale);
    }

    protected Number parseNumber(Format format, String value) throws ParseException {
        return  (Number) format.parse(value);
    }

    @Override
    public org.jsapar.text.Format makeFormat(Locale locale, String pattern) {
        if (locale == null)
            locale = SchemaCellFormat.defaultLocale;
        if (pattern != null && !pattern.isEmpty())
            return new NumberFormat(pattern, locale);
        else
            return makeFormat(locale);
    }
}
