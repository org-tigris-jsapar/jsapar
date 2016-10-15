package org.jsapar.parse;

import org.jsapar.error.JSaParError;
import org.jsapar.schema.SchemaCellFormat;

/**
 * This class is used as a way for the parser to report back parsing errors. The
 * class contains error information about a cell that failed to parse.
 *
 * @author Jonas Stenberg
 */
public final class CellParseError extends JSaParError {

    // long cellIndex=0;
    private long      lineNumber;
    private final String           cellName;
    private final String           cellValue;
    private final SchemaCellFormat cellFormat;

    /**
     * @param lineNumber
     * @param cellName
     * @param cellValue
     * @param cellFormat
     * @param errorDescription
     */
    public CellParseError(long lineNumber,
                          String cellName,
                          String cellValue,
                          SchemaCellFormat cellFormat,
                          String errorDescription) {
        super(errorDescription);
        this.lineNumber = lineNumber;
        this.cellName = cellName;
        this.cellValue = cellValue;
        this.cellFormat = cellFormat;
    }

    /**
     * @param cellName
     * @param cellValue
     * @param cellFormat
     * @param errorDescription
     */
    public CellParseError(String cellName, String cellValue, SchemaCellFormat cellFormat, String errorDescription) {
        super(errorDescription);
        this.cellName = cellName;
        this.cellValue = cellValue;
        this.cellFormat = cellFormat;
        lineNumber = 0;
    }

    /**
     * @param lineNumber
     * @param cellName
     * @param cellValue
     * @param cellFormat
     * @param errorDescription
     */
    public CellParseError(long lineNumber,
                          String cellName,
                          String cellValue,
                          SchemaCellFormat cellFormat,
                          String errorDescription,
                          Throwable e) {
        super(errorDescription, e);
        this.lineNumber = lineNumber;
        this.cellName = cellName;
        this.cellValue = cellValue;
        this.cellFormat = cellFormat;
    }

    /**
     * Constructs a new cell parse error containing a copy of supplied error but assigning a new line number.
     *
     * @param lineNumber
     * @param error
     */
    public CellParseError(long lineNumber, CellParseError error) {
        super(error.getErrorDescription(), error.getException());
        this.lineNumber = lineNumber;
        this.cellName = error.cellName;
        this.cellValue = error.cellValue;
        this.cellFormat = error.cellFormat;
    }

    /**
     * @return the cellName
     */
    public String getCellName() {
        return cellName;
    }

    /**
     * @return the cellValue
     */
    public String getCellValue() {
        return cellValue;
    }

    /**
     * @return the cellFormat
     */
    public SchemaCellFormat getCellFormat() {
        return cellFormat;
    }

    /**
     * @return A simple message describing the error and it's location.
     */
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Line=");
        sb.append(this.getLineNumber());
        sb.append(", Cell='");
        sb.append(this.cellName);
        sb.append("'");
        sb.append(", Value='");
        sb.append(this.cellValue);
        sb.append("'");
        sb.append(" - Parse error: ");
        sb.append(this.getErrorDescription());
        if (cellFormat != null) {
            sb.append(" - Expected format: ");
            sb.append(this.cellFormat);
        }
        return sb.toString();
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
