package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.FloatCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses float values into {@link Cell} objects
 */
public class FloatCellFactory extends NumberCellFactory {

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        final Number number = super.parseNumber(format, value);
        return new FloatCell(name, number instanceof Double ? (Double) number : number.doubleValue());
    }

    @Override
    public Format makeFormat(Locale locale) {
        return Format.ofDoubleInstance(locale);
    }
}
