package org.jsapar.compose.csv;

import com.sun.corba.se.impl.presentation.rmi.DynamicMethodMarshallerImpl;
import org.jsapar.compose.CellComposer;
import org.jsapar.model.Cell;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.SchemaCell;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by stejon0 on 2016-01-30.
 */
public class CsvCellComposer {
    private final static String REPLACE_STRING = "\u00A0"; // non-breaking space
    CellComposer cellComposer = new CellComposer();
    private Writer writer;

    public CsvCellComposer(Writer writer) {
        this.writer = writer;
    }

    /**
     * Writes the cell to the supplied writer, including quote character if necessary.
     * @param cell
     * @param schemaCell
     * @param cellSeparator
     * @param quoteChar
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
                sValue = applyMaxLength(sValue, schemaCell.getMaxLength()-2);
                sValue = quoteChar + sValue + quoteChar;
            }
        }
        writer.write(sValue);
    }

    public String format(Cell cell, CsvSchemaCell schemaCell) {
        String value = cellComposer.format(cell, schemaCell);
        return applyMaxLength(value, schemaCell.getMaxLength());
    }


    /**
     * Same as Groovy String method take(int)
     * @param sValue
     * @param maxLength
     * @return The sValue, truncated if necessary to fit maxLength2
     */
    private String applyMaxLength(String sValue, int maxLength) {
        if(maxLength>0 && sValue.length()>maxLength)
            return sValue.substring(0, Math.max(maxLength, 0));
        else
            return sValue;
    }
}
