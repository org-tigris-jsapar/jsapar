package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Line;

/**
 * Internal class. Decorates line errors with current line information.
 * Created by stejon0 on 2016-10-02.
 */
public class LineDecoratorErrorEventListener implements ErrorEventListener {

    private final ErrorEventListener errorListener;
    private       Line               line;

    public LineDecoratorErrorEventListener(ErrorEventListener errorListener, Line line) {
        this.errorListener = errorListener;
        this.line = line;
    }

    @Override
    public void errorEvent(ErrorEvent event) {
        if(event.getError() instanceof CellParseException) {
            ((CellParseException) event.getError()).setLineNumber(line.getLineNumber());
            line.addCellError((CellParseException) event.getError());
        }
        else if(event.getError() instanceof LineParseException) {
            ((LineParseException) event.getError()).setLineNumber(line.getLineNumber());
        }
        errorListener.errorEvent(event);
    }

}
