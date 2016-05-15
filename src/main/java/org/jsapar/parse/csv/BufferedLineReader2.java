/**
 *
 */
package org.jsapar.parse.csv;

import org.jsapar.JSaParException;
import org.jsapar.parse.BufferedLineReader;
import org.jsapar.parse.LineReader;
import org.jsapar.parse.ReaderLineReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * LineReader implementation that reads lines from a Reader object.
 * <p/>
 * Reader object should be closed by caller. Once End of File has been reached, the instance will no longer be useful
 * (unless it is reset to a previous state by calling reset() ).
 *
 * @author stejon0
 */
public class BufferedLineReader2 implements LineReader {

    private static final int MAX_LINE_LENGTH = 10 * 1024;

    private long lineNumber=0;
    private BufferedReader   reader;
    private ReaderLineReader lineReaderImpl;

    /**
     * Creates a lineReader instance reading from a reader.
     *
     * @param lineSeparator The line separator character to use while parsing lines.
     * @param reader        The reader to read from.
     */
    public BufferedLineReader2(String lineSeparator, Reader reader) {
        super();
        this.reader = new BufferedReader(reader);
        this.lineReaderImpl = new ReaderLineReader(lineSeparator, this.reader);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.input.parse.LineReader#readLine()
     */
    @Override
    public String readLine() throws IOException, JSaParException {
        reader.mark(MAX_LINE_LENGTH);
        lineNumber++;
        return peekLine();
    }

    /**
     * @return
     * @throws IOException
     * @throws JSaParException
     */
    public String peekLine() throws IOException, JSaParException {
        return lineReaderImpl.readLine();
    }

    /**
     * Resets state of reader to before last call to readLine
     *
     * @throws IOException
     */
    public void reset() throws IOException {
        reader.reset();
        lineNumber--;
        lineReaderImpl.resetEof();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.input.parse.LineReader#getLineSeparator()
     */
    @Override
    public String getLineSeparator() {
        return lineReaderImpl.getLineSeparator();
    }

    /**
     * @return The internal reader that is used to read lines.
     */
    public Reader getReader() {
        return reader;
    }

    /**
     * @return 0 before any line has been read. The number of lines read by realLine() after that if not reset by call
     * to reset(). Calls to peekLine() are not counted.
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * @return True if End of input stream was reached, false otherwise.
     */
    public boolean eofReached() {
        return lineReaderImpl.isEofReached();
    }
}
