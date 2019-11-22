package org.jsapar.text;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * As of JDK 9 some locales have got new decimal symbols. This is implementation has a work around to also still be
 * able to parse the
 * old format since they are still widely used.
 */
public class DecimalFormat implements Format<BigDecimal> {
    private final NumberFormat numberFormat;

    public DecimalFormat(String pattern, Locale locale) {
        this(pattern, new DecimalFormatSymbols(locale));
    }

    public DecimalFormat(Locale locale) {
        this("0.#", locale);
    }

    public DecimalFormat(String pattern, DecimalFormatSymbols decimalFormatSymbols) {
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
