/**
 * 
 */
package org.jsapar.convert;

import java.util.List;

import org.jsapar.error.JSaParError;
import org.jsapar.parse.CellParseError;
import org.jsapar.parse.ParseException;

/**
 * @author stejon0
 *
 */
public class MaxErrorsExceededException extends ParseException {

    /**
     * 
     */
    private static final long serialVersionUID = -8025034269584118995L;
    private final List<JSaParError> errors;


    public MaxErrorsExceededException(JSaParError lastError, List<JSaParError> allErrors) {
        super(lastError);
        this.errors = allErrors;
    }


    /**
     * @return the errors
     */
    public List<JSaParError> getErrors() {
        return errors;
    }
    
    @Override
    public String getMessage() {
        return "Maximum number of errors exceeded.";
    }
    

}
