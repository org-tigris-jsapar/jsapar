package org.jsapar.compose.fixed;

import org.jsapar.JSaParException;
import org.jsapar.compose.CellComposer;
import org.jsapar.compose.ComposeException;
import org.jsapar.model.Cell;
import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by stejon0 on 2016-01-31.
 */
class FixedWidthCellComposer {

    CellComposer cellComposer = new CellComposer();
    private Writer writer;

    public FixedWidthCellComposer(Writer writer) {
        this.writer = writer;
    }

    /**
     * Writes a cell to the supplied writer using supplied fill character.
     *
     * @param cell
     *            The cell to write
     * @param schemaCell
     * @param fillCharacter
     *            The fill character to fill empty spaces.
     * @throws IOException
     * @throws ComposeException
     */
    void compose(Cell cell, FixedWidthSchemaCell schemaCell, char fillCharacter) throws IOException, JSaParException {
        String sValue = cellComposer.format(cell, schemaCell);
        compose(sValue, fillCharacter, schemaCell.getLength(), schemaCell.getAlignment());
    }

    /**
     * Writes a cell to the supplied writer using supplied fill character.
     *
     * @param sValue
     *            The value to write
     * @param fillCharacter
     *            The fill character to fill empty spaces.
     * @param length
     *            The number of characters to write.
     * @param alignment
     *            The alignment of the cell content if the content is smaller than the cell length.
     * @throws IOException
     * @throws ComposeException
     */
    private void compose(String sValue, char fillCharacter, int length, FixedWidthSchemaCell.Alignment alignment)
            throws IOException, ComposeException {
        if (sValue.length() == length) {
            writer.write(sValue);
            return;
        } else if (sValue.length() > length) {
            // If the cell value is larger than the cell length, we have to cut the value.
            alignment.fit(writer, length, sValue);
            return;
        } else {
            // Otherwise use the alignment of the schema.
            int nToFill = length - sValue.length();
            alignment.padd(writer, nToFill, sValue, fillCharacter);
        }
    }


}
