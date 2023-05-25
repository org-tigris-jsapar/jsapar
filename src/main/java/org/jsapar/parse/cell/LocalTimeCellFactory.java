package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.LocalTimeCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Parses date values into {@link Cell} objects
 */
public class LocalTimeCellFactory extends AbstractDateTimeCellFactory {

    /**
     * Creates an instance
     */
    public LocalTimeCellFactory() {
        super(DateTimeFormatter.ISO_TIME, CellType.LOCAL_TIME);
    }

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        return new LocalTimeCell(name, LocalTime.from((TemporalAccessor) format.parse(value)));
    }

}
