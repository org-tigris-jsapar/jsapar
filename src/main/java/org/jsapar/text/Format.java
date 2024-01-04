package org.jsapar.text;

import org.jsapar.model.CellType;
import org.jsapar.text.format.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Interface for parsing and formatting objects from and to String. Use one of the static factory methods of this interface
 * to create an instance that fits the purpose. For instance, you can use {@link #ofJavaTextFormat(java.text.Format)} in order
 * to wrap a java.text.Format instance.
 * @param <T> The value type to format/parse into.
 */
public interface Format<T> {

    /**
     * @return The type of cell parsed and produced by this format by default.
     */
    CellType cellType();

    /**
     * Parses an instance of type T from the supplied string.
     * @param stringValue The string value to parse from.
     * @return A parsed object.
     * @throws ParseException If parsing fails for any reason.
     */
    T parse(String stringValue) throws ParseException;

    /**
     * Formats supplied object into string.
     * @param value The value to format. Usually it should be of the type T but this method is a bit more generous and
     *              may accept other type of objects on occasion.
     * @return A string that is formatted from the supplied object.
     * @throws IllegalArgumentException If the supplied value is of an unsupported type.
     */
    String format(Object value) throws IllegalArgumentException;

    /**
     * @param format The java.text.Format to use.
     * @param <T> The return type.
     * @return An instance that formats and parses using a java.text.Format instance.
     */
    static <T> Format<T>  ofJavaTextFormat(java.text.Format format){
        return new JavaTextFormat<>(format, CellType.STRING);
    }

    /**
     * @param ignoreCase If true, upper/lower case is ignored.
     * @return An instance that formats and parses Boolean in a default manner using 'true' or the true value and 'false' for false.
     */
    static Format<Boolean> ofBooleanInstance(boolean ignoreCase){
        return new BooleanFormat(ignoreCase);
    }

    /**
     * @param trueValue  The string value to use for true.
     * @param falseValue The string value to use for false.
     * @param ignoreCase If true, upper/lower case is ignored.
     * @return An instance that formats and parses Boolean.
     */
    static Format<Boolean> ofBooleanInstance(String trueValue, String falseValue, boolean ignoreCase){
        return new BooleanFormat(trueValue, falseValue, ignoreCase);
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
     * @param trueValues  An array of all of the strings that represents the true value. The first item in the array is used when formatting.
     * @param falseValues An array of all of the strings that represents the false value. The first item in the array is used when formatting.
     * @param ignoreCase If true, upper/lower case is ignored.
     * @return An instance that formats and parses Boolean.
     */
    static Format<Boolean> ofBooleanInstance(String[] trueValues, String[] falseValues, boolean ignoreCase){
        return new BooleanFormat(trueValues, falseValues, ignoreCase);
    }


    /**
     * Create a {@link Format} instance for the boolean cell type given the locale and a specified pattern.
     * @param pattern A pattern to use for the format object. If null or empty, default format will be returned.
     *                The pattern should contain the true and false values separated with a ';' character.
     * Example: pattern="Y;N" will imply that Y represents true and N to represents false.
     * Comparison while parsing is not case-sensitive.
     * Multiple true or false values can be specified, separated with the | character but the first value is always the
     * one used while composing. Example: pattern="Y|YES;N|NO"
     * @param ignoreCase If true, upper/lower case is ignored.
     * @return An instance that formats and parses Boolean.
     */
    static Format<Boolean> ofBooleanInstance(String pattern, boolean ignoreCase){
        if(pattern == null || pattern.isEmpty())
            return ofBooleanInstance(ignoreCase);
        String[] aTrueFalse = pattern.trim().split("\\h*;\\h*");
        if (aTrueFalse.length < 1 || aTrueFalse.length > 2)
            throw new IllegalArgumentException(
                    "Boolean format pattern should only contain two fields separated with ; character");
        return ofBooleanInstance(aTrueFalse[0].split("\\h*\\|\\h*"), aTrueFalse.length == 2 ? aTrueFalse[1].split("\\h*\\|\\h*") : new String[]{""}, ignoreCase);
    }



    /**
     * @param pattern The parse pattern to use as in {@link java.text.DecimalFormat}. If null, only the locale is uses.
     * @param locale The locale to use.
     * @param cellType The type of cell to produce and parse.
     * @return An instance that formats and parses numbers.
     */
    static  Format<Number> ofNumberInstance(String pattern, Locale locale, CellType cellType){
        if(pattern == null || pattern.isEmpty())
            return ofNumberInstance(locale);
        return new NumberFormat(pattern, locale, cellType);
    }

    /**
     * @param pattern The parse pattern to use as in {@link java.text.DecimalFormat}. If null, only the locale is uses.
     * @param locale The locale to use.
     * @return An instance that formats and parses float and double numbers.
     */
    static  Format<Number> ofNumberInstance(String pattern, Locale locale){
        return ofNumberInstance(pattern, locale, CellType.FLOAT);
    }

    /**
     * @param locale The locale to use.
     * @param cellType The type of cell to produce and parse.
     * @return An instance that formats and parses numbers.
     */
    static  Format<Number> ofNumberInstance(Locale locale, CellType cellType){
        return new NumberFormat(locale, cellType);
    }

    /**
     * @param locale The locale to use.
     * @return An instance that formats and parses float and double numbers.
     */
    static  Format<Number> ofNumberInstance(Locale locale){
        return ofNumberInstance(locale, CellType.FLOAT);
    }

    /**
     * @param locale The locale to use.
     * @return An instance that formats and parses double precision numbers.
     */
    static  Format<Number>  ofDoubleInstance(Locale locale){
        if(locale == Locale.US)
            return new USDoubleFormat();
        return ofNumberInstance(locale);
    }

    /**
     * @param locale The locale to use.
     * @return An instance that formats and parses integer numbers.
     */
    static  Format<Number>  ofIntegerInstance(Locale locale){
        if(locale == Locale.US)
            return new USIntegerFormat();
        final java.text.NumberFormat intFormat = java.text.NumberFormat.getIntegerInstance(locale);
        intFormat.setGroupingUsed(false);
        return new NumberFormat(intFormat, CellType.FLOAT);
    }

    /**
     * @param formatter The date formatter to format with
     * @param zoneId The time zone id that is used when parsing and composing from an Instant.
     * @return An instance that formats and parses Instant objects.
     * @since 2.3
     */
    static  Format<TemporalAccessor>  ofInstantInstance(DateTimeFormatter formatter, ZoneId zoneId){
        return new DateTimeFormat(formatter, CellType.INSTANT, zoneId);
    }    
    /**
     * @param formatter  The formatter to use while formatting and parsing.
     * @param cellType The type of cell to parse and produce
     * @return An instance that formats and parses date time objects.
     */
    static Format<TemporalAccessor>  ofDateTimeInstance(DateTimeFormatter formatter, CellType cellType){
        // TODO When not given time zone explicitly, it should be given by the parser instead of system default
        return new DateTimeFormat(formatter, cellType, ZoneId.systemDefault());
    }    /**
     * @param formatter  The formatter to use while formatting and parsing.
     * @return An instance that formats and parses date time objects of type local date time.
     */
    static  Format<TemporalAccessor>  ofDateTimeInstance(DateTimeFormatter formatter){
        return ofDateTimeInstance(formatter, CellType.LOCAL_DATE_TIME);
    }

    /**
     * @param locale  The locale to use
     * @param pattern The date pattern to use according to {@link DateTimeFormatter}. Required.
     * @param cellType The type of cell to parse and produce.
     * @return An instance that formats and parses date time objects.
     */
    static  Format<TemporalAccessor>  ofDateTimeInstance(Locale locale, String pattern, CellType cellType){
        return ofDateTimeInstance(DateTimeFormatter.ofPattern(pattern, locale), cellType);
    }    /**
     * @param locale  The locale to use
     * @param pattern The date pattern to use according to {@link DateTimeFormatter}. Required.
     * @return An instance that formats and parses date time objects of type local date time.
     */
    static  Format<TemporalAccessor>  ofDateTimeInstance(Locale locale, String pattern){
        return ofDateTimeInstance(locale, pattern, CellType.LOCAL_DATE_TIME);
    }

    /**
     * @param pattern The parse pattern to use as in {@link java.text.DecimalFormat}. If null, only the locale is uses.
     * @param locale The locale to use.
     * @return An instance that formats and parses decimal numbers into {@link BigDecimal}.
     */
    static Format<BigDecimal> ofDecimalInstance(String pattern, Locale locale){
        if(pattern == null || pattern.isEmpty())
            return ofDecimalInstance(locale);
        return new DecimalFormat(pattern, locale);
    }

    /**
     * @param locale The locale to use.
     * @return An instance that formats and parses decimal numbers into {@link BigDecimal}.
     */
    static Format<BigDecimal> ofDecimalInstance(Locale locale){
        return new DecimalFormat(locale);
    }

    /**
     * @param decimals The number of decimals to imply
     * @return An instance that can be used to parse and format <i>implied decimals</i>.
     * The text representation is always an integer but when parsing the decimal point is shifted left and when composing it is shifted right.
     */
    static Format<BigDecimal> ofImpliedDecimalInstance(int decimals){
        return new ImpliedDecimalFormat(decimals);
    }

    /**
     * @return An instance that just does {@link String#valueOf(Object)} when formatting.
     */
    static Format<String> ofStringInstance(){
        return new StringFormat();
    }

    /**
     * @param pattern The regular expression to check while both parsing and formatting.
     * @return An instance that checks that the text representation matches the supplied regular expression. If null or empty, no check will be made.
     * @see java.util.regex.Pattern
     */
    static Format<String> ofStringInstance(String pattern){
        if(pattern == null || pattern.isEmpty())
            return ofStringInstance();
        return new RegExpFormat(pattern);
    }

    /**
     * @return An instance that formats characters.
     */
    static Format<Character> ofCharacterInstance() {
        return new CharacterFormat();
    }


}
