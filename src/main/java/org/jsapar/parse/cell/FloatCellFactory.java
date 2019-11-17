package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.FloatCell;
import org.jsapar.text.Format;

import java.text.ParseException;

/**
 * Parses float values into {@link Cell} objects
 */
public class FloatCellFactory extends NumberCellFactory {

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        if(format == null)
            return new FloatCell(name, Double.valueOf(value));
        final Number number = super.parseNumber(format, value);
        return new FloatCell(name, number instanceof Double ? (Double) number : number.doubleValue());
    }
}
