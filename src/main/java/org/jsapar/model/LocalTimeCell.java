package org.jsapar.model;

import java.time.LocalTime;

/**
 */
public class LocalTimeCell extends TemporalCell<LocalTime> implements ComparableCell<LocalTime>{

    /**
     * Creates a cell with a name and value.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell
     */
    public LocalTimeCell(String name, LocalTime value) {
        super(name, value, CellType.LOCAL_TIME);
    }

    public static Cell emptyOf(String name) {
        return new EmptyCell(name, CellType.LOCAL_TIME);
    }
}
