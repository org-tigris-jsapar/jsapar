package org.jsapar.text;

import org.jsapar.utils.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * As of JDK 9 some locales have got new decimal symbols. This is implementation has a work around to also still be
 * able to parse the
 * old format since they are still widely used.
 */
public class DecimalFormat implements Format<BigDecimal> {
    private final List<Function<String, String>> mappers = new ArrayList<>(3);
    private final java.text.DecimalFormat textFormat;

    private DecimalFormat(java.text.DecimalFormat decimalFormat, DecimalFormatSymbols decimalFormatSymbols) {
        textFormat = decimalFormat;
        textFormat.setParseBigDecimal(true);

        char groupingSeparator = decimalFormatSymbols.getGroupingSeparator();

        if (Character.isSpaceChar(groupingSeparator)) {
            mappers.add(StringUtils::removeAllSpaces);
        }

        final String minusPrefix = decimalFormat.getNegativePrefix();
        if(!minusPrefix.equals("-")){
            mappers.add(s->replaceNegativePrefix(s, minusPrefix));
        }


        String exp = decimalFormatSymbols.getExponentSeparator();
        if(!exp.equals("E")) {
            mappers.add(s -> replaceExponent(s, exp));
        }
    }

    public DecimalFormat(String pattern, Locale locale) {
        this(pattern, new DecimalFormatSymbols(locale));
    }

    public DecimalFormat(Locale locale) {
        this("0.#", locale);
    }

    public DecimalFormat(String pattern, DecimalFormatSymbols decimalFormatSymbols) {
        this(new java.text.DecimalFormat(pattern, decimalFormatSymbols), decimalFormatSymbols);
    }

    private static String replaceNegativePrefix(String value, String minusPrefix) {
        if( !value.isEmpty() && value.charAt(0)=='-') {
            return minusPrefix + value.substring(1);
        }
        return value;
    }
    private static String replaceExponent(String value, String exp) {
        int pos = value.indexOf('E');
        if(pos > 0)
            return value.substring(0, pos) + exp + value.substring(pos+1);
        return value;
    }

    @Override
    public BigDecimal parse(String stringValue) throws ParseException {
        for (Function<String, String> mapper : mappers) {
            stringValue = mapper.apply(stringValue);
        }
        return (BigDecimal) textFormat.parse(stringValue);
    }

    @Override
    public String format(Object value) {
        return textFormat.format(value);
    }
}
