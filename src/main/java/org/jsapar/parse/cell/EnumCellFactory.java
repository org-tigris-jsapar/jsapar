package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.StringCell;
import org.jsapar.text.EnumFormat;
import org.jsapar.text.RegExpFormat;

import java.text.Format;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Parses string values into {@link Cell} objects
 */
public class EnumCellFactory implements CellFactory{

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
//        if (format == null)
//            format = defaultFormat;
//        return new EnumCell(name, (Enum) format.parseObject(value));
        return null;
    }

    @Override
    public Format makeFormat(Locale locale) {
        return null;
    }

    /**
     * Create a {@link Format} instance for the enum cell type given the locale and a specified pattern.
     * @param locale  For enum cell type, the locale is insignificant.
     * @param pattern A pattern to use for the format object. If null or empty, default format will be returned.
     *                The pattern should contain the true and false values separated with a ; character.
     * Example: pattern="Y;N" will imply that Y represents true and N to represents false.
     * Comparison while parsing is not case sensitive.
     * Multiple true or false values can be specified, separated with the | character but the first value is always the
     * one used while composing. Example: pattern="Y|YES;N|NO"
     * @return A format object to use for the boolean type cells.
     */
    @Override
    public Format makeFormat(Locale locale, String pattern) {
        String[] values = pattern.trim().split("\\s*;\\s*");
        return new EnumFormat(null);
    }

}
