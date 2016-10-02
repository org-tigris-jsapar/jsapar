package org.jsapar.parse.error;

import org.jsapar.parse.*;
import org.jsapar.schema.ValidationAction;

/**
 * Created by stejon0 on 2016-07-12.
 */
public class ErrorHandler {

    private ErrorEventListener eventListener;

    public ErrorHandler(ErrorEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public boolean lineValidationError(Object source, long lineNumber, String message, ValidationAction action) throws ParseException {
        switch(action) {
        case ERROR:
            LineParseError error = new LineParseError(lineNumber, message);
            LineErrorEvent event = new LineErrorEvent(source, error);
            eventListener.lineErrorEvent(event);
            break;
        case EXCEPTION:

        case NONE:
        case IGNORE_LINE:
            return false;
        }
        return false;
    }
}
