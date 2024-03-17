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
    public Number parse(CharSequence csValue) throws ParseException {
        try {
            return Double.parseDouble(csValue.toString()); // TODO This might be possible to optimize without allocating String.
        }catch (NumberFormatException e){
            throw new ParseException("Failed to parse float number from value [" + csValue +"]", 0);
        }
    }



    @Override
    public String format(Object value) throws IllegalArgumentException {
        return value.toString();
    }
}
