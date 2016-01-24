package org.jsapar.parse;

import java.io.IOException;

import org.jsapar.JSaParException;
import org.jsapar.parse.LineEventListener;

public interface SchemaParser {

    /**
     * This method should only be called by a Parser class. Don't use this
     * directly in your code. Use a Parser instead.
     * 
     * @param listener
     * @throws JSaParException
     * @throws IOException
     */
    public void parse(LineEventListener listener) throws JSaParException, IOException;

}
