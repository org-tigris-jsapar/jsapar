package org.jsapar.parse.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a line from a {@link BufferedLineReader} and splits it into an array of Strings, one for each cell depending on cell
 * separator and quote character. If {@link #reset()} is called the next call to {@link #readLine(String, char)} will start reading the same line
 * again. This makes it possible to read the line once first to match against cell conditions and then again to do
 * the actual parsing.
 * Also makes it possible to handle the fact that a quoted cell can contain line-breaks.
 * <p>
 */
public class CsvLineReaderBuffered implements CsvLineReader{

    private boolean reset               = false;

    private BufferedLineReader lineReader;
    private RawLine            currentLine;

    public CsvLineReaderBuffered(String lineSeparator, Reader reader) {
        this.lineReader = new BufferedLineReader(lineSeparator, reader);
    }


    /**
     * Resets the reader instance so that the next call to {@link #readLine(String, char)} will start from the same position as previous
     * call. Multiple calls to this method between each call to {@link #readLine(String, char)} have no further effect.
     *
     */
    public void reset()  {
        reset = true;
    }

    /**
     * Reads next line from the input source. A call to {@link #reset()} method will cause this method to start from the same
     * position in the input as previous line. The analogy with {@link java.io.BufferedReader} is that this method does an implicit call
     * to {@link java.io.BufferedReader#mark(int)} on the underlying buffered reader.  Handles the fact that a quoted cell can contain line-breaks and
     * cell separator that should be part of the cell value.
     *
     * @param cellSeparator A sequence of characters that determines separation between cell elements in the input text.
     * @param quoteChar     The character that can be used to quote a cell if the cell contains cell separators or line
     *                      separators that should be part of the cell value. The value 0 indicates that quotes are not used.
     * @return A list of String cell values fetched from the input reader. Handles the fact that a quoted cell can
     * contain line-breaks and cell separator that should be part of the cell value. Returns an empty list if line was
     * empty and null if end of input was reached.
     * @throws IOException In case of an error in underlying IO.
     *
     */
    public List<String> readLine(String cellSeparator, char quoteChar) throws IOException {
        if (currentLine != null) {
            if (reset) {
                reset = false;
                if (!currentLine.sameType(cellSeparator, quoteChar)) {
                    lineReader.reset();
                    currentLine = new RawLine(cellSeparator, quoteChar,
                            makeCellSplitter(cellSeparator, quoteChar));
                    return currentLine.makeLine();
                }
                return currentLine.getLine();
            }
            if (currentLine.sameType(cellSeparator, quoteChar)) {
                currentLine.clear();
            } else {
                currentLine = new RawLine(cellSeparator, quoteChar,
                        makeCellSplitter(cellSeparator, quoteChar));
            }
        } else {
            currentLine = new RawLine(cellSeparator, quoteChar, makeCellSplitter(cellSeparator, quoteChar));
        }
        return currentLine.makeLine();
    }

    private CellSplitter makeCellSplitter(String cellSeparator, char quoteChar) {
        return quoteChar == 0 ?
                new UnquotedCellSplitter(cellSeparator) :
                new QuotedCellSplitter(cellSeparator, quoteChar, lineReader, 25);
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

    /**
     * Internal private class that represents a raw line where cells have been split into string values.
     */
    private class RawLine {
        private List<String> line = new ArrayList<>(32);
        private String       cellSeparator;
        private char         quoteChar;
        private CellSplitter cellSplitter;

        RawLine(String cellSeparator, char quoteChar, CellSplitter cellSplitter) {
            this.cellSeparator = cellSeparator;
            this.quoteChar = quoteChar;
            this.cellSplitter = cellSplitter;
        }

        boolean sameType(String cellSeparator, char quoteChar) {
            return this.cellSeparator.equals(cellSeparator) && this.quoteChar == quoteChar;
        }

        List<String> makeLine() throws IOException {
            String sLine = lineReader.readLine();
            if (null == sLine)
                return null;
            line.clear();
            cellSplitter.split(sLine, line);
            if(line.size() == 1 && line.get(0).trim().isEmpty())
                line.clear();
            return line;
        }

        boolean isEmpty() {
            return line.isEmpty();
        }

        List<String> getLine() {
            return line;
        }

        void clear() {
            line.clear();
        }
    }

}
