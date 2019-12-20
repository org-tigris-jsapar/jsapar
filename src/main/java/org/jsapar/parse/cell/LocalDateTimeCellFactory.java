package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.LocalDateTimeCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Parses date values into {@link Cell} objects
 */
public class LocalDateTimeCellFactory extends AbstractDateTimeCellFactory{

    public LocalDateTimeCellFactory() {
        super(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        return new LocalDateTimeCell(name, LocalDateTime.from((TemporalAccessor) format.parse(value)));
    }

}
