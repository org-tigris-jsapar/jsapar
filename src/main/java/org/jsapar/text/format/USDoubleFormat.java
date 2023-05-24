package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.text.ParseException;

public class USDoubleFormat implements Format<Number> {
    @Override
    public CellType cellType() {
        return CellType.FLOAT;
    }

    @Override
    public Number parse(String stringValue) throws ParseException {
        try {
            return Double.valueOf(stringValue);
        }catch (NumberFormatException e){
            throw new ParseException("Failed to parse float number from value [" + stringValue+"]", 0);
        }
    }

    @Override
    public String format(Object value) throws IllegalArgumentException {
        return value.toString();
    }
}
