package org.jsapar.parse.fixed;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.ParseException;
import org.jsapar.parse.CellParser;
import org.jsapar.schema.FixedWidthSchemaCell;

public class FixedWidthCellParser extends CellParser {

    private static final String EMPTY_STRING = "";

    public FixedWidthCellParser() {
    }

    /**
     * Builds a Cell from a reader input.
     *
     * @param cellSchema         The schema for the cell.
     * @param reader             The input reader
     * @param trimFillCharacters If true, fill characters are ignored while reading string values. If the cell is
     *                           of any other type, the value is trimmed any way before parsing.
     * @param fillCharacter      The fill character to ignore if trimFillCharacters is true.
     * @param listener
     * @return A Cell filled with the parsed cell value and with the name of this schema cell.
     * @throws IOException
     * @throws ParseException
     */
    public Cell parse(FixedWidthSchemaCell cellSchema,
                      Reader reader,
                      boolean trimFillCharacters,
                      char fillCharacter,
                      ErrorEventListener listener) throws IOException {

        String sValue = parseToString(cellSchema, reader, 0, trimFillCharacters, fillCharacter);
        if(sValue == null) {
            checkIfMandatory(cellSchema, listener);
            return null;
        }
        return parse(cellSchema, sValue, listener);
    }

    /**
     * @param cellSchema The schema of the cell to read.
     * @param reader The reader to read from.
     * @param trimFillCharacters If true, surrounding fill characters are removed.
     * @param fillCharacter The fill character to remove if trimFillCharacters is true.
     * @return The string value of the cell read from the reader at the position pointed to by the offset. Null if end
     * of input stream was reached.
     * @throws IOException If there is a problem while reading the input reader.
     */
    String parseToString(FixedWidthSchemaCell cellSchema,
                         Reader reader,
                         int offset,
                         boolean trimFillCharacters,
                         char fillCharacter) throws IOException {

        int nLength = cellSchema.getLength(); // The actual length

        char[] buffer = new char[nLength];
        int nRead = reader.read(buffer, offset, nLength);
        if (nRead <= 0) {
            if (nLength <= 0)
                return EMPTY_STRING; // It should be empty.
            else{
                return null; // EOF
            }
        }
        nLength = nRead;
        int readOffset = 0;
        if (trimFillCharacters || cellSchema.getCellFormat().getCellType() != CellType.STRING) {
            while (readOffset < nLength && buffer[readOffset] == fillCharacter) {
                readOffset++;
            }
            while (nLength > readOffset && buffer[nLength - 1] == fillCharacter) {
                nLength--;
            }
            nLength -= readOffset;
        }
        return new String(buffer, readOffset, nLength);
    }
}
