package org.jsapar.model;

import java.time.temporal.Temporal;

/**
 */
abstract class TemporalCell<T extends Temporal> extends AbstractCell<T> {

    /**
     * Creates a cell with a name.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell.
     * @param cellType The type of the cell.
     */
    TemporalCell(String name, T value, CellType cellType) {
        super(name, value, cellType);
    }
}
