package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.text.ParseException;

public class CharacterFormat implements Format<Character> {

    @Override
    public CellType cellType() {
        return CellType.CHARACTER;
    }

    @Override
    public Character parse(CharSequence value) throws ParseException {
        if (value.length() > 1) {
            throw new java.text.ParseException("Invalid characters found while parsing single character.", 1);
        } else if (value.length() == 1)
            return value.charAt(0);
        throw new java.text.ParseException("Empty value found while parsing single character.", 0);
    }

    @Override
    public String format(Object value) throws IllegalArgumentException {
        return String.valueOf(value);
    }
}
