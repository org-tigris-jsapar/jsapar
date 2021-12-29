package org.jsapar.compose.fixed;

import org.jsapar.compose.cell.CellFormat;
import org.jsapar.compose.fixed.pad.Pad;
import org.jsapar.model.Cell;
import org.jsapar.model.EmptyCell;
import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.io.Writer;

/**
 * Composes fixed width output on cell level.
 */
class FixedWidthCellComposer {

    private final CellFormat           cellFormat;
    private final FixedWidthSchemaCell schemaCell;
    private final Pad                  pad;

    FixedWidthCellComposer(FixedWidthSchemaCell schemaCell) {
        this.cellFormat = CellFormat.ofSchemaCell(schemaCell);
        this.schemaCell = schemaCell;
        this.pad = Pad.ofAlignment(schemaCell.getAlignment(), schemaCell.getPadCharacter(), schemaCell.getLength());
    }


    /**
     * Writes a cell to the supplied writer using supplied fill character.
     *
     * @param writer     The writer to write to.
     * @param cell       The cell to write
     * @throws IOException If there is an error writing characters
     * @return The length of the cell written.
     */
    int compose(Writer writer, Cell<?> cell) throws IOException {
        final String sValue = cellFormat.format(cell);
        compose(writer, sValue, schemaCell.getLength());
        return schemaCell.getLength();
    }

    /**
     * Writes a cell to the supplied writer using supplied fill character.
     *
     *
     * @param writer     The writer to write to.
     * @param sValue
     *            The value to write
     * @param length
     *            The number of characters to write.
     * @throws IOException If there is an error writing characters
     */
    private void compose(Writer writer, String sValue, int length)
            throws IOException{
        if (sValue.length() == length) {
            writer.write(sValue);
        } else if (sValue.length() > length) {
            // If the cell value is larger than the cell length, we have to cut the value.
            pad.fit(writer, sValue);
        } else {
            pad.pad(writer, sValue);
        }
    }

    public String getName() {
        return schemaCell.getName();
    }

    public EmptyCell makeEmptyCell() {
        return schemaCell.makeEmptyCell();
    }
}
