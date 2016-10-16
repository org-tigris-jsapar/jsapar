package org.jsapar.compose;

import org.jsapar.model.Cell;
import org.jsapar.schema.SchemaCell;

/**
 * Utility class that helps composing a cell.
 */
public class CellComposer {

    private static final String         EMPTY_STRING          = "";

    /**
     * Formats a cell to a string according to the rules of this schema.
     *
     * @param cell
     *            The cell to format. If this parameter is null or an empty string, the default
     *            value will be returned or if there is no default value, an empty string will be
     *            returned.
     * @return The formatted value for this cell.
     */
    public String format(Cell cell, SchemaCell schemaCell) {
        if (schemaCell.isIgnoreWrite())
            return EMPTY_STRING;

        if (cell == null) {
            return getDefaultValueOrEmpty(schemaCell);
        }
        String value = cell.getStringValue(schemaCell.getCellFormat().getFormat());
        if (value == null || value.isEmpty()) {
            return getDefaultValueOrEmpty(schemaCell);
        }
        return value;
    }

    /**
     * @return The default value if it is not null or empty string otherwise.
     * @param schemaCell The cell schema to use
     */
    private String getDefaultValueOrEmpty(SchemaCell schemaCell) {
        return schemaCell.getDefaultValue() == null ? EMPTY_STRING : schemaCell.getDefaultValue();
    }

}
