package org.jsapar.parse;

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
    public void cellErrorEvent(CellErrorEvent event) {
        errorListener.cellErrorEvent(new CellErrorEvent(event.getSource(), new CellParseError(lineNumber, event.getParseError())));
    }

    @Override
    public void lineErrorEvent(LineErrorEvent event) {
        errorListener.lineErrorEvent(new LineErrorEvent(event.getSource(), new LineParseError(lineNumber, event.getParseError())));
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
