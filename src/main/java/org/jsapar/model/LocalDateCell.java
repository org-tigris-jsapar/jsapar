package org.jsapar.model;

import java.time.LocalDate;

/**
 */
public final class LocalDateCell extends TemporalCell<LocalDate> implements ComparableCell<LocalDate>{

    /**
     * Creates a cell with a name and value.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell
     */
    public LocalDateCell(String name, LocalDate value) {
        super(name, value, CellType.LOCAL_DATE);
    }

    /**
     * @param name The name of the cell
     * @return An empty cell of this type.
     */
    public static Cell<LocalDate> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.LOCAL_DATE);
    }
}
