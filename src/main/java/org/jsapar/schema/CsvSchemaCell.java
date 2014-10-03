package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;

import org.jsapar.Cell;

public class CsvSchemaCell extends SchemaCell {
    private final static String replaceString = "\u00A0"; // non-breaking space

    private int maxLength=-1;
    
    public CsvSchemaCell(String sName) {
        super(sName);
    }

    public CsvSchemaCell(String sName, SchemaCellFormat cellFormat) {
        super(sName, cellFormat);
    }
    
    public CsvSchemaCell() {
        super();
    }

    void output(Cell cell, Writer writer, String cellSeparator, char quoteChar) throws IOException {
        String sValue = format(cell);
        if(sValue.isEmpty())
            return;
        
        
        if (quoteChar == 0){
            sValue = sValue.replace(cellSeparator, replaceString);
            sValue = applyMaxLength(sValue, getMaxLength());
        }
        else {
            if (sValue.contains(cellSeparator) || sValue.charAt(0) ==quoteChar){
                sValue = applyMaxLength(sValue, getMaxLength()-2);
                sValue = quoteChar + sValue + quoteChar;
            }
        }
        writer.write(sValue);
    }

    private String applyMaxLength(String sValue, int maxLength2) {
        if(isMaxLength() && sValue.length()>maxLength2)
            return sValue.substring(0, Math.max(maxLength2, 0));
        else
            return sValue;
    }

    @Override
    public CsvSchemaCell clone() {
        return (CsvSchemaCell) super.clone();
    }

    /**
     * The maximum number of characters that are read or written to/from the cell.
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * The maximum number of characters that are read or written to/from the cell.
     * Set to a positive value if maxLength should be used. 
     * @param maxLength the maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return True if maxLength should be considered.
     */
    public boolean isMaxLength(){
        return this.maxLength > 0;
    }
}
