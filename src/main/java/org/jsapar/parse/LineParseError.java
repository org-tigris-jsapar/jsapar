package org.jsapar.parse;

import org.jsapar.error.JSaParError;

/**
 * Created by stejon0 on 2016-07-12.
 */
public class LineParseError extends JSaParError {
    private long      lineNumber;

    public LineParseError(long lineNumber, String errorDescription) {
        super(errorDescription);

        this.lineNumber = lineNumber;
    }

    public LineParseError(long lineNumber, String errorDescription, Throwable e) {
        super(errorDescription, e);
        this.lineNumber = lineNumber;
    }

    /**
     * Creates a new LineParseError which is a copy of the old one but with a different line number
     * @param lineNumber The new line number
     * @param error The LineParseError to make a copy of
     */
    public LineParseError(long lineNumber, LineParseError error) {
        super(error.getMessage(), error.getCause());
        this.lineNumber = lineNumber;
    }

    /**
     * @return the lineNumber
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * @return A simple message describing the error and it's location.
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Line=");
        sb.append(this.getLineNumber());
        sb.append(" - ");
        sb.append(super.getMessage());
        return sb.toString();
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
