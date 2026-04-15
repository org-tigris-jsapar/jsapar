package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.IntegerCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses integer values into {@link Cell} objects
 */
public class IntegerCellFactory extends NumberCellFactory {

    public IntegerCellFactory() {}

    @Override
    public Cell<Number> makeCell(String name, String value, Format<Number> format) throws ParseException {
        Number number = super.parseNumber(format, value);
        return new IntegerCell(name, number.longValue());
    }

    @Override
    public Format<Number> makeFormat(Locale locale) {
        return Format.ofIntegerInstance(locale);
    }
}
