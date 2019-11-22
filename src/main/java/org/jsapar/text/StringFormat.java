package org.jsapar.text;

import java.text.ParseException;

public class StringFormat implements Format<String> {
    @Override
    public String parse(String stringValue) throws ParseException {
        return stringValue;
    }

    @Override
    public String format(Object value) {
        return String.valueOf(value);
    }
}
