package org.jsapar.compose.cell;

import org.jsapar.model.Cell;

class NothingCellFormat implements CellFormat {
    private static final String         EMPTY_STRING          = "";

    @Override
    public String format(Cell cell) {
        return EMPTY_STRING;
    }

}
