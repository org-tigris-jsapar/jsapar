package org.jsapar.compose;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Line;

/**
 * Internal utility class for handling validation error.
 */
public class ErrorHandler {

    public ErrorHandler() {
    }

    /**
     * Handles an error while composing depending on the supplied {@link ValidationAction}
     * @param source The origin of the error. Usually, use this from caller.
     * @param line The line where the error occurred.
     * @param message A descriptive message of the error.
     * @param action The error action currently configured for the error.
     * @param eventListener An {@link ErrorEventListener} to use in case an error event should be fired.
     * @return True if the line should be processed, false otherwise.
     * @throws ComposeException if the action is {@link ValidationAction#EXCEPTION}
     */
    public boolean lineValidationError(Object source,
                                       Line line,
                                       String message,
                                       ValidationAction action,
                                       ErrorEventListener eventListener)  {
        switch (action) {
        case ERROR: {
            ComposeException error = new ComposeException(message, line);
            ErrorEvent event = new ErrorEvent(source, error);
            eventListener.errorEvent(event);
            break;
        }
        case EXCEPTION: {
            throw new ComposeException(message, line);
        }
        case NONE:
            return true;
        case IGNORE_LINE:
            return false;
        }
        return false;
    }
}
