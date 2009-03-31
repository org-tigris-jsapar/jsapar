package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;

import org.jsapar.Cell;

public class CsvSchemaCell extends SchemaCell {
    private final static String replaceString = "\u00A0"; // non-breaking space

    public CsvSchemaCell(String sName) {
        super(sName);
    }

    public CsvSchemaCell() {
        super();
    }

    void output(Cell cell, Writer writer, String cellSeparator, char quoteChar) throws IOException {
        String sValue = format(cell);
        if (quoteChar == 0)
            sValue = sValue.replace(cellSeparator, replaceString);
        else {
            if (sValue.indexOf(cellSeparator) > 0)
                sValue = quoteChar + sValue + quoteChar;
        }
        writer.write(sValue);
    }

    public CsvSchemaCell clone() throws CloneNotSupportedException {
        return (CsvSchemaCell) super.clone();
    }

}
