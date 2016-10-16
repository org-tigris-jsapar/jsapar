package org.jsapar.compose.csv;

import org.jsapar.compose.CellComposer;
import org.jsapar.model.Cell;
import org.jsapar.schema.CsvSchemaCell;

import java.io.IOException;
import java.io.Writer;

/**
 * Composes cell values based on the schema of the cell.
 * Created by stejon0 on 2016-01-30.
 */
class CsvCellComposer {
    private final static String REPLACE_STRING = "\u00A0"; // non-breaking space
    CellComposer cellComposer = new CellComposer();
    private Writer writer;

    public CsvCellComposer(Writer writer) {
        this.writer = writer;
    }

    /**
     * Writes the cell to the supplied writer, including quote character if necessary.
     * @param cell The cell to compose output for.
     * @param schemaCell The schema for the cell.
     * @param cellSeparator The cell separator to use.
     * @param quoteChar The quote character
     * @throws IOException
     */
    void compose(Cell cell, CsvSchemaCell schemaCell, String cellSeparator, char quoteChar) throws IOException {
        String sValue = format(cell, schemaCell);
        if(sValue.isEmpty())
            return;


        if (quoteChar == 0){
            sValue = sValue.replace(cellSeparator, REPLACE_STRING);
        }
        else {
            if (sValue.contains(cellSeparator) || sValue.charAt(0) ==quoteChar){
                sValue = take(sValue, schemaCell.getMaxLength()-2);
                sValue = quoteChar + sValue + quoteChar;
            }
        }
        writer.write(sValue);
    }

    private String format(Cell cell, CsvSchemaCell schemaCell) {
        String value = cellComposer.format(cell, schemaCell);
        return take(value, schemaCell.getMaxLength());
    }


    /**
     * Same as Groovy String method take(int). Returns a String consisting of the first maxLength chars, or else the whole String if it has less then maxLength
     * elements.
     * @param sValue The string to take from.
     * @param maxLength The maximum number of characters to take.
     * @return a String consisting of the first maxLength chars, or else the whole String if it has less then maxLength
     * elements.
     */
    private String take(String sValue, int maxLength) {
        if(maxLength>0 && sValue.length()>maxLength)
            return sValue.substring(0, Math.max(maxLength, 0));
        else
            return sValue;
    }
}
