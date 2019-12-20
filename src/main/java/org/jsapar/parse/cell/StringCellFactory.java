package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.StringCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses string values into {@link Cell} objects
 */
public class StringCellFactory implements CellFactory{

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        return new StringCell(name, (String) format.parse(value));
    }

    @Override
    public Format makeFormat(Locale locale) {
        return Format.ofStringInstance();
    }

    @Override
    public org.jsapar.text.Format makeFormat(Locale locale, String pattern) {
        return pattern != null ? Format.ofStringInstance(pattern) : Format.ofStringInstance();
    }

}
