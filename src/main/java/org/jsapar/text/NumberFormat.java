package org.jsapar.text;

import org.jsapar.utils.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Formats and parses number objects.
 * As of JDK 9 some locales have got new decimal symbols. This is implementation has a work around to also still be
 * able to parse the old format since they are still widely used.
 */
class NumberFormat extends JavaTextFormat<Number> implements Format<Number> {
    private final List<Function<String, String>> mappers = new ArrayList<>(3);

    NumberFormat(String pattern, Locale locale) {
        this(new java.text.DecimalFormat(pattern, new DecimalFormatSymbols(locale)));
    }

    NumberFormat(java.text.NumberFormat numberFormat) {
        super(numberFormat);
        if(numberFormat instanceof java.text.DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat) numberFormat;

            char groupingSeparator = decimalFormat.getDecimalFormatSymbols().getGroupingSeparator();

            if (Character.isSpaceChar(groupingSeparator)) {
                mappers.add(StringUtils::removeAllSpaces);
            }

            final String minusPrefix = decimalFormat.getNegativePrefix();
            if (!minusPrefix.equals("-")) {
                mappers.add(s -> replaceNegativePrefix(s, minusPrefix));
            }


            String exp = decimalFormat.getDecimalFormatSymbols().getExponentSeparator();
            if (!exp.equals("E")) {
                mappers.add(s -> replaceExponent(s, exp));
            }
        }
    }

    NumberFormat(Locale locale) {
        this(java.text.NumberFormat.getInstance(locale));
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
    public Number parse(String stringValue) throws ParseException {
        for (Function<String, String> mapper : mappers) {
            stringValue = mapper.apply(stringValue);
        }
        return super.parse(stringValue);
    }

}
