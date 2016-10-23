package org.jsapar.parse.cell;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.model.Cell;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by stejon0 on 2016-10-23.
 */
public class BigDecimalCellFactory extends NumberCellFactory implements CellFactory {

    @Override
    public Cell makeCell(String name, String sValue, Format format) throws ParseException {
        if (format == null)
            return new BigDecimalCell(name, new BigDecimal(sValue));

        if (format instanceof DecimalFormat)
            ((DecimalFormat) format).setParseBigDecimal(true);
        return new BigDecimalCell(name, (BigDecimal) parseObject(format, sValue));
    }

    @Override
    public Format makeFormat(Locale locale) {
        Format format = super.makeFormat(locale);
        if (format instanceof DecimalFormat)
            ((DecimalFormat) format).setParseBigDecimal(true);
        return format;
    }

    @Override
    public Format makeFormat(Locale locale, String pattern) {
        if (locale == null)
            locale = Locale.getDefault();
        if (pattern == null || pattern.isEmpty())
            return makeFormat(locale);
        DecimalFormat decFormat = new java.text.DecimalFormat(pattern, new DecimalFormatSymbols(locale));
        decFormat.setParseBigDecimal(true);
        return decFormat;
    }
}
