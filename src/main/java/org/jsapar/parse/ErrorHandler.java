package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ValidationAction;

/**
 * Internal utility class for handling validation error.
 */
public class ErrorHandler {

    public ErrorHandler() {
    }

    public boolean lineValidationError(Object source,
                                       long lineNumber,
                                       String message,
                                       ValidationAction action,
                                       ErrorEventListener eventListener)  {
        switch (action) {
        case ERROR: {
            LineParseException error = new LineParseException(lineNumber, message);
            ErrorEvent event = new ErrorEvent(source, error);
            eventListener.errorEvent(event);
            return true;
        }
        case EXCEPTION: {
            throw new LineParseException(lineNumber, message);
        }
        case NONE:
            return true;
        case IGNORE_LINE:
            return false;
        }
        return false;
    }
}
