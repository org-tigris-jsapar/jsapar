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
public class BigDecimalCellFactory implements CellFactory {

    /**
     * @param name   The name to give the newly created cell.
     * @param value  The value to parse.
     * @param format The format object to use while parsing.
     * @return A cell of type {@link BigDecimalCell}
     * @throws ParseException If there is an error parsing
     */
    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        if (format == null)
            return new BigDecimalCell(name, new BigDecimal(value));

        return new BigDecimalCell(name, (BigDecimal) format.parse(value));
    }

    /**
     * @param locale The locale to use for the format object.
     * @return A {@link java.text.NumberFormat} instance to use while parsing decimal values.
     */
    @Override
    public Format makeFormat(Locale locale) {
        return Format.ofDecimalInstance(locale);
    }

    /**
     * @param locale  The locale to use for the format object.
     * @param pattern A pattern to use for the format object. If null or empty, default format will be returned.
     * @return A {@link java.text.NumberFormat} instance to use while parsing decimal values.
     */
    @Override
    public Format makeFormat(Locale locale, String pattern) {
        if (locale == null)
            locale = SchemaCellFormat.defaultLocale;
        return Format.ofDecimalInstance(pattern, locale);
    }
}
