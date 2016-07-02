package org.jsapar.parse.csv;

import org.jsapar.JSaParException;

import java.io.IOException;
import java.io.Reader;

/**
 * Reads a line from a BufferedLineReader and splits it into an array of Strings, one for each cell depending on cell
 * separator and quote character. If reset() is called the next call to readLine() will start reading the same line
 * again. Handles the fact that a quoted cell can contain line-breaks.
 * <p/>
 * Created by stejon0 on 2016-05-01.
 */
public class CsvLineReader {

    private static final String[] EMPTY_LINE = new String[0];
    private boolean reset               = false;

    BufferedLineReader lineReader;
    RawLine            currentLine;

    public CsvLineReader(String lineSeparator, Reader reader) {
        this.lineReader = new BufferedLineReader(lineSeparator, reader);
    }

    public CsvLineReader(BufferedLineReader lineReader) {
        this.lineReader = lineReader;
    }

    /**
     * Resets the reader instance so that the next call to readLine() will start from the same position as previous
     * call. Multiple calls to this method between each call to readLine() have no further effect.
     *
     * @throws IOException
     */
    public void reset() throws IOException {
        reset = true;
    }

    /**
     * Reads next line from the input source. A call to reset() method will cause this method to start from the same
     * position in the input as previous line. The analogy with BufferedReader is that this method does an implicit call
     * to mark() on the underlying buffered reader.  Handles the fact that a quoted cell can contain line-breaks and
     * cell separator that should be part of the cell value.
     *
     * @param cellSeparator A sequence of characters that determines separation between cell elements in the input text.
     * @param quoteChar     The character that can be used to quote a cell if the cell contains cell separators or line
     *                      separators that should be part of the cell value. The value 0 indicates that quotes are not used.
     * @return An array of String cell values fetched from the input reader. Handles the fact that a quoted cell can
     * contain line-breaks and cell separator that should be part of the cell value. Returns en empty array if line was
     * empty and null if end of input was reached.
     * @throws IOException
     * @throws JSaParException
     */
    public String[] readLine(String cellSeparator, char quoteChar) throws IOException, JSaParException {
        if (currentLine != null) {
            if (reset) {
                if (!currentLine.sameType(cellSeparator, quoteChar)) {
                    lineReader.reset();
                    currentLine = new RawLine(cellSeparator, quoteChar,
                            makeCellSplitter(cellSeparator, quoteChar, lineReader));
                    currentLine.makeLine(lineReader);
                }
                reset = false;
                return currentLine.getLine();
            }
            if (currentLine.sameType(cellSeparator, quoteChar)) {
                currentLine = new RawLine(cellSeparator, quoteChar, currentLine);
            } else {
                currentLine = new RawLine(cellSeparator, quoteChar,
                        makeCellSplitter(cellSeparator, quoteChar, lineReader));
            }
        } else {
            currentLine = new RawLine(cellSeparator, quoteChar, makeCellSplitter(cellSeparator, quoteChar, lineReader));
        }
        currentLine.makeLine(lineReader);
        return currentLine.getLine();
    }

    private CellSplitter makeCellSplitter(String cellSeparator, char quoteChar, BufferedLineReader lineReader2) {
        return quoteChar == 0 ?
                new SimpleCellSplitter(cellSeparator) :
                new QuotedCellSplitter(cellSeparator, quoteChar, lineReader2);
    }

    /**
     * @return True if the last call to readLine resulted in a line that was empty.
     */
    public boolean lastLineWasEmpty() {
        return currentLine == null || currentLine.isEmpty();
    }

    /**
     * @return True if the last call to readLine resulted in end of input. A call to reset will reset also this flag to
     * the state it had before last call to readLine()
     */
    public boolean eofReached() {
        return !reset && lineReader.eofReached();
    }

    /**
     * @return The line number of the line last returned by readLine()
     */
    public long currentLineNumber() {
        return lineReader.getLineNumber();
    }

    private class RawLine {
        private String[]     line;
        private String       cellSeparator;
        private char         quoteChar;
        private CellSplitter cellSplitter;

        public RawLine(String cellSeparator, char quoteChar, CellSplitter cellSplitter) {
            this.cellSeparator = cellSeparator;
            this.quoteChar = quoteChar;
            this.cellSplitter = cellSplitter;
        }

        public RawLine(String cellSeparator, char quoteChar, RawLine sameLineAgain) {
            this.cellSeparator = cellSeparator;
            this.quoteChar = quoteChar;
            this.cellSplitter = sameLineAgain.cellSplitter;
        }

        public boolean sameType(String cellSeparator, char quoteChar) {
            return this.cellSeparator.equals(cellSeparator) && this.quoteChar == quoteChar;
        }

        public String[] makeLine(BufferedLineReader lineReader2) throws IOException, JSaParException {
            String sLine = lineReader2.readLine();
            if (null == sLine)
                return null;
            line = cellSplitter.split(sLine);
            return line;
        }

        public boolean isEmpty() {
            return line.length <= 0;
        }

        public String[] getLine() {
            return line;
        }
    }

}
