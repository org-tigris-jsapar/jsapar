package org.jsapar.compose;

import org.jsapar.error.JSaParError;
import org.jsapar.model.Line;

/**
 * Error that can happen when composing.
 */
public class ComposeError extends JSaParError {

    /**
     * A clone of the line that caused the error.
     */
    private final Line line;

    /**
     * Creates a new ComposeError
     * @param errorDescription Error message.
     * @param line The line where the error occured.
     * @param exception An exception that was caught and is hereby forwarded as en error instead.
     */
    public ComposeError(String errorDescription, Line line, Throwable exception) {
        super(errorDescription, exception);
        this.line = line.clone();
    }


    /**
     * Creates a new ComposeError
     * @param errorDescription Error message.
     * @param exception An exception that was caught and is hereby forwarded as en error instead.
     */
    public ComposeError(String errorDescription, Throwable exception) {
        super(errorDescription, exception);
        this.line = null;
    }

    /**
     * Creates a new ComposeError
     * @param errorDescription Error message.
     */
    public ComposeError(String errorDescription) {
        super(errorDescription);
        this.line = null;
    }

    /**
     * @return The line where the error occurred or null if no line reference could be given.
     */
    public Line getLine() {
        return line;
    }
}
