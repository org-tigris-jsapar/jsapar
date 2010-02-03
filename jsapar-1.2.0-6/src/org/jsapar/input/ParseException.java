/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.input;

import org.jsapar.JSaParException;

/**
 * @author Jonas
 * 
 */
public class ParseException extends JSaParException {

    /**
     * 
     */
    private static final long serialVersionUID = 7936794742075345440L;

    private CellParseError cellParseError;

    /**
     * 
     */
    public ParseException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cellParseError
     * @param sMessage
     */
    public ParseException(CellParseError cellParseError, String sMessage) {
        super(sMessage);
        this.cellParseError = cellParseError;
    }

    /**
     * @param sMessage
     */
    public ParseException(String sMessage) {
        super(sMessage);
    }

    /**
     * @param cellParseError
     * @param ex
     */
    public ParseException(CellParseError cellParseError, Throwable ex) {
        super(ex);
        this.cellParseError = cellParseError;
    }

    /**
     * @param cellParseError
     * @param sMessage
     * @param ex
     */
    public ParseException(CellParseError cellParseError, String sMessage, Throwable ex) {
        super(sMessage, ex);
        this.cellParseError = cellParseError;
    }

    /**
     * @param sMessage
     * @param ex
     */
    public ParseException(String sMessage, Throwable ex) {
        super(sMessage, ex);
    }

    /**
     * @param cellParseError
     */
    public ParseException(CellParseError cellParseError) {
        this.cellParseError = cellParseError;
    }

    /**
     * Returns the cell parse error information. Can be null if the parse exception does not origin
     * from a cell parsing.
     * 
     * @return the parseError
     */
    public CellParseError getCellParseError() {
        return cellParseError;
    }

    /**
     * @param parseError
     *            the parseError to set
     */
    public void setCellParseError(CellParseError parseError) {
        this.cellParseError = parseError;
    }

    @Override
    public String getMessage() {
        String sMessage = super.getMessage();
        if (cellParseError != null)
            sMessage += cellParseError.toString();
        return sMessage;
    }
}
