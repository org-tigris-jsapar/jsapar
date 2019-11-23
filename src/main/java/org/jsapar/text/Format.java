package org.jsapar.text;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Interface for parsing and formating objects from and to String. Use one of the factory methods to create an instance
 * that fits the purpose.
 * @param <T>
 */
public interface Format<T> {
    T parse(String stringValue) throws ParseException;
    String format(Object value);

    /**
     * @param format The java.text.Format to use.
     * @param <T> The return type.
     * @return An instance that formats and parses using a java.text.Format instance.
     */
    static <T> Format<T>  ofJavaTextFormat(java.text.Format format){
        return new JavaTextFormat<>(format);
    }

    /**
     * @param pattern The parse pattern to use as in {@link java.text.DecimalFormat}. If null, only the locale is uses.
     * @param locale The locale to use.
     * @return An instance that formats and parses numbers.
     */
    static  Format<Number> ofNumberInstance(String pattern, Locale locale){
        if(pattern == null || pattern.isEmpty())
            return ofNumberInstance(locale);
        return new NumberFormat(pattern, locale);
    }

    /**
     * @param locale The locale to use.
     * @return An instance that formats and parses numbers.
     */
    static  Format<Number> ofNumberInstance(Locale locale){
        return new NumberFormat(locale);
    }

    /**
     * @param locale The locale to use.
     * @return An instance that formats and parses integer numbers.
     */
    static  Format<Number>  ofIntegerInstance(Locale locale){
        return new NumberFormat(java.text.NumberFormat.getIntegerInstance(locale));
    }

    /**
     * @param formatter  The formatter to use while formatting and parsing.
     * @return An instance that formats and parses date time objects.
     */
    static  Format<TemporalAccessor>  ofDateTimeInstance(DateTimeFormatter formatter){
        return new DateTimeFormat(formatter);
    }

    /**
     * @param locale  The locale to use
     * @param pattern The date pattern to use according to {@link DateTimeFormatter}. Required.
     * @return An instance that formats and parses date time objects.
     */
    static  Format<TemporalAccessor>  ofDateTimeInstance(Locale locale, String pattern){
        return new DateTimeFormat(DateTimeFormatter.ofPattern(pattern, locale));
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
     * @return An instance that can be used to parse and format <a href="https://www.ibm.com/support/knowledgecenter/en/SSLVMB_24.0.0/spss/base/syn_data_list_implied_decimal_positions.html">implied decimals</a>.
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

}
