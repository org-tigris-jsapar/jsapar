package org.jsapar.model;

import org.jsapar.parse.cell.*;

/**
 * Enum used to be able to denote the type of a cell.
 *
 */
public enum CellType {

    STRING(new StringCellFactory(), false),
    DATE(new DateCellFactory(), false),
    INTEGER(new IntegerCellFactory(), true),
    BOOLEAN(new BooleanCellFactory(), true),
    FLOAT(new FloatCellFactory(), false),
    DECIMAL(new BigDecimalCellFactory(), false),
    CHARACTER(new CharacterCellFactory(), true),
    CUSTOM(null, false),
    LOCAL_DATE_TIME(new LocalDateTimeCellFactory(), false),
    LOCAL_DATE(new LocalDateCellFactory(), false),
    LOCAL_TIME(new LocalTimeCellFactory(), false),
    ZONED_DATE_TIME(new ZonedDateTimeCellFactory(), false);

    private CellFactory cellFactory;
    private boolean atomic;

    CellType(CellFactory cellFactory, boolean atomic) {
        this.cellFactory = cellFactory;
        this.atomic = atomic;
    }

    public CellFactory getCellFactory() {
        return cellFactory;
    }

    /**
     * @return True if this type is atomic in a sense that it cannot contain cell or line separator character.
     */
    public boolean isAtomic() {
        return atomic;
    }
}
