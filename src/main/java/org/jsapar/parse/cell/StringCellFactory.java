package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.StringCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses string values into {@link Cell} objects
 */
public class StringCellFactory implements CellFactory<String> {

    public StringCellFactory() {}

    @Override
    public Cell<String> makeCell(String name, String value, Format<String> format) throws ParseException {
        return new StringCell(name, format.parse(value));
    }

    @Override
    public Format<String> makeFormat(Locale locale) {
        return Format.ofStringInstance();
    }

    @Override
    public Format<String> makeFormat(Locale locale, String pattern) {
        return pattern != null ? Format.ofStringInstance(pattern) : Format.ofStringInstance();
    }

}
