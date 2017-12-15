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
     * Composes line separator accoring to schema.
     */
    void composeLineSeparator();


    /**
     * Composes the supplied line but does not write any line separator.
     *
     * @param line The line to compose output from.
     * @return True if line was actually composed, false otherwise.
     * @throws IOException if an io-error occurs
     */
    boolean composeLine(Line line) ;
}
