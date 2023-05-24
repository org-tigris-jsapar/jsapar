package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Formats and parses using a java.text.Format. Requires that all characters are parsed from the supplied value.
 * @param <T>
 */
public class JavaTextFormat<T> implements Format<T> {
    private final java.text.Format format;
    private final CellType cellType;

    /**
     * @param format The text format to format with
     * @param cellType The type of cells to create while parsing.
     */
    public JavaTextFormat(java.text.Format format, CellType cellType) {
        this.cellType = cellType;
        this.format = format;
    }

    @Override
    public CellType cellType() {
        return cellType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parse(String stringValue) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        T value = (T) format.parseObject(stringValue, pos);
        if (pos.getIndex() < stringValue.length())
            // It is not acceptable to parse only a part of the string. That can happen for instance if there is a space
            // in an integer value.
            throw new java.text.ParseException("Invalid characters found while parsing.", pos.getIndex());

        return value;

    }

    @Override
    public String format(Object value) {
        return format.format(value);
    }
}
