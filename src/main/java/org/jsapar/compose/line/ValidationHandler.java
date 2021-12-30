package org.jsapar.compose.line;

import org.jsapar.compose.ComposeException;
import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Line;

import java.util.function.Consumer;

/**
 * Internal utility class for handling validation error.
 */
public class ValidationHandler {

    public ValidationHandler() {
    }

    /**
     * Handles an error while composing depending on the supplied {@link ValidationAction}
     *
     * @param line          The line where the error occurred.
     * @param message       A descriptive message of the error.
     * @param action        The error action currently configured for the error.
     *                      ERROR will generate an error event but return true.
     *                      EXCEPTION will throw a LineComposeException immediately.
     *                      NONE will simply ignore the validation and continue without action, if possible.
     *                      OMIT_LINE will omit the current line from the composing context.
     * @param eventListener An {@link Consumer} to use in case an error event should be fired as a callback.
     * @return True if the line should be processed, false otherwise.
     * @throws ComposeException if the action is {@link ValidationAction#EXCEPTION}
     */
    public boolean lineValidationError(Line line,
                                       String message,
                                       ValidationAction action,
                                       Consumer<JSaParException> eventListener) {
        switch (action) {
        case ERROR: {
            ComposeException error = new ComposeException(message, line);
            eventListener.accept(error);
            return true;
        }
        case EXCEPTION: {
            throw new ComposeException(message, line);
        }
        case NONE:
            return true;
        case OMIT_LINE:
            return false;
        }
        return false;
    }
}
