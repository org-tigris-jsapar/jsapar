package org.jsapar.compose;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.error.MulticastErrorEventListener;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.parse.MulticastConsumer;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Common interface for composer classes. A composer is able to take a {@link Document} or a sequence of {@link Line} and turn them into an output of
 * some kind. The output type depends on the implementation of this interface.
 *
 * @see org.jsapar.TextComposer
 * @see org.jsapar.compose.bean.BeanComposer
 */
public interface Composer extends AutoCloseable{

    /**
     * This method composes some output based on an entire {@link Document}.
     *
     * @param document The document to compose output from.
     * @throws java.io.UncheckedIOException When a low level IO error occurs.
     */
    default void compose(Document document){
        compose(document.iterator());
    }

    /**
     * Composes all lines returned by the iterator.
     *
     * @param lineIterator An iterator that iterates over a collection of lines. Can be used to build lines on-the-fly if you
     *                     don't want to store them all in memory.
     */
    default void compose(Iterator<Line> lineIterator) {
        while (lineIterator.hasNext())
            composeLine(lineIterator.next());
    }

    /**
     * Composes all lines returned by the supplied stream.
     * @param lineStream A stream of lines to compose
     */
    default void compose(Stream<Line> lineStream){
        lineStream.forEach(this::composeLine);
    }

    /**
     * Composes output based on supplied {@link Line}, including line separator if applicable.
     *
     * @param line The line to compose
     * @return True if the line was actually composed.
     * @throws java.io.UncheckedIOException When a low level IO error occurs.
     */
    boolean composeLine(Line line);

    /**
     * Composes an empty line, i.e. writing an additional line separator.
     * @return True if empty line was composed.
     */
    default boolean composeEmptyLine() {
        return false;
    }

    /**
     * Sets an error event listener to this composer. If you want to add more than one error event listeners, use the {@link MulticastErrorEventListener}
     *
     * @param errorEventListener The error event listener to add.
     */
    @Deprecated
    default void setErrorEventListener(ErrorEventListener errorEventListener){
        setErrorConsumer(e->errorEventListener.errorEvent(new ErrorEvent(this, e)));
    }

    /**
     * Sets an error consumer to this composer. Default behavior otherwise is to throw an exception upon the first
     * error. If you want more than one consumer to get each error event, use a {@link MulticastConsumer}.
     *
     * @param errorConsumer The error consumer.
     */
    void setErrorConsumer(Consumer<JSaParException> errorConsumer);

    @Override
    default void close() throws IOException{
        // do nothing
    }
}
