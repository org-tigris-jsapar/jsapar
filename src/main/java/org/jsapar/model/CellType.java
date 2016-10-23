package org.jsapar.model;

import org.jsapar.parse.cell.*;

/**
 * @author stejon0
 *
 */
public enum CellType {

    STRING(new StringCellFactory()),
    DATE(new DateCellFactory()),
    INTEGER(new IntegerCellFactory()),
    BOOLEAN(new BooleanCellFactory()),
    FLOAT(new FloatCellFactory()),
    DECIMAL(new BigDecimalCellFactory()),
    CHARACTER(new CharacterCellFactory()),
    CUSTOM(null);

    private CellFactory cellFactory;

    CellType(CellFactory cellFactory) {
        this.cellFactory = cellFactory;
    }

    public CellFactory getCellFactory() {
        return cellFactory;
    }
}
