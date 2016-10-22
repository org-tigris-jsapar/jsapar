package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;

/**
 * Decorates line errors with current line information.
 * Created by stejon0 on 2016-10-02.
 */
public class LineDecoratorErrorEventListener implements ErrorEventListener {

    private final ErrorEventListener errorListener;
    private long lineNumber;

    public LineDecoratorErrorEventListener(ErrorEventListener errorListener, long lineNumber) {
        this.errorListener = errorListener;
        this.lineNumber = lineNumber;
    }

    @Override
    public void errorEvent(ErrorEvent event) {
        if(event.getError() instanceof CellParseException)
            ((CellParseException) event.getError()).setLineNumber(lineNumber);
        else if(event.getError() instanceof LineParseException)
            ((LineParseException) event.getError()).setLineNumber(lineNumber);
        errorListener.errorEvent(event);
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
