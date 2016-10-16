package org.jsapar.error;

import org.jsapar.parse.*;

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
            LineParseError error = new LineParseError(lineNumber, message);
            ErrorEvent event = new ErrorEvent(source, error);
            eventListener.errorEvent(event);
            break;
        }
        case EXCEPTION: {
            LineParseError error = new LineParseError(lineNumber, message);
            throw new ParseException(error, message);
        }
        case NONE:
            return true;
        case IGNORE_LINE:
            return false;
        }
        return false;
    }
}
