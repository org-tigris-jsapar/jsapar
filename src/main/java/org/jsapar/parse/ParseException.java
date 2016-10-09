/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.parse;

import org.jsapar.error.Error;

/**
 * @author Jonas
 * 
 */
public class ParseException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 7936794742075345440L;

    private final Error parseError;


    /**
     * @param parseError
     * @param sMessage
     */
    public ParseException(Error parseError, String sMessage) {
        super(sMessage);
        this.parseError = parseError;
    }


    /**
     * @param parseError
     * @param ex
     */
    public ParseException(Error parseError, Throwable ex) {
        super(ex);
        this.parseError = parseError;
    }

    /**
     * @param parseError
     * @param sMessage
     * @param ex
     */
    public ParseException(Error parseError, String sMessage, Throwable ex) {
        super(sMessage, ex);
        this.parseError = parseError;
    }

    /**
     * @param parseError
     */
    public ParseException(Error parseError) {
        this.parseError = parseError;
    }


    /**
     * Returns the cell parse error information. Can be null if the parse exception does not origin
     * from a cell parsing.
     * 
     * @return the parseError
     */
    public Error getParseError() {
        return parseError;
    }


    @Override
    public String getMessage() {
        String sMessage = super.getMessage();
        if (parseError != null)
            sMessage += parseError.toString();
        return sMessage;
    }
}
