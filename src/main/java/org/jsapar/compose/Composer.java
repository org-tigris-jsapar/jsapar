package org.jsapar.compose;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.MulticastErrorEventListener;
import org.jsapar.model.Document;
import org.jsapar.model.Line;

/**
 * Common interface for composer classes. A composer is able to take a {@link Document} or a sequence of {@link Line} and turn them into an output of
 * some kind. The output type depends on the implementation of this interface.
 *
 * @see org.jsapar.TextComposer
 * @see org.jsapar.compose.bean.BeanComposer
 */
public interface Composer {

    /**
     * This method composes some output based on an entire {@link Document}.
     *
     * @param document The document to compose output from.
     * @throws java.io.UncheckedIOException When a low level IO error occurs.
     */
    void compose(Document document);

    /**
     * Composes output based on supplied {@link Line}, including line separator
     *
     * @param line The line to compose
     * @return True if the line was actually composed.
     * @throws java.io.UncheckedIOException When a low level IO error occurs.
     */
    boolean composeLine(Line line);

    /**
     * Sets an error event listener to this composer. If you want to add more than one error event listeners, use the {@link MulticastErrorEventListener}
     *
     * @param errorListener The error event listener to add.
     */
    void setErrorEventListener(ErrorEventListener errorListener);
}
