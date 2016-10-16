package org.jsapar.parse;

import java.io.IOException;

import org.jsapar.error.JSaParException;
import org.jsapar.error.ErrorEventListener;

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
