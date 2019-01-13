package org.jsapar.parse.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Reads a line from a {@link Reader} and splits it into an array of Strings, one for each cell depending on cell
 * separator and quote character. If {@link #reset()} is called the next call to {@link #readLine(String, char)} will start reading the same line
 * again. This makes it possible to read the line once first to match against cell conditions and then again to do
 * the actual parsing.
 * Also makes it possible to handle the fact that a quoted cell can contain line-breaks.
 * <p>
 */
public interface CsvLineReader {

    /**
     * Resets the reader instance so that the next call to {@link #readLine(String, char)} will start from the same position as previous
     * call. Multiple calls to this method between each call to {@link #readLine(String, char)} have no further effect.
     */
    void reset();

    /**
     * Fetches next line. If {@link #reset()} was called after last call, the same line will be parsed again but with
     * different cell separator and quote character. Handles the fact that a quoted cell can contain line-breaks and
     * cell separator that should be part of the cell value. The implementation of this method may return the same
     * list instance every time.
     *
     * @param cellSeparator A sequence of characters that determines separation between cell elements in the input text.
     * @param quoteChar     The character that can be used to quote a cell if the cell contains cell separators or line
     *                      separators that should be part of the cell value. The value 0 indicates that quotes are not used.
     * @return A list of String cell values fetched from the input reader. Handles the fact that a quoted cell can
     * contain line-breaks and cell separator that should be part of the cell value. Returns an empty list if line was
     * empty or if end of input was reached. The caller should regard the returned list as unmutable. Alterations
     * to the returned
     * list may result in unexpected behavior.
     * @throws IOException In case of an error in underlying IO.
     */
    List<String> readLine(String cellSeparator, char quoteChar) throws IOException;

    /**
     * @return True if the last call to readLine resulted in end of input. A call to reset will reset also this flag to
     * the state it had before last call to readLine()
     */
    boolean eofReached();

    /**
     * @return The line number of the line last returned by readLine()
     */
    long currentLineNumber();

    /**
     * @return True if the last call to readLine resulted in a line that was empty.
     */
    boolean lastLineWasEmpty();

}
