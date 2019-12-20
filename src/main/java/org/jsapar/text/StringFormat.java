package org.jsapar.text;

/**
 * Just passes the string value through untouched while parsing or formatting.
 */
class StringFormat implements Format<String> {

    @Override
    public String parse(String stringValue) {
        return stringValue;
    }

    @Override
    public String format(Object value) {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return "StringFormat";
    }
}
