package org.jsapar.model;

import java.time.LocalDateTime;

/**
 */
public final class LocalDateTimeCell extends TemporalCell<LocalDateTime> implements ComparableCell<LocalDateTime>{

    /**
     * Creates a cell with a name and value.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell
     */
    public LocalDateTimeCell(String name, LocalDateTime value) {
        super(name, value, CellType.LOCAL_DATE_TIME);
    }

    /**
     * @param name The name of the cell
     * @return An empty cell of this type.
     */
    public static Cell<LocalDateTime> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.LOCAL_DATE_TIME);
    }

    @Override
    public Cell<LocalDateTime> cloneWithName(String newName) {
        return new LocalDateTimeCell(newName, getValue());
    }
}
