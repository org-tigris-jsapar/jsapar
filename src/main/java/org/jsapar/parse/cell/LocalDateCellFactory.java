package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.LocalDateCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Parses date values into {@link Cell} objects
 */
public class LocalDateCellFactory extends AbstractDateTimeCellFactory {

    /**
     * Creates an instance
     */
    public LocalDateCellFactory() {
        super(DateTimeFormatter.ISO_DATE, CellType.LOCAL_DATE);
    }

    @Override
    public Cell makeCell(String name, String value, Format<TemporalAccessor> format) throws ParseException {
        return new LocalDateCell(name, LocalDate.from(format.parse(value)));
    }

}
