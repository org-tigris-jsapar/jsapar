package org.jsapar.parse.text;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.LineEventListener;

import java.io.IOException;

/**
 * Internal interface for text parser that can parse text based on a schema.
 */
public interface TextSchemaParser {

    /**
     * This method should only be called by a TextParseTask class. Don't use this
     * directly in your code. Use a TextParseTask instead.
     * 
     * @param listener
     * @param errorListener
     *
     * @throws IOException
     * @return Number of lines parsed
     */
    long parse(LineEventListener listener, ErrorEventListener errorListener) throws IOException;

}
