package org.jsapar.compose;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;

import java.io.IOException;
import java.util.Iterator;

/**
 * Internal common interface for all schema composers that uses a schema to compose text output.
 * You should normally not use this class directly in your code.
 * Use a {@link org.jsapar.TextComposer} instead.
 */
public interface SchemaComposer {

    /**
     * This method should only be called by a {@link org.jsapar.TextComposer}. You should normally not use this class directly in your code.
     * Use a {@link org.jsapar.TextComposer} instead.
     *
     * Composes all the lines supplied by the iterator and adds line separator between each composed line.
     *
     * @param iterator A line iterator to get lines from.
     * @throws IOException if an io-error occurs
     *
     */
    void compose(Iterator<Line> iterator) throws IOException, JSaParException;

    /**
     * Called before compose() in order to set up or write file header.
     *
     * @throws IOException if an io-error occurs
     *
     */
    void beforeCompose() throws IOException, JSaParException;

    /**
     * Called after compose() in order to clean up or write file footer.
     *
     * @throws IOException if an io-error occurs
     *
     */
    void afterCompose() throws IOException, JSaParException;

    /**
     * Composes the supplied line but does not write any line separator.
     *
     * @param line The line to compose output from.
     * @return True if line was actually composed, false otherwise.
     * @throws IOException if an io-error occurs
     */
    boolean composeLine(Line line) throws IOException;
}
