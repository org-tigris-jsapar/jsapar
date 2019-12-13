package org.jsapar.compose.cell;

import org.jsapar.model.Cell;

/**
 * CellFormat that uses the {@link Cell#getStringValue()}
 */
class StringValueFormat implements CellFormat {
    private final String defaultValue;

    StringValueFormat(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String format(Cell cell) {
        return cell.isEmpty() ? defaultValue : cell.getStringValue();
    }
}
