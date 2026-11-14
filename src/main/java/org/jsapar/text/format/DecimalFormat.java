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

    public DecimalFormat(String pattern, Locale locale) {
        this(pattern, DecimalFormatSymbols.getInstance(locale));
    }

    public DecimalFormat(Locale locale) {
        this(unlimitedFractionDigits(new java.text.DecimalFormat("0.#", DecimalFormatSymbols.getInstance(locale))));
    }

    DecimalFormat(String pattern, DecimalFormatSymbols decimalFormatSymbols) {
        this(new java.text.DecimalFormat(pattern, decimalFormatSymbols));
    }

    private DecimalFormat(java.text.DecimalFormat textFormat) {
        textFormat.setParseBigDecimal(true);
        this.numberFormat = new NumberFormat(textFormat, CellType.FLOAT);
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
        return (BigDecimal) numberFormat.parse(stringValue);
    }

    @Override
    public String format(Object value) {
        return numberFormat.format(value);
    }
}
