/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.parse;

import org.jsapar.error.JSaParError;

/**
 * @author Jonas
 * 
 */
public class ParseException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 7936794742075345440L;

    private final JSaParError parseError;


    /**
     * @param parseError
     * @param sMessage
     */
    public ParseException(JSaParError parseError, String sMessage) {
        super(sMessage);
        this.parseError = parseError;
    }


    /**
     * @param parseError
     * @param ex
     */
    public ParseException(JSaParError parseError, Throwable ex) {
        super(ex);
        this.parseError = parseError;
    }

    /**
     * @param parseError
     * @param sMessage
     * @param ex
     */
    public ParseException(JSaParError parseError, String sMessage, Throwable ex) {
        super(sMessage, ex);
        this.parseError = parseError;
    }

    /**
     * @param parseError
     */
    public ParseException(JSaParError parseError) {
        this.parseError = parseError;
    }


    /**
     * Returns the cell parse error information. Can be null if the parse exception does not origin
     * from a cell parsing.
     * 
     * @return the parseError
     */
    public JSaParError getParseError() {
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
