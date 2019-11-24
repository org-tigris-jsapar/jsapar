package org.jsapar.compose.cell;

import org.jsapar.model.Cell;
import org.jsapar.text.Format;

/**
 * Cell format that uses a {@link Format}
 */
class FormatCellFormat implements CellFormat {
    private final Format format;
    private final String defaultValue;

    FormatCellFormat(Format format, String defaultValue) {
        this.format = format;
        this.defaultValue = defaultValue;
    }

    @Override
    public String format(Cell cell) {
        return cell.isEmpty() ? defaultValue : format.format(cell.getValue());
    }
}
