package org.jsapar.text;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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
 * You can provide an array of valid true and false strings. In that case the first true and the first false strings
 * are used when formatting.
 *
 */
class BooleanFormat implements Format<Boolean> {

    private final String trueValue;
    private final String falseValue;
    private final String[] optionalTrue;
    private final String[] optionalFalse;
    private boolean ignoreCase;

    /**
     * Creates a default instance where the text "true" is the true value and "false" is the false value.
     * <ul>
     * <li>Optional true values while parsing are: "on", "1", "yes", "y"</li>
     * <li>Optional false values while parsing are: "off", "0", "no", "n"</li>
     * </ul>
     * @param ignoreCase If true, ignores upper/lower case.
     */
    BooleanFormat(boolean ignoreCase) {
        this(new String[]{"true", "on", "1", "yes", "y"}, new String[]{"false", "off", "0", "no", "n"}, true);
        this.ignoreCase = ignoreCase;
    }

    /**
     * Creates a formatter for boolean values.
     *  @param trueValue  The string that represents the true value.
     * @param falseValue The string that represents the false value.
     * @param ignoreCase If true, ignores upper/lower case.
     */
    BooleanFormat(String trueValue, String falseValue, boolean ignoreCase) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
        this.ignoreCase = ignoreCase;
        this.optionalTrue = new String[0];
        this.optionalFalse = new String[0];
        if (trueValue.equals(falseValue))
            throw new IllegalArgumentException("true and false values cannot be the same");
    }

    /**
     * Creates a formatter for boolean values where multiple values are accepted as true or false values. When parsing,
     * the supplied
     * values are tested for equality against the input in following order:
     * <ol>
     *     <li>The first true value.</li>
     *     <li>The first false value.</li>
     *     <li>The rest of the true values are tested in supplied order.</li>
     *     <li>The rest of the false values are tested in supplied order.</li>
     * </ol>
     *  @param trueValues  An array of all of the strings that represents the true value. The first item in the array is used when formatting.
     * @param falseValues An array of all of the strings that represents the false value. The first item in the array is used when formatting.
     * @param ignoreCase If true, ignore case while parsing.
     */
    BooleanFormat(String[] trueValues, String[] falseValues, boolean ignoreCase) {
        assert trueValues != null: "trueValues parameter cannot be null";
        assert falseValues != null: "falseValues parameter cannot be null";
        assert trueValues.length > 0: "trueValues needs to contain at least one value";
        assert falseValues.length > 0: "falseValues needs to contain at least one value";
        this.trueValue = trueValues[0];
        this.falseValue = falseValues[0];
        this.ignoreCase = ignoreCase;
        this.optionalTrue = Arrays.copyOfRange(trueValues, 1, trueValues.length);
        this.optionalFalse = Arrays.copyOfRange(falseValues, 1, falseValues.length);
        if (trueValue.equals(falseValue))
            throw new IllegalArgumentException("true and false values cannot be the same");
    }

    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public String format(Object toFormat) {
        if (toFormat.equals(Boolean.TRUE))
            return trueValue;
        else if (toFormat.equals(Boolean.FALSE))
            return falseValue;
        else if(toFormat instanceof Number){
            return ((Number) toFormat).longValue() == 0L ? falseValue : trueValue;
        }
        else
            throw new IllegalArgumentException("Only boolean objects can be formatted with this formatter.");
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
     * @see java.text.Format#parseNumber(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Boolean parse(String toParse) throws ParseException {
        if(toParse.equals(trueValue))
            return Boolean.TRUE;
        if(toParse.equals(falseValue))
            return Boolean.FALSE;

        if(ignoreCase) {
            if (toParse.regionMatches(true, 0, trueValue, 0, trueValue.length())) {
                return Boolean.TRUE;
            }
            if (toParse.regionMatches(true, 0, falseValue, 0, falseValue.length())) {
                return Boolean.FALSE;
            }
        }
        Boolean theValue = matchValue(Arrays.stream(optionalTrue), Boolean.TRUE, toParse, ignoreCase)
                .orElseGet(() -> matchValue(Arrays.stream(optionalFalse), Boolean.FALSE, toParse, ignoreCase)
                        .orElse(null));
        if(theValue == null)
            throw new ParseException("The value " + toParse + " could not be parsed into a boolean value.", 0);
        return theValue;
    }


    private Optional<Boolean> matchValue(Stream<String> values, Boolean result, String toParse, boolean ignoreCase) {
        return values
                .filter(v->toParse.regionMatches(ignoreCase, 0, v, 0, v.length()))
                .map(v->result)
                .findFirst();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[true=" + trueValue + ", false=" + falseValue + "]";
    }

}
