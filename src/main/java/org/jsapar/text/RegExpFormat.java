package org.jsapar.text;

import org.jsapar.model.CellType;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * A formatter that while both parsing and formatting, checks that the value matches supplied regex pattern.
 *
 */
class RegExpFormat implements Format<String> {

    private final Pattern pattern;

    /**
     * @param pattern The regex pattern to match while parsing and formatting.
     */
    RegExpFormat(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * Checks that the string value of the provided object matches the regex pattern
     * @param obj The object to format.
     * @return The destination buffer after appending the value.
     * @throws IllegalArgumentException if string representation of the provided object does not match the regex pattern.
     */
    @Override
    public String format(Object obj) {
        String sValue = String.valueOf(obj);
        if (!this.pattern.matcher(sValue).matches())
            throw new IllegalArgumentException(
                    "Value [" + sValue + "] does not match regular expression [" + this.pattern.pattern() + "].");
        return sValue;
    }

    @Override
    public CellType cellType() {
        return CellType.STRING;
    }

    /**
     * @param source The string source to parse
     * @return The same string as provided as source, if regex pattern matches.
     * @throws ParseException If regex pattern does not match.
     */
    @Override
    public String parse(String source) throws ParseException {
        if (!this.pattern.matcher(source).matches())
            throw new ParseException(
                    "Value [" + source + "] does not match regular expression [" + this.pattern.pattern() + "].", 0);
        return source;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Pattern='" + this.pattern + "'";
    }

}
