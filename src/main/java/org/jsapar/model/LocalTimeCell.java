package org.jsapar.model;

import java.time.LocalTime;

/**
 */
public final class LocalTimeCell extends TemporalCell<LocalTime> implements ComparableCell<LocalTime>{

    /**
     * Creates a cell with a name and value.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell
     */
    public LocalTimeCell(String name, LocalTime value) {
        super(name, value, CellType.LOCAL_TIME);
    }

    /**
     * @param name The name of the cell
     * @return An empty cell of this type.
     */
    public static Cell<LocalTime> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.LOCAL_TIME);
    }

    @Override
    public Cell<LocalTime> cloneWithName(String newName) {
        return new LocalTimeCell(newName, getValue());
    }
}
