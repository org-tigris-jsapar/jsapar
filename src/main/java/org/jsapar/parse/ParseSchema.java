package org.jsapar.parse;

import java.io.IOException;

import org.jsapar.error.JSaParException;
import org.jsapar.TextParser;

/**
 * Common interface for all schemas that can be used for parsing.
 * 
 * @see TextParser
 * @author stejon0
 *
 */
public interface ParseSchema {

    /**
     * This method should only be called by a TextParser class. Don't use this
     * directly in your code. Use a TextParser instead.
     * 
     * @param reader
     * @param listener
     * @throws IOException
     * @throws JSaParException
     */
//    public abstract void parse(java.io.Reader reader, LineEventListener listener)
//	    throws IOException, JSaParException;

}
