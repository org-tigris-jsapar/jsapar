package org.jsapar;

import org.jsapar.model.Document;
import org.jsapar.model.Line;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by stejon0 on 2016-01-24.
 */
public interface Composer {

    /**
     * This is the common interface for all composers.
     *
     * @param document
     * @throws IOException
     * @throws JSaParException
     */
    void compose(Document document) throws IOException;

    /**
     * Composes output based on supplied line
     *
     * @param line The line to compose
     * @return True if the line was actually composed.
     */
    boolean composeLine(Line line) throws IOException;


}
