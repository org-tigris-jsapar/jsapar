package org.jsapar.text;

import java.text.ParseException;

public class JavaTextFormat<T> implements Format<T>{
    private java.text.Format format;

    public JavaTextFormat(java.text.Format format) {
        this.format = format;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parse(String stringValue) throws ParseException {
        return (T) format.parseObject(stringValue);
    }

    @Override
    public String format(Object value) {
        return format.format(value);
    }
}
