package org.jsapar.parse;

import org.jsapar.error.JSaParException;
import org.jsapar.schema.SchemaCellFormat;

/**
 * This class is used as a way for the parser to report back parsing errors. The
 * class contains error information about a cell that failed to parse.
 *
 */
public final class CellParseException extends JSaParException {

    private long      lineNumber;
    private final String           cellName;
    private final String           cellValue;
    private final SchemaCellFormat cellFormat;

    /**
     * Creates a new cell parsing exception
     * @param lineNumber The line number where the error occurred.
     * @param cellName The cell name where the error occurred.
     * @param cellValue The cell value that caused the error.
     * @param cellFormat Expected cell format. Can be null.
     * @param errorDescription Description of the error.
     */
    public CellParseException(long lineNumber,
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
     * Creates a new cell parsing exception
     * @param cellName The cell name where the error occurred.
     * @param cellValue The cell value that caused the error.
     * @param cellFormat Expected cell format. Can be null.
     * @param errorDescription Description of the error.
     */
    public CellParseException(String cellName, String cellValue, SchemaCellFormat cellFormat, String errorDescription) {
        super(errorDescription);
        this.cellName = cellName;
        this.cellValue = cellValue;
        this.cellFormat = cellFormat;
        lineNumber = 0;
    }

    /**
     * Creates a new cell parsing exception
     * @param lineNumber The line number where the error occurred.
     * @param cellName The cell name where the error occurred.
     * @param cellValue The cell value that caused the error.
     * @param cellFormat Expected cell format. Can be null.
     * @param errorDescription Description of the error.
     * @param cause           An exception that caused this error.
     */
    public CellParseException(long lineNumber,
                              String cellName,
                              String cellValue,
                              SchemaCellFormat cellFormat,
                              String errorDescription,
                              Throwable cause) {
        super(errorDescription, cause);
        this.lineNumber = lineNumber;
        this.cellName = cellName;
        this.cellValue = cellValue;
        this.cellFormat = cellFormat;
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
        sb.append(" Cell='");
        sb.append(this.cellName);
        sb.append("'");
        sb.append(" Value='");
        sb.append(this.cellValue);
        sb.append("'");
        if (cellFormat != null) {
            sb.append(" Expected: ");
            sb.append(this.cellFormat);
        }
        sb.append(" - ");
        sb.append(super.getMessage());
        return sb.toString();
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
