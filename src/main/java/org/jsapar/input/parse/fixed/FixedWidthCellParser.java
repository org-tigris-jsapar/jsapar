package org.jsapar.input.parse.fixed;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.input.ParseException;
import org.jsapar.input.LineEventListener;
import org.jsapar.input.parse.CellParser;
import org.jsapar.schema.FixedWidthSchemaCell;

public class FixedWidthCellParser extends CellParser {

    private static final String EMPTY_STRING = "";

    public FixedWidthCellParser() {
    }

    /**
     * Builds a Cell from a reader input.
     * 
     * @param reader
     *            The input reader
     * @param trimFillCharacters
     *            If true, fill characters are ignored while reading string values. If the cell is
     *            of any other type, the value is trimmed any way before parsing.
     * @param fillCharacter
     *            The fill character to ignore if trimFillCharacters is true.
     * @param nLineNumber
     * @param listener
     * @return A Cell filled with the parsed cell value and with the name of this schema cell.
     * @throws IOException
     * @throws ParseException
     */
    Cell parse(FixedWidthSchemaCell cellSchema,
               Reader reader,
               boolean trimFillCharacters,
               char fillCharacter,
               LineEventListener listener,
               long nLineNumber) throws IOException, ParseException {

        int nOffset = 0;
        int nLength = cellSchema.getLength(); // The actual length

        char[] buffer = new char[nLength];
        int nRead = reader.read(buffer, 0, nLength);
        if (nRead <= 0) {
            checkIfMandatory(cellSchema, listener, nLineNumber);
            if (cellSchema.getLength() <= 0)
                return parse(cellSchema, EMPTY_STRING);
            else{
                return null;
            }
        }
        nLength = nRead;
        if (trimFillCharacters || cellSchema.getCellFormat().getCellType() != CellType.STRING) {
            while (nOffset < nLength && buffer[nOffset] == fillCharacter) {
                nOffset++;
            }
            while (nLength > nOffset && buffer[nLength - 1] == fillCharacter) {
                nLength--;
            }
            nLength -= nOffset;
        }
        return parse(cellSchema, new String(buffer, nOffset, nLength), listener, nLineNumber);
    }    
}
