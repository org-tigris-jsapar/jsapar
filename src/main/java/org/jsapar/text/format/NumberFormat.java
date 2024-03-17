package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;
import org.jsapar.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Formats and parses number objects.
 * As of JDK 9 some locales have got new decimal symbols. This is implementation has a work-around to also still be
 * able to parse the old format since they are still widely used.
 */
public class NumberFormat extends JavaTextFormat<Number> implements Format<Number> {
    private final List<Function<CharSequence, CharSequence>> mappers = new ArrayList<>(3);

    /**
     * Creates an instance
     * @param pattern  The pattern suited for number format.
     * @param locale The locale to use
     * @param cellType The type of cell to create
     */
    public NumberFormat(String pattern, Locale locale, CellType cellType) {
        this(new java.text.DecimalFormat(pattern, new DecimalFormatSymbols(locale)), cellType);
    }

    /**
     * Creates an instance
     * @param numberFormat The number format to use while paring and formatting.
     * @param cellType The type of cell to create
     */
    public NumberFormat(java.text.NumberFormat numberFormat, CellType cellType) {
        super(numberFormat, cellType);
        if(!cellType.isNumber())
            throw new IllegalArgumentException("Only number cell types are allowed in NumberFormat. " + cellType + " does not parse or" +
                    " compose objects of type Number");
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

    public NumberFormat(Locale locale, CellType cellType) {
        this(java.text.NumberFormat.getInstance(locale), cellType);
    }

    private static CharSequence replaceNegativePrefix(CharSequence value, String minusPrefix) {
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
    public Number parse(CharSequence csValue) throws ParseException {
        for (Function<String, String> mapper : mappers) {
            csValue = mapper.apply(csValue);
        }
        return super.parse(csValue);
    }

    @Override
    public String toString() {
        return "NumberFormat";
    }
}
