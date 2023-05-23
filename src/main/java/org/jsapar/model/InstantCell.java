package org.jsapar.model;

import java.time.Instant;

public class InstantCell extends TemporalCell<Instant> implements ComparableCell<Instant> {

    /**
     * Creates a cell with a name.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell.
     */
    public InstantCell(String name, Instant value) {
        super(name, value, CellType.INSTANT);
    }
}
