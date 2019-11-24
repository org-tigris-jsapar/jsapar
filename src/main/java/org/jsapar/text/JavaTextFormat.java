package org.jsapar.text;

import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Formats and parses using a java.text.Format. Requires that all characters are parsed from the supplied value.
 * @param <T>
 */
class JavaTextFormat<T> implements Format<T>{
    private java.text.Format format;

    JavaTextFormat(java.text.Format format) {
        this.format = format;
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
