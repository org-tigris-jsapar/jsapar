package org.jsapar.compose.fixed;

import org.jsapar.compose.cell.CellComposer;
import org.jsapar.model.Cell;
import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.io.Writer;

/**
 * Composes fixed width output on cell level.
 */
class FixedWidthCellComposer {

    private CellComposer cellComposer = new CellComposer();
    private Writer writer;

    FixedWidthCellComposer(Writer writer) {
        this.writer = writer;
    }

    /**
     * Writes a cell to the supplied writer using supplied fill character.
     *
     * @param cell
     *            The cell to write
     * @param schemaCell The schema of the cell
     * @throws IOException If there is an error writing characters
     */
    void compose(Cell cell, FixedWidthSchemaCell schemaCell) throws IOException {
        String sValue = cellComposer.format(cell, schemaCell);
        compose(sValue, schemaCell.getPadCharacter(), schemaCell.getLength(), schemaCell.getAlignment());
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
     * @throws IOException If there is an error writing characters
     */
    private void compose(String sValue, char fillCharacter, int length, FixedWidthSchemaCell.Alignment alignment)
            throws IOException{
        if (sValue.length() == length) {
            writer.write(sValue);
        } else if (sValue.length() > length) {
            // If the cell value is larger than the cell length, we have to cut the value.
            fit(alignment, sValue, length);
        } else {
            // Otherwise use the alignment of the schema.
            int nToFill = length - sValue.length();
            pad(alignment, nToFill, sValue, fillCharacter);
        }
    }

    /**
     * Fits supplied value to supplied length, cutting in the correct end.
     * @param alignment The alignment to use.
     * @param sValue The value to write. Needs to be longer than or equal to supplied length
     * @param length The maximum number of characters to write.
     * @throws IOException If there is an error writing characters
     */
    private void fit(FixedWidthSchemaCell.Alignment alignment, String sValue, int length) throws IOException {
        switch (alignment) {
            case LEFT:
                writer.write(sValue, 0, length);
                break;
            case CENTER:
                writer.write(sValue, (sValue.length()-length)/2, length);
                break;
            case RIGHT:
                writer.write(sValue, sValue.length() - length, length);
                break;
        }
    }

    /**
     * Padds supplied value in the correct end with the supplied number of characters
     *
     * @param alignment How to allign the value of the cell.
     * @param nToFill Number of characters to fill
     * @param sValue The value to write
     * @param fillCharacter The fill character to use.
     * @throws IOException If there is an error writing characters
     */
    private void pad(FixedWidthSchemaCell.Alignment alignment,
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
     * Writes specified fill character specified number of times.
     * @param writer The writer to write to
     * @param ch The character to write
     * @param nSize Number of times to write the character
     * @throws IOException If there is an error writing characters
     */
    static void fill(Writer writer, char ch, int nSize) throws IOException {
        for (int i = 0; i < nSize; i++) {
            writer.write(ch);
        }
    }

}
