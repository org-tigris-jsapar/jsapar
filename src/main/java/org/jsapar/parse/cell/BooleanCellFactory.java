package org.jsapar.parse.cell;

import org.jsapar.model.BooleanCell;
import org.jsapar.model.Cell;
import org.jsapar.text.BooleanFormat;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

/**
 * Parses boolean values into {@link Cell} objects
 */
public class BooleanCellFactory implements CellFactory {

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        if (format != null)
            return new BooleanCell(name, (Boolean) format.parseObject(value));
        value = value.trim().toLowerCase();
        switch (value) {
        case "true":
        case "on":
        case "1":
        case "yes":
            return new BooleanCell(name, true);
        case "false":
        case "off":
        case "0":
        case "no":
            return new BooleanCell(name, false);
        default:
            throw new ParseException("Failed to parse boolean value from: " + value, 0);
        }
    }

    @Override
    public Format makeFormat(Locale locale) {
        return null;
    }

    @Override
    public Format makeFormat(Locale locale, String pattern) {
        String[] aTrueFalse = pattern.trim().split("\\s*;\\s*");
        if (aTrueFalse.length < 1 || aTrueFalse.length > 2)
            throw new IllegalArgumentException(
                    "Boolean format pattern should only contain two fields separated with ; character");
        return new BooleanFormat(aTrueFalse[0], aTrueFalse.length == 2 ? aTrueFalse[1] : "");
    }
}
