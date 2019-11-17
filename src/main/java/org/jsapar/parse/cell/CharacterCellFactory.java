package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CharacterCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses character values into {@link Cell} objects
 */
public class CharacterCellFactory implements CellFactory {

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        if (format == null) {
            if (value.length() > 1) {
                throw new java.text.ParseException("Invalid characters found while parsing single character.", 1);
            } else if (value.length() == 1)
                return new CharacterCell(name, value.charAt(0));
            throw new java.text.ParseException("Empty value found while parsing single character.", 0);
        }
        final Character characterValue = (Character) format.parse(value);
        return new CharacterCell(name, characterValue);
    }

    @Override
    public Format makeFormat(Locale locale) {
        return null;
    }

    @Override
    public org.jsapar.text.Format makeFormat(Locale locale, String pattern) {
        return null;
    }
}
