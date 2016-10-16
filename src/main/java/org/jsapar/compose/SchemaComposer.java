package org.jsapar.compose;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;

import java.io.IOException;
import java.util.Iterator;

/**
 * Internal common interface for all schema composers that uses a schema to compose text output.
 */
public interface SchemaComposer {

    /**
     * This method should only be called by a TextComposer class. Don't use this directly in your code.
     * Use a TextComposer instead.
     *
     * @param iterator A line iterator to get lines from.
     * @throws IOException
     * @throws JSaParException
     */
    void compose(Iterator<Line> iterator) throws IOException, JSaParException;

    /**
     * Called before compose() in order to set up or write file header.
     *
     * @throws IOException
     * @throws JSaParException
     */
    void beforeCompose() throws IOException, JSaParException;

    /**
     * Called after compose() in order to clean up or write file footer.
     *
     * @throws IOException
     * @throws JSaParException
     */
    void afterCompose() throws IOException, JSaParException;

    /**
     * Create a schema line composer for this schema composer.
     *
     * @param line The line to compose output from.
     * @return A schema line composer.
     */
    boolean composeLine(Line line) throws IOException;
}
