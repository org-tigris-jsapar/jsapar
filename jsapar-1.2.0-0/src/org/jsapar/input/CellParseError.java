package org.jsapar.input;

import org.jsapar.schema.SchemaCellFormat;

/**
 * This class is used as a way for the parser to report back parsing errors. The
 * class contains error information about a cell that failed to parse.
 * 
 * @author Jonas Stenberg
 */
public class CellParseError {

    long lineNumber = 0;
    // long cellIndex=0;
    String cellName = null;
    String cellValue = null;
    SchemaCellFormat cellFormat = null;
    String errorDescription = null;

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
    }

    /**
     * @return the lineNumber
     */
    public long getLineNumber() {
	return lineNumber;
    }

    /**
     * @param lineNumber
     *                the lineNumber to set
     */
    public void setLineNumber(long lineNumber) {
	this.lineNumber = lineNumber;
    }

    /**
     * @return the cellName
     */
    public String getCellName() {
	return cellName;
    }

    /**
     * @param cellName
     *                the cellName to set
     */
    public void setCellName(String cellName) {
	this.cellName = cellName;
    }

    /**
     * @return the cellValue
     */
    public String getCellValue() {
	return cellValue;
    }

    /**
     * @param cellValue
     *                the cellValue to set
     */
    public void setCellValue(String cellValue) {
	this.cellValue = cellValue;
    }

    /**
     * @return the cellFormat
     */
    public SchemaCellFormat getCellFormat() {
	return cellFormat;
    }

    /**
     * @param cellFormat
     *                the cellFormat to set
     */
    public void setCellFormat(SchemaCellFormat cellFormat) {
	this.cellFormat = cellFormat;
    }

    /**
     * @return the errorDescription
     */
    public String getErrorDescription() {
	return errorDescription;
    }

    /**
     * @param errorDescription
     *                the errorDescription to set
     */
    public void setErrorDescription(String errorDescription) {
	this.errorDescription = errorDescription;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("{ Line ");
	sb.append(this.lineNumber);
	sb.append(", Cell '");
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
	sb.append(" }");
	return sb.toString();
    }

}
