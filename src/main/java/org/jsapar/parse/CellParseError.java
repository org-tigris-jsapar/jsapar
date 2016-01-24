package org.jsapar.parse;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jsapar.schema.SchemaCellFormat;

/**
 * This class is used as a way for the parser to report back parsing errors. The
 * class contains error information about a cell that failed to parse.
 * 
 * @author Jonas Stenberg
 */
public final class CellParseError {

    private final long lineNumber;
    // long cellIndex=0;
    private final String cellName;
    private final String cellValue;
    private final SchemaCellFormat cellFormat;
    private final String errorDescription;
    private final Throwable exception;

    /**
     * @param lineNumber
     * @param cellName
     * @param cellValue
     * @param cellFormat
     * @param errorDescription
     */
    public CellParseError(long lineNumber, String cellName, String cellValue,
	    SchemaCellFormat cellFormat, String errorDescription) {
	this.lineNumber = lineNumber;
	this.cellName = cellName;
	this.cellValue = cellValue;
	this.cellFormat = cellFormat;
	this.errorDescription = errorDescription;
	this.exception = null;
    }

    /**
     * @param cellName
     * @param cellValue
     * @param cellFormat
     * @param errorDescription
     */
    public CellParseError(String cellName, String cellValue, SchemaCellFormat cellFormat,
	    String errorDescription) {
	this.lineNumber = 0;
	this.cellName = cellName;
	this.cellValue = cellValue;
	this.cellFormat = cellFormat;
	this.errorDescription = errorDescription;
        this.exception = null;
    }

    /**
     * @param lineNumber
     * @param cellName
     * @param cellValue
     * @param cellFormat
     * @param errorDescription
     */
    public CellParseError(long lineNumber, String cellName, String cellValue,
            SchemaCellFormat cellFormat, String errorDescription, Throwable e) {
        this.lineNumber = lineNumber;
        this.cellName = cellName;
        this.cellValue = cellValue;
        this.cellFormat = cellFormat;
        this.errorDescription = errorDescription;
        this.exception = e;
    }

    /**
     * Constructs a new cell parse error containing a copy of supplied error but assigning a new line number.
     * @param lineNumber
     * @param error
     */
    public CellParseError(long lineNumber, CellParseError error) {
        this.lineNumber = lineNumber;
        this.cellName = error.cellName;
        this.cellValue = error.cellValue;
        this.cellFormat = error.cellFormat;
        this.errorDescription = error.errorDescription;
        this.exception = error.exception;
    }

    /**
     * @return the lineNumber
     */
    public long getLineNumber() {
	return lineNumber;
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
     * @return the errorDescription
     */
    public String getErrorDescription() {
	return errorDescription;
    }

    /**
     * @return A simple message describing the error and it's location.
     */
    public String getMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("Line=");
        sb.append(this.lineNumber);
        sb.append(", Cell='");
        sb.append(this.cellName);
        sb.append("'");
        sb.append(", Value='");
        sb.append(this.cellValue);
        sb.append("'");
        sb.append(" - Parse error: ");
        sb.append(this.errorDescription);
        if (cellFormat != null) {
            sb.append(" - Expected format: ");
            sb.append(this.cellFormat);
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        sb.append(getMessage());
        if (exception != null) {
            StringWriter stackWriter = new StringWriter();
            sb.append("- Exception: ");
            exception.printStackTrace(new PrintWriter(stackWriter));
            sb.append(stackWriter.toString());
            stackWriter.toString();
        }
        sb.append(" }");
        return sb.toString();
    }

    public Throwable getException() {
        return exception;
    }

}
