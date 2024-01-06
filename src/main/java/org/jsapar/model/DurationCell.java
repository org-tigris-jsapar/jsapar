package org.jsapar.model;

import java.time.Duration;
import java.time.LocalDate;

public class DurationCell extends AbstractCell<Duration> implements ComparableCell<Duration>{
    /**
     * Creates a cell with a name.
     *
     * @param name     The name of the cell
     * @param value    The value to set for this cell.
     */
    public DurationCell(String name, Duration value) {
        super(name, value, CellType.DURATION);
    }

    /**
     * @param name The name of the cell
     * @return An empty cell of this type.
     */
    public static Cell<LocalDate> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.LOCAL_DATE);
    }

}
