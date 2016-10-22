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
     * @param message Error message.
     * @param line The line where the error occured.
     * @param exception An exception that was caught and is hereby forwarded as en error instead.
     */
    public ComposeError(String message, Line line, Throwable exception) {
        super(message, exception);
        this.line = line.clone();
    }


    /**
     * Creates a new ComposeError
     * @param message Error message.
     * @param exception An exception that was caught and is hereby forwarded as en error instead.
     */
    public ComposeError(String message, Throwable exception) {
        super(message, exception);
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
     * @return A simple message describing the error and it's location.
     */
    @Override
    public String getMessage() {
        if(line != null) {
            StringBuilder sb = new StringBuilder(super.getMessage());
            sb.append(" at line ");
            sb.append(this.line);
            return sb.toString();
        }
        else
            return super.getMessage();
    }
    /**
     * @return The line where the error occurred or null if no line reference could be given.
     */
    public Line getLine() {
        return line;
    }
}
