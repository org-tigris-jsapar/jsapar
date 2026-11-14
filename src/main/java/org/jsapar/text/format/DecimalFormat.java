package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Formats and parses decimal numbers.
 * As of JDK 9 some locales have got new decimal symbols. This is implementation has a workaround to also still be
 * able to parse the old format since they are still widely used.
 */
public class DecimalFormat implements Format<BigDecimal> {
    private final NumberFormat numberFormat;
    /**
     * True if this format has no pattern and the locale uses plain ASCII symbols. Then common values can be parsed and
     * formatted directly by {@link BigDecimal} with exactly the same result as the java.text.DecimalFormat.
     */
    private final boolean plain;

    public DecimalFormat(String pattern, Locale locale) {
        this(pattern, DecimalFormatSymbols.getInstance(locale));
    }

    public DecimalFormat(Locale locale) {
        this(unlimitedFractionDigits(new java.text.DecimalFormat("0.#", DecimalFormatSymbols.getInstance(locale))), true);
    }

    DecimalFormat(String pattern, DecimalFormatSymbols decimalFormatSymbols) {
        this(new java.text.DecimalFormat(pattern, decimalFormatSymbols), false);
    }

    private DecimalFormat(java.text.DecimalFormat textFormat, boolean defaultPattern) {
        textFormat.setParseBigDecimal(true);
        this.numberFormat = new NumberFormat(textFormat, CellType.FLOAT);
        DecimalFormatSymbols symbols = textFormat.getDecimalFormatSymbols();
        this.plain = defaultPattern
                && symbols.getDecimalSeparator() == '.'
                && symbols.getMinusSign() == '-'
                && symbols.getZeroDigit() == '0'
                && textFormat.getNegativePrefix().equals("-")
                && textFormat.getNegativeSuffix().isEmpty()
                && textFormat.getPositivePrefix().isEmpty()
                && textFormat.getPositiveSuffix().isEmpty();
    }

    /**
     * Without an explicit pattern, all decimals of the value must be kept when composing. The pattern "0.#" alone would
     * round to one decimal.
     */
    private static java.text.DecimalFormat unlimitedFractionDigits(java.text.DecimalFormat textFormat) {
        textFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
        return textFormat;
    }

    @Override
    public CellType cellType() {
        return CellType.DECIMAL;
    }

    @Override
    public BigDecimal parse(String stringValue) throws ParseException {
        if (plain && isPlainDecimal(stringValue))
            return new BigDecimal(stringValue);
        return (BigDecimal) numberFormat.parse(stringValue);
    }

    /**
     * @return True if the value matches -?[0-9]*(\.[0-9]*)? with at least one digit. For those values,
     * new BigDecimal(value) gives exactly the same value and scale as the java.text.DecimalFormat.
     */
    private static boolean isPlainDecimal(String value) {
        final int length = value.length();
        int i = (length > 0 && value.charAt(0) == '-') ? 1 : 0;
        boolean digitFound = false;
        boolean pointFound = false;
        for (; i < length; i++) {
            final char c = value.charAt(i);
            if (c >= '0' && c <= '9')
                digitFound = true;
            else if (c == '.' && !pointFound)
                pointFound = true;
            else
                return false;
        }
        return digitFound;
    }

    @Override
    public String format(Object value) {
        if (plain && value instanceof BigDecimal)
            return ((BigDecimal) value).stripTrailingZeros().toPlainString();
        return numberFormat.format(value);
    }
}
