package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.text.ParseException;

public class USIntegerFormat implements Format<Number> {

    @Override
    public CellType cellType() {
        return CellType.INTEGER;
    }

    @Override
    public Number parse(CharSequence csValue) throws ParseException {
        try {
            return Long.parseLong(csValue, 0, csValue.length(), 10);
        }catch (NumberFormatException e){
            throw new ParseException("Failed to parse integer from value [" + csValue +"]", 0);
        }
    }

    @Override
    public String format(Object value) throws IllegalArgumentException {
        return value.toString();
    }

    @Override
    public String toString() {
        return "USIntegerFormat";
    }
}
