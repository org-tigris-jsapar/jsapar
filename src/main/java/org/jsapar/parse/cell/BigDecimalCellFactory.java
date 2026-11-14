package org.jsapar.parse.cell;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.model.Cell;
import org.jsapar.schema.SchemaCellFormat;
import org.jsapar.text.Format;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

/**
 * Parses decimal values into {@link Cell} objects
 */
public class BigDecimalCellFactory implements CellFactory<Number> {

    public BigDecimalCellFactory() {}

    /**
     * @param name   The name to give the newly created cell.
     * @param value  The value to parse.
     * @param format The format object to use while parsing.
     * @return A cell of type {@link BigDecimalCell}
     * @throws ParseException If there is an error parsing
     */
    @Override
    public Cell<Number> makeCell(String name, String value, Format<Number> format) throws ParseException {
        return new BigDecimalCell(name, toBigDecimal(format.parse(value), value));
    }

    /**
     * Custom formats may return other number types than BigDecimal.
     */
    private static BigDecimal toBigDecimal(Number number, String value) throws ParseException {
        if (number instanceof BigDecimal bigDecimal)
            return bigDecimal;
        if (number == null)
            return null;
        if (number instanceof Double || number instanceof Float) {
            double d = number.doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d))
                throw new ParseException("Value [" + value + "] is not a valid decimal number.", 0);
            return BigDecimal.valueOf(d);
        }
        if (number instanceof java.math.BigInteger bigInteger)
            return new BigDecimal(bigInteger);
        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte)
            return BigDecimal.valueOf(number.longValue());
        return new BigDecimal(number.toString());
    }

    /**
     * @param locale The locale to use for the format object.
     * @return A {@link java.text.NumberFormat} instance to use while parsing decimal values.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Format<Number> makeFormat(Locale locale) {
        return (Format<Number>)(Format<?>) Format.ofDecimalInstance(locale);
    }

    /**
     * @param locale  The locale to use for the format object.
     * @param pattern A pattern to use for the format object. If null or empty, default format will be returned.
     * @return A {@link java.text.NumberFormat} instance to use while parsing decimal values.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Format<Number> makeFormat(Locale locale, String pattern) {
        if (locale == null)
            locale = SchemaCellFormat.defaultLocale;
        return (Format<Number>)(Format<?>) Format.ofDecimalInstance(pattern, locale);
    }
}
