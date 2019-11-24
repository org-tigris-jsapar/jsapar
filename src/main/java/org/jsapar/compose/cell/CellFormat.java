package org.jsapar.compose.cell;

import org.jsapar.model.Cell;
import org.jsapar.schema.SchemaCell;

/**
 * Interface for formatting a cell into a string.
 */
public interface CellFormat {

    /**
     * @param cell  The cell to format.
     * @return The String value of the supplied cell.
     */
    String format(Cell cell);


    /**
     * @param schemaCell The schema cell to produce a format for.
     * @return The Cell format instance that is most suitable for the supplied schema cell.
     */
    static CellFormat ofSchemaCell(SchemaCell schemaCell){
        if(schemaCell.isIgnoreWrite())
            return new NothingCellFormat();
        if(schemaCell.getCellFormat().getFormat() != null){
            return new FormatCellFormat(schemaCell.getCellFormat().getFormat(), schemaCell.isDefaultValue() ? schemaCell.getDefaultValue() :"");
        }
        return new StringValueFormat(schemaCell.isDefaultValue() ? schemaCell.getDefaultValue() :"");
    }

}
