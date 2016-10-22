/**
 *
 */
package org.jsapar.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Format class that can be used to parse or format boolean values based on a true and a false value. For instance, you
 * may have a text where Y is supposed to mean true and N is supposed to mean false. In that case you can create an
 * instance of this class in this way:
 * <pre>
 * {@code
 * BooleanFormat format = new BooleanFormat("Y", "N");
 * assert format.format(true).equals("Y");
 * assert format.format(false).equals("N");
 * assert format.parse("Y");
 * assert !format.parse("N");
 * }
 * </pre>
 */
public class BooleanFormat extends Format {

    String trueValue;
    String falseValue;

    /**
     *
     */
    private static final long serialVersionUID = -281569113302316449L;

    /**
     * Creates a default instance where the text "true" is the true value and "false" is the false value.
     */
    public BooleanFormat() {
        this("true", "false");
    }

    /**
     * Creates a formatter for boolean values.
     *
     * @param trueValue  The string that represents the true value.
     * @param falseValue The string that represents the false value.
     */
    public BooleanFormat(String trueValue, String falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
        if (trueValue.equals(falseValue))
            throw new IllegalArgumentException("true and false values cannot be the same");
    }

    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(Object toFormat, StringBuffer appendToBuffer, FieldPosition pos) {
        int startPos = appendToBuffer.length();
        String value;
        if (toFormat.equals(Boolean.TRUE))
            value = trueValue;
        else if (toFormat.equals(Boolean.FALSE))
            value = falseValue;
        else
            throw new IllegalArgumentException("Only boolean objects can be formatted with this formatter.");

        appendToBuffer.append(value);
        int endPos = appendToBuffer.length();
        pos.setBeginIndex(startPos);
        pos.setEndIndex(endPos);
        return appendToBuffer;
    }

    /**
     * Formats a boolean value.
     *
     * @param value The value to format
     * @return the string value that represents the supplied boolean value.
     */
    public String format(boolean value) {
        return value ? trueValue : falseValue;
    }

    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(String toParse, ParsePosition pos) {
        final boolean ignoreCase = true;
        if (toParse.regionMatches(ignoreCase, pos.getIndex(), trueValue, 0, trueValue.length())) {
            pos.setIndex(pos.getIndex() + trueValue.length());
            return Boolean.TRUE;
        }
        if (toParse.regionMatches(ignoreCase, pos.getIndex(), falseValue, 0, falseValue.length())) {
            pos.setIndex(pos.getIndex() + falseValue.length());
            return Boolean.FALSE;
        }
        pos.setErrorIndex(pos.getIndex());
        return null;
    }

    /**
     * @param toParse The value to parse
     * @return true or false depending on value to parse.
     */
    public boolean parse(String toParse) {
        if (toParse.equalsIgnoreCase(trueValue))
            return true;
        else if (toParse.equalsIgnoreCase(falseValue))
            return false;
        else
            throw new NumberFormatException(
                    "Faled to parse [" + toParse + "] to boolean value only [" + trueValue + "] or [" + falseValue
                            + "] is allowed.");
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[true=" + trueValue + ", false=" + falseValue + "]";
    }

}
