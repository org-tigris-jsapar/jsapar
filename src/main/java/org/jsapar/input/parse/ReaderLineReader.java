/**
 * 
 */
package org.jsapar.input.parse;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.JSaParException;

/**
 * LineReader implementation that reads lines from a Reader object.
 * 
 * Reader object should be closed by caller. Once End of File has been reached, the instance will no longer be useful.
 * 
 * @author stejon0
 *
 */
public class ReaderLineReader implements LineReader {

    private String  lineSeparator;

    private Reader  reader;

    private boolean eofReached = false;

    /**
     * Creates a lineReader instance reading from a reader.
     * 
     * @param lineSeparator
     *            The line separator character to use while parsing lines.
     * @param reader
     *            The reader to read from.
     */
    public ReaderLineReader(String lineSeparator, Reader reader) {
        super();
        this.lineSeparator = lineSeparator;
        this.reader = reader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.input.parse.LineReader#readLine()
     */
    @Override
    public String readLine() throws IOException, JSaParException {
        if (eofReached)
            return null;
        char chLineSeparatorNext = getLineSeparator().charAt(0);
        StringBuilder lineBuilder = new StringBuilder();
        StringBuilder pending = new StringBuilder();
        while (true) {
            int nRead = reader.read();
            if (nRead == -1) {
                eofReached = true;
                return lineBuilder.toString();
            }
            char chRead = (char) nRead;
            if (chRead == chLineSeparatorNext) {
                pending.append(chRead);
                if (getLineSeparator().length() > pending.length())
                    chLineSeparatorNext = getLineSeparator().charAt(pending.length());
                else
                    break; // End of line found.
            }
            // It was not a complete line separator.
            else if (pending.length() > 0) {
                // Move pending characters to lineBuilder.
                lineBuilder.append(pending);
                pending.setLength(0);
                lineBuilder.append(chRead);
            } else
                lineBuilder.append(chRead);
            if (lineBuilder.length() > 1000000)
                throw new JSaParException(
                        "Line size exceeds 1M characters. Probably wrong line-separator for the line type within the schema.");
        }
        return lineBuilder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.input.parse.LineReader#getLineSeparator()
     */
    @Override
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * @return The internal reader that is used to read lines.
     */
    public Reader getReader() {
        return reader;
    }

}
