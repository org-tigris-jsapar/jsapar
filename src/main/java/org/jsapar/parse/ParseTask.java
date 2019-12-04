package org.jsapar.parse;

import org.jsapar.error.*;
import org.jsapar.model.Line;
import org.jsapar.parse.bean.BeanParseTask;
import org.jsapar.parse.text.TextParseTask;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Common interface for all parse jobs. The interface does not state anything about the origin of the parsed items.
 * <p>
 * An instance of a parser is only useful once. You create an instance, initializes it
 * with the event listeners needed, then call {@link #execute()}.
 *
 * @see TextParseTask
 * @see BeanParseTask
 */
public interface ParseTask extends AutoCloseable {

    /**
     * Sets a line event listener to this parser.
     * <p>
     * Deprecated as of 2.2. Use {@link #setLineConsumer(Consumer)} instead.
     *
     * @param eventListener The line event listener.
     */
    @Deprecated
    default void setLineEventListener(LineEventListener eventListener){
        setLineConsumer(l->eventListener.lineParsedEvent(new LineParsedEvent(this, l)));
    }

    /**
     * Sets an error event listener to this parser. Default behavior otherwise is to throw an exception upon the first
     * error.
     * <p>
     * Deprecated as of 2.2. Use {@link #setErrorConsumer(Consumer)} instead.
     *
     * @param errorEventListener The error event listener.
     */
    @Deprecated
    default void setErrorEventListener(ErrorEventListener errorEventListener){
        setErrorConsumer(e->errorEventListener.errorEvent(new ErrorEvent(this, e)));
    }

    /**
     * Sets a line consumer to this parser. If you want more than one line event listener registered, use a {@link MulticastConsumer}.
     *
     * @param lineConsumer The line consumer to use.
     */
    void setLineConsumer(Consumer<Line> lineConsumer);

    /**
     * Sets an error consumer to this parser. Default behavior otherwise is to throw an exception upon the first
     * error. If you want more than one consumer to get each error event, use a {@link MulticastConsumer}.
     *
     * @param errorConsumer The error consumer.
     */
    void setErrorConsumer(Consumer<JSaParException> errorConsumer);

    /**
     * Start the parsing. Should only be called once for each {@link ParseTask}. Consecutive calls may have unexpected behavior.
     *
     * @return Number of line events that were generated, i.e. the number of lines parsed.
     * @throws IOException In case there is an error reading the input.
     */
    long execute() throws IOException;

    /**
     * Closes attached resources.
     *
     * @throws IOException In case of error closing io resources.
     */
    @Override
    default void close() throws IOException {
        // Do nothing
    }
}
