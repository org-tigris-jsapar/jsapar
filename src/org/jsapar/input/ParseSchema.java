package org.jsapar.input;

import java.io.IOException;

import org.jsapar.JSaParException;


/**
 * Common interface for all schemas that can be used for parsing.
 * 
 * @see Parser
 * @author stejon0
 *
 */
public interface ParseSchema {

    /**
     * This method should only be called by a Parser class. Don't use this
     * directly in your code. Use a Parser instead.
     * 
     * @param reader
     * @param listener
     * @throws IOException
     * @throws JSaParException
     */
    public abstract void parse(java.io.Reader reader, ParsingEventListener listener)
	    throws IOException, JSaParException;

}
