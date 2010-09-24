/**
 * 
 */
package org.jsapar.io;

import java.util.List;

import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseException;

/**
 * @author stejon0
 *
 */
public class MaxErrorsExceededException extends ParseException {

    /**
     * 
     */
    private static final long serialVersionUID = -8025034269584118995L;
    private List<CellParseError> parseErrors;


    public MaxErrorsExceededException(List<CellParseError> parseErrors) {
        super(parseErrors.get(parseErrors.size()-1));
        this.setParseErrors(parseErrors);
    }

    /**
     * @param parseErrors the parseErrors to set
     */
    public void setParseErrors(List<CellParseError> parseErrors) {
        this.parseErrors = parseErrors;
    }

    /**
     * @return the parseErrors
     */
    public List<CellParseError> getParseErrors() {
        return parseErrors;
    }
    
    @Override
    public String getMessage() {
        return "Maximum number of errors exceeded.";
    }
    

}
