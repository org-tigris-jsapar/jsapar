package org.jsapar.parse.line;

import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.parse.LineParseException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Internal utility class for handling validation error.
 */
public class ValidationHandler {

    public ValidationHandler() {
    }

    /**
     * Handle line validation.
     *
     * @param lineNumber    The line number if present, 0 otherwise.
     * @param action        How to tread this validation.
     *                      ERROR will generate an error event but return true.
     *                      EXCEPTION will throw a LineParseException immediately.
     *                      NONE will simply ignore the validation and continue without action.
     *                      OMIT_LINE will omit the current line from the parsing context.
     * @param eventListener Destination error event listener in case error event is the action taken by the method.
     * @param messageSupplier    Should create a message to provide as error message in case it is considered an error.
     * @return True if the line should be included in the current context. False if the line should be ignored.
     */
    public boolean lineValidation(long lineNumber,
                                  ValidationAction action,
                                  Consumer<JSaParException> eventListener,
                                  Supplier<String> messageSupplier) {
        switch (action) {
        case ERROR: {
            LineParseException error = new LineParseException(lineNumber, messageSupplier.get());
            eventListener.accept(error);
            return true;
        }
        case EXCEPTION: {
            throw new LineParseException(lineNumber, messageSupplier.get());
        }
        case NONE:
            return true;
        case OMIT_LINE:
            return false;
        }
        return false;
    }
}
