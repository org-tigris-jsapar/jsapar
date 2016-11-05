package org.jsapar.parse;

import java.io.IOException;

/**
 * Reads input line by line.
 */
public interface LineReader {

    /**
     * Parses one line from the input and returns the result without line separator characters.
     * 
     * @return The next available line from the input without trailing line separator or null if end of input buffer was
     *         reached.
     * @throws IOException
     */
    String readLine() throws IOException;


    /**
     * @return The line separator.
     */
    String getLineSeparator();

    /**
     * @return True if this reader has reached end of input stream. False otherwise.
     */
    boolean eofReached();
}
