package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.ZonedDateTimeCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Parses zoned date time values into {@link Cell} objects
 */
public class ZonedDateTimeCellFactory extends AbstractDateTimeCellFactory {

    public ZonedDateTimeCellFactory() {
        super(DateTimeFormatter.ISO_OFFSET_DATE_TIME, CellType.ZONED_DATE_TIME);
    }

    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        if (format == null)
            format = getDefaultFormat();

        return new ZonedDateTimeCell(name, ZonedDateTime.from((TemporalAccessor) format.parse(value)));
    }

}
