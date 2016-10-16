package org.jsapar.error;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Base class for all types of errors that can be added to an {@link ErrorEvent}
 */
public class JSaParError {
    private final String    errorDescription;
    private final Throwable exception;

    public JSaParError(String errorDescription, Throwable exception) {
        this.errorDescription =
                errorDescription == null || (exception != null && exception.getMessage() != null && errorDescription
                        .endsWith(exception.getMessage())) ?
                        errorDescription :
                        errorDescription + " - " + exception.getMessage();
        this.exception = exception;
    }

    public JSaParError(String errorDescription) {
        this.errorDescription = errorDescription;
        this.exception = null;
    }

    /**
     * @return the errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    public Throwable getException() {
        return exception;
    }

    /**
     * @return A simple message describing the error and it's location.
     */
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Error: ");
        sb.append(this.getErrorDescription());
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        sb.append(getMessage());
        if (getException() != null) {
            StringWriter stackWriter = new StringWriter();
            sb.append("- Exception: ");
            getException().printStackTrace(new PrintWriter(stackWriter));
            sb.append(stackWriter.toString());
            stackWriter.toString();
        }
        sb.append(" }");
        return sb.toString();
    }
}
