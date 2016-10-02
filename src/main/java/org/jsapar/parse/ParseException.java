/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.parse;

/**
 * @author Jonas
 * 
 */
public class ParseException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 7936794742075345440L;

    private final ParseError parseError;


    /**
     * @param parseError
     * @param sMessage
     */
    public ParseException(ParseError parseError, String sMessage) {
        super(sMessage);
        this.parseError = parseError;
    }


    /**
     * @param parseError
     * @param ex
     */
    public ParseException(ParseError parseError, Throwable ex) {
        super(ex);
        this.parseError = parseError;
    }

    /**
     * @param parseError
     * @param sMessage
     * @param ex
     */
    public ParseException(ParseError parseError, String sMessage, Throwable ex) {
        super(sMessage, ex);
        this.parseError = parseError;
    }

    /**
     * @param parseError
     */
    public ParseException(ParseError parseError) {
        this.parseError = parseError;
    }


    /**
     * Returns the cell parse error information. Can be null if the parse exception does not origin
     * from a cell parsing.
     * 
     * @return the parseError
     */
    public ParseError getParseError() {
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
