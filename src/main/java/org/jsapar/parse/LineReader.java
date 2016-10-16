package org.jsapar.parse;

import java.io.IOException;

import org.jsapar.error.JSaParException;

public interface LineReader {

    /**
     * Parses one line from the input and returns the result without line separator characters.
     * 
     * @return The next available line from the input without trailing line separator or null if end of input buffer was
     *         reached.
     * @throws IOException
     * @throws JSaParException
     */
    String readLine() throws IOException, JSaParException;


    /**
     * @return The line separator.
     */
    String getLineSeparator();

    /**
     * @return True if this reader has reached end of input stream. False otherwise.
     */
    boolean eofReached();
}
