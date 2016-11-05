package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.utils.StringUtils;

import java.text.*;
import java.util.Locale;

/**
 * Abstract base class for parsing number values into {@link Cell} objects
 */
public abstract class NumberCellFactory implements CellFactory{

    @Override
    public Format makeFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }

    protected Number parseObject(Format format, String value) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        value = adjustValueForSpaces(value, format);
        Number number= (Number) format.parseObject(value, pos);

        if (pos.getIndex() < value.length())
            // It is not acceptable to parse only a part of the string. That can happen for instance if there is a space
            // in an integer value.
            throw new java.text.ParseException("Invalid characters found while parsing number.", pos.getIndex());
        return number;
    }

    private String adjustValueForSpaces(String sValue, Format format) {
        if (format != null && format instanceof DecimalFormat) {
            // This is necessary because some locales (e.g. swedish)
            // have non breakable space as thousands grouping character. Naturally
            // we want to remove all space characters including the non breakable.
            DecimalFormat decFormat = (DecimalFormat) format;
            char groupingSeparator = decFormat.getDecimalFormatSymbols().getGroupingSeparator();
            if (Character.isSpaceChar(groupingSeparator)) {
                sValue = StringUtils.removeAllSpaces(sValue);
            }
        }
        return sValue;
    }

    @Override
    public Format makeFormat(Locale locale, String pattern) {
        if (locale == null)
            locale = Locale.getDefault();
        if (pattern != null && !pattern.isEmpty())
            return new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
        else
            return makeFormat(locale);
    }
}
