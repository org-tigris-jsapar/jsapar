package org.jsapar.parse;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;

import java.io.IOException;

/**
 * Internal interface for text parser that can parse text based on a schema.
 */
public interface SchemaParser {

    /**
     * This method should only be called by a TextParser class. Don't use this
     * directly in your code. Use a TextParser instead.
     * 
     * @param listener
     * @param errorListener
     * @throws JSaParException
     * @throws IOException
     */
    void parse(LineEventListener listener, ErrorEventListener errorListener) throws JSaParException, IOException;

}
