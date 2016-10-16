package org.jsapar.schema;

/**
 * Describes the schema for a specific csv cell.
 */
public class CsvSchemaCell extends SchemaCell {

    /**
     * The maximum number of characters that are read or written to/from the cell. Input and output
     * value will be silently truncated to this length. If you want to get an error when field is to
     * long, use the format regexp pattern instead.
     */
    private int maxLength=-1;
    
    public CsvSchemaCell(String sName) {
        super(sName);
    }

    public CsvSchemaCell(String sName, SchemaCellFormat cellFormat) {
        super(sName, cellFormat);
    }
    
    @Override
    public CsvSchemaCell clone() {
        return (CsvSchemaCell) super.clone();
    }

    /**
     * The maximum number of characters that are read or written to/from the cell. Input and output
     * value will be silently truncated to this length. If you want to get an error when field is to
     * long, use the format regexp pattern instead.
     * 
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * The maximum number of characters that are read or written to/from the cell. Input and output
     * value will be silently truncated to this length. If you want to get an error when field is to
     * long, use the format regexp pattern instead. <br/>
     * Set to a positive value if maxLength should be used.
     * 
     * @param maxLength
     *            the maxLength to set
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
