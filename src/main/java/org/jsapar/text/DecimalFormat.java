package org.jsapar.text;

import org.jsapar.utils.StringUtils;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class DecimalFormat<T extends Number> extends JavaTextFormat<T> {
    private List<Function<String, String>> mappers = new ArrayList<>();

    private DecimalFormat(java.text.DecimalFormat decimalFormat, DecimalFormatSymbols decimalFormatSymbols) {
        super(decimalFormat);
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
    public T parse(String stringValue) throws ParseException {
        for (Function<String, String> mapper : mappers) {
            stringValue = mapper.apply(stringValue);
        }
        return super.parse(stringValue);
    }
}
