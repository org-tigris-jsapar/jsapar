package org.jsapar.text;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Formats and parses decimal numbers.
 * As of JDK 9 some locales have got new decimal symbols. This is implementation has a work around to also still be
 * able to parse the old format since they are still widely used.
 */
class DecimalFormat implements Format<BigDecimal> {
    private final NumberFormat numberFormat;

    DecimalFormat(String pattern, Locale locale) {
        this(pattern, DecimalFormatSymbols.getInstance(locale));
    }

    DecimalFormat(Locale locale) {
        this("0.#", locale);
    }

    DecimalFormat(String pattern, DecimalFormatSymbols decimalFormatSymbols) {
        java.text.DecimalFormat textFormat = new java.text.DecimalFormat(pattern, decimalFormatSymbols);
        textFormat.setParseBigDecimal(true);
        this.numberFormat = new NumberFormat(textFormat);
    }

    @Override
    public BigDecimal parse(String stringValue) throws ParseException {
        return (BigDecimal) numberFormat.parse(stringValue);
    }

    @Override
    public String format(Object value) {
        return numberFormat.format(value);
    }
}
