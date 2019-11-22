package org.jsapar.text;

import java.text.ParseException;

public interface Format<T> {
    T parse(String stringValue) throws ParseException;
    String format(Object value);
}
