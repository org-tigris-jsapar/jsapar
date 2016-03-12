package org.jsapar.parse;

import java.io.IOException;

import org.jsapar.JSaParException;
import org.jsapar.parse.LineEventListener;

public interface Parser {

    /**
     * This method should only be called by a TextParser class. Don't use this
     * directly in your code. Use a TextParser instead.
     * 
     * @param listener
     * @throws JSaParException
     * @throws IOException
     */
    void parse(LineEventListener listener) throws JSaParException, IOException;

}
