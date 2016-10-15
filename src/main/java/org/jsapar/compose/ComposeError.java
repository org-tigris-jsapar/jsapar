package org.jsapar.compose;

import org.jsapar.error.JSaParError;
import org.jsapar.model.Line;

/**
 * Created by stejon0 on 2016-10-09.
 */
public class ComposeError extends JSaParError {

    /**
     * A clone of the line that caused the error.
     */
    private final Line line;

    public ComposeError(String errorDescription, Line line, Throwable exception) {
        super(errorDescription, exception);
        this.line = line.clone();
    }


    public ComposeError(String errorDescription, Throwable exception) {
        super(errorDescription, exception);
        this.line = null;
    }

    public ComposeError(String errorDescription) {
        super(errorDescription);
        this.line = null;
    }



    public Line getLine() {
        return line;
    }
}
