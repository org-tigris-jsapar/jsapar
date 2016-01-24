package org.jsapar.parse;

import java.io.IOException;

import org.jsapar.JSaParException;

public interface LineReader {

    /**
     * Parses one line from the input and returns the result without line separator characters.
     * 
     * @return The next available line from the input without trailing line separator or null if end of input buffer was
     *         reached.
     * @throws IOException
     * @throws JSaParException
     */
    public String readLine() throws IOException, JSaParException;

    /**
     * @return The line separator.
     */
    public String getLineSeparator();

}
