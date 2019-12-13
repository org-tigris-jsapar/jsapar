package org.jsapar.parse;

import org.jsapar.error.*;
import org.jsapar.model.Line;

import java.util.function.Consumer;

/**
 * Abstract implementation of {@link ParseTask} interface. Provides possibility to have line event listeners and
 * error event listeners. Override this class to implement a specific parser.
 */
public abstract class AbstractParseTask implements ParseTask {
    private Consumer<Line>            lineConsumer       = null;
    private Consumer<JSaParException> errorConsumer = new ExceptionErrorConsumer();


    public Consumer<Line> getLineConsumer() {
        return lineConsumer;
    }

    public void setLineConsumer(Consumer<Line> lineConsumer) {
        this.lineConsumer = lineConsumer;
    }

    public Consumer<JSaParException> getErrorConsumer() {
        return errorConsumer;
    }

    public void setErrorConsumer(Consumer<JSaParException> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
