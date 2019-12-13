package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Abstract base class for all parsers.
 *
 * The default error handling is to throw an exception upon the first error that occurs. You can however change that
 * behavior by adding an {@link org.jsapar.error.ErrorEventListener}. There are several implementations to choose from such as
 * {@link org.jsapar.error.RecordingErrorEventListener} or
 * {@link org.jsapar.error.ThresholdRecordingErrorEventListener}, or you may implement your own..
 */
public class AbstractParser {

    private Consumer<JSaParException> errorConsumer = new ExceptionErrorConsumer();

    /**
     * Deprecated since 2.2. Use {@link #setErrorConsumer(Consumer)} instead.
     * @param errorEventListener The event listener that will receive events.
     */
    @Deprecated
    public void setErrorEventListener(ErrorEventListener errorEventListener) {
        setErrorConsumer(e->errorEventListener.errorEvent(new ErrorEvent(this, e)));
    }

    protected long execute(ParseTask parseTask, Consumer<Line> lineConsumer) throws IOException {
        parseTask.setLineConsumer(lineConsumer);
        parseTask.setErrorConsumer(errorConsumer);
        return parseTask.execute();
    }

    /**
     * Sets an error consumer that will be called for each error. Default is to throw an exception.
     * @param errorConsumer The error consumer that will be called for each error.
     */
    public void setErrorConsumer(Consumer<JSaParException> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
