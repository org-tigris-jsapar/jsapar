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
    void compose(Cell cell, FixedWidthSchemaCell schemaCell, char fillCharacter) throws IOException {
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
            throws IOException{
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
            pad(alignment, writer, nToFill, sValue, fillCharacter);
        }
    }

    /**
     * Padds supplied value in the correct end with the supplied number of characters
     *
     * @param alignment
     * @param writer
     * @param nToFill
     * @param sValue
     * @param fillCharacter
     * @throws IOException
     */
    private void pad(FixedWidthSchemaCell.Alignment alignment,
                     Writer writer,
                     int nToFill,
                     String sValue,
                     char fillCharacter) throws IOException {
        switch (alignment) {

        case LEFT:
            writer.write(sValue);
            fill(writer, fillCharacter, nToFill);
            break;
        case CENTER:
            int nLeft = nToFill / 2;
            fill(writer, fillCharacter, nLeft);
            writer.write(sValue);
            fill(writer, fillCharacter, nToFill - nLeft);
            break;
        case RIGHT:
            fill(writer, fillCharacter, nToFill);
            writer.write(sValue);
            break;
        }
    }

    /**
     * @param writer
     * @param ch
     * @param nSize
     * @throws IOException
     */
    public static void fill(Writer writer, char ch, int nSize) throws IOException {
        for (int i = 0; i < nSize; i++) {
            writer.write(ch);
        }
    }

}