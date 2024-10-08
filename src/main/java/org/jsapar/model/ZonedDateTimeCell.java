package org.jsapar.model;

import java.time.ZonedDateTime;

/**
 */
public final class ZonedDateTimeCell extends TemporalCell<ZonedDateTime> implements ComparableCell<ZonedDateTime>{

    /**
     * Creates a cell with a name and value.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell
     */
    public ZonedDateTimeCell(String name, ZonedDateTime value) {
        super(name, value, CellType.ZONED_DATE_TIME);
    }

    /**
     * @param name The name of the cell
     * @return An empty cell of this type.
     */
    public static Cell<ZonedDateTime> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.ZONED_DATE_TIME);
    }

    @Override
    public Cell<ZonedDateTime> cloneWithName(String newName) {
        return new ZonedDateTimeCell(newName, getValue());
    }
}
