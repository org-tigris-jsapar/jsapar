package org.jsapar.parse.line;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineParseException;

import java.util.function.Consumer;

/**
 * Internal class. Decorates line errors with current line information.
 */
public class LineDecoratorErrorConsumer implements Consumer<JSaParException> {

    private Consumer<JSaParException> errorListener;
    private Line                      line;

    public LineDecoratorErrorConsumer(){}

    public void initialize(Consumer<JSaParException> errorListener, Line line) {
        this.errorListener = errorListener;
        this.line = line;
    }

    @Override
    public void accept(JSaParException error) {
        if(error instanceof CellParseException) {
            ((CellParseException) error).setLineNumber(line.getLineNumber());
            line.addCellError((CellParseException) error);
        }
        else if(error instanceof LineParseException) {
            ((LineParseException) error).setLineNumber(line.getLineNumber());
        }
        errorListener.accept(error);
    }

}
