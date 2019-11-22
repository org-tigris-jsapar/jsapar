package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.StringCell;
import org.jsapar.text.Format;
import org.jsapar.text.RegExpFormat;
import org.jsapar.text.StringFormat;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses string values into {@link Cell} objects
 */
public class StringCellFactory implements CellFactory{
    private StringFormat stringFormatTemplate = new StringFormat();

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        if(format != null)
            value = (String) format.parse(value);
        return new StringCell(name, value);
    }

    @Override
    public Format makeFormat(Locale locale) {
        return stringFormatTemplate;
    }

    @Override
    public org.jsapar.text.Format makeFormat(Locale locale, String pattern) {
        return pattern != null ? new RegExpFormat(pattern) : null;
    }

}
