package org.jsapar.parse.csv.states;

import org.jsapar.parse.LineParseException;
import org.jsapar.parse.csv.CsvLineReader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This implementation uses state pattern. It loads characters into a buffer and creates cell value strings from that
 * buffer.
 */
public class CsvLineReaderStates implements CsvLineReader {
    private static final int MAX_LINE_LENGTH = 8 * 1024;
    private static final String EMPTY_CELL = "";

    private State beginCellState;
    private State foundEndQuoteState;
    private State foundEndQuoteWithinState;
    private State quotedCellState;
    private State unquotedCellState;
    private State state;
    private List<String> currentLine;
    private int currentCellOffset =0;
    private int offsetFromEndQuote =0;
    private EolCheck eolCheck;
    private final char lastEolChar;

    private boolean eof;
    private boolean reset;

    private String cellSeparator;
    private char lastCellSeparatorChar;
    private char quoteChar;
    private long lineNumber = 0;

    private final ReadBuffer buffer;

    /**
     * @param lineSeparator  The line separator to use
     * @param reader The reader to read characters from.
     * @param allowReadAhead If true, reading from the reader can be optimized by reading larger chunks of data into a
     *                       buffer but that can only be utilized if it is ok to read until the end of the file or if it
     *                       is ok consume more characters from the reader than is actually needed for parsing.
     */
    public CsvLineReaderStates(String lineSeparator, Reader reader, boolean allowReadAhead) {
        eolCheck = Arrays.asList("\n", "\r\n").contains(lineSeparator) ? new EolCheckCRLF() : new EolCheckCustom(lineSeparator);
        lastEolChar = eolCheck.getLastEolChar();

        beginCellState = new BeginCellState();
        foundEndQuoteState = new FoundEndQuoteState();
        foundEndQuoteWithinState = new FoundEndQuoteWithinState();
        quotedCellState = new QuotedCellState();
        unquotedCellState = new UnquotedCellState();

        currentLine = new ArrayList<>();
        buffer = new ReadBuffer(reader, MAX_LINE_LENGTH, (allowReadAhead ? MAX_LINE_LENGTH : 1));

        beginCellState();
    }

    @Override
    public void reset(){
        this.eof=false;
        this.reset=true;
    }

    @Override
    public boolean eofReached() {
        return eof;
    }

    @Override
    public long currentLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean lastLineWasEmpty() {
        return currentLine.isEmpty();
    }

    @Override
    public List<String> readLine(String cellSeparator, char quoteChar) throws IOException {
        if(reset)
            return lastLine(cellSeparator, quoteChar);
        setLineCharacteristics(cellSeparator, quoteChar);
        buffer.markLine();
        lineNumber++;
        return processLine();
    }

    private void setLineCharacteristics(String cellSeparator, char quoteChar){
        this.cellSeparator = cellSeparator;
        this.lastCellSeparatorChar = cellSeparator.charAt(cellSeparator.length()-1);
        this.quoteChar = quoteChar;
    }

    private List<String> processLine() throws IOException {
        currentLine.clear();

        while (true) {
            if(buffer.cursor >= buffer.bufferSize){
                final int count = buffer.load();
                if(count<1){
                    if(state == quotedCellState){
                        buffer.resetCell();
                        currentCellOffset=0;
                        offsetFromEndQuote=0;
                        state=unquotedCellState;
                        continue;
                    }
                    else if (count == 0){
                        throw new LineParseException( lineNumber,
                                "Maximum line size exceeded. More than " + MAX_LINE_LENGTH + " bytes were read without finding a line separator or maybe there is a miss-placed start quote without matching end quote.");

                    }
                    this.eof = true;
                    addToLineExcept(offsetFromEndQuote);
                    return lineComplete();
                }
            }
            if(state.processChar(buffer.nextCharacter()))
                return lineComplete();
        }
    }

    private List<String> lineComplete() {
        beginCellState();
        if(currentLine.size() == 1 && currentLine.get(0).trim().isEmpty())
            currentLine.clear();
        return currentLine;
    }

    /**
     * Parses last line again but use different separator and quote character.
     * @param cellSeparator The new cell separator to use.
     * @param quoteChar The quote character to use.
     * @return A line
     * @throws IOException In case of underlying io error.
     */
    private List<String> lastLine(String cellSeparator, char quoteChar) throws IOException {
        reset=false;
        if(quoteChar==this.quoteChar && cellSeparator.equals(this.cellSeparator))
            return currentLine;
        buffer.resetLine();
        setLineCharacteristics(cellSeparator, quoteChar);
        return processLine();
    }

    /**
     * Sets state to beginCellState and resets cell offsets.
     */
    private void beginCellState() {
        state = beginCellState;
        buffer.markCell();
        currentCellOffset = 0;
        offsetFromEndQuote = 0;
    }

    /**
     * Adds a completed cell to a line.
     * @param except Number of characters to skip from end while adding cell to line.
     */
    private void addToLineExcept(int except) {
        final int cellStart = buffer.cellMark + currentCellOffset;
        addToLine(cellStart, buffer.cursor-except-cellStart);
    }

    /**
     * Adds a completed cell to a line.
     * @param offset Begin index
     * @param count Number of characters to add
     */
    private void addToLine(int offset, int count) {
        if(count==0)
            currentLine.add(EMPTY_CELL);
        else
            currentLine.add(new String(buffer.buffer, offset, count));
    }

    private void addEmptyToLine() {
        currentLine.add(EMPTY_CELL);
        buffer.markCell();
        currentCellOffset = 0;
    }

    /**
     * Checks tail of current cell matches supplied string. Assumes that the current character is already checked.
     * @param toMatch The string to match
     * @return True if tail of current cell matches supplied string if the supplied character were to be added.
     */
    private boolean tailOfCellMatches(String toMatch){
        int cellOffset = buffer.cursor -toMatch.length();
        if(cellOffset < buffer.cellMark) {
            return false;
        }
        // Scan backwards to see if characters before matches. Start at character before current.
        for(int i = toMatch.length()-2; i>=0; i--){
            if(toMatch.charAt(i) !=  buffer.buffer[cellOffset + i])
                return false;
        }
        return true;
    }

    private boolean endOfLineAddPending(char c, int skip){
        int size = this.eolCheck.eolMatchSize(c);
        if(size<=0)
            return false;
        addToLineExcept(size+skip);
        return true;
    }

    /**
     * Interface for states
     */
    private interface State {
        boolean processChar(final char c);
    }

    /**
     *
     */
    private class BeginCellState implements State {
        @Override
        public boolean processChar(final char c) {
            if (c == quoteChar) {
                state = quotedCellState;
                currentCellOffset++;
                return false;
            }
            if (c == lastCellSeparatorChar && cellSeparator.length()==1) {
                addEmptyToLine();
                return false;
            }
            if (c == lastEolChar && endOfLineAddPending(c, 0)) {
                return true; // An empty line without cells.
            }
            state = unquotedCellState;
            return false;
        }
    }

    /**
     * Unquoted cell content expected.
     */
    private class UnquotedCellState implements State {
        @Override
        public boolean processChar(final char c) {
            if( c==lastCellSeparatorChar && tailOfCellMatches(cellSeparator)){
                addToLineExcept(cellSeparator.length());
                beginCellState();
                return false;
            }
            return c == lastEolChar && endOfLineAddPending(c, 0);
        }
    }

    /**
     * Quoted cell content expected.
     */
    private class QuotedCellState implements State {
        @Override
        public boolean processChar(final char c) {
            if (c == quoteChar) {
                offsetFromEndQuote=1;
                state = foundEndQuoteState;
            }
            return false;
        }
    }

    /**
     * End quote was found.
     */
    private class FoundEndQuoteState implements State {
        @Override
        public boolean processChar(final char c) {
            if (c==lastCellSeparatorChar && cellSeparator.length()==1) {
                addToLineExcept(2);
                beginCellState();
                return false;
            }
            if(c == lastEolChar && endOfLineAddPending(c, 1)){
                return true;
            }
            if (c == quoteChar) {
                offsetFromEndQuote=1;
                return false;
            }

            offsetFromEndQuote++;
            state = foundEndQuoteWithinState;
            return false;
        }
    }

    /**
     * A second quote was found but some other character was found afterwards that was not a single character cell separator or line separator.
     */
    private class FoundEndQuoteWithinState implements State {
        @Override
        public boolean processChar(final char c) {
            if (c == quoteChar) {
                state = foundEndQuoteState;
                offsetFromEndQuote=1;
                return false;
            }
            if (c == lastCellSeparatorChar && tailOfCellMatches(cellSeparator)) {
                if(cellSeparator.length() == offsetFromEndQuote)
                    addToLineExcept(cellSeparator.length()+1);
                else
                    addToLine(buffer.cellMark, buffer.cursor - buffer.cellMark -cellSeparator.length());
                beginCellState();
                return false;
            }
            if(c == lastEolChar){
                final int eolSize = eolCheck.eolMatchSize(c);
                if( eolSize == offsetFromEndQuote){
                    addToLineExcept(eolSize+offsetFromEndQuote-1);
                    return true;
                }
                else if(eolSize>0) {
                    addToLine(buffer.cellMark, buffer.cursor - buffer.cellMark - eolSize);
                    return true;
                }
            }

            offsetFromEndQuote++;
            return false;
        }
    }

    /**
     * Interface for checking end of line.
     */
    private interface EolCheck {
        int eolMatchSize(final char c);
        char getLastEolChar();
    }


    /**
     * Checking end of line with either LF or CR+LF.
     */
    class EolCheckCRLF implements EolCheck {
        @Override
        public int eolMatchSize(final char c) {
            if(buffer.cursor > 2 && buffer.buffer[buffer.cursor-2] == '\r')
                return 2;
            return 1;
        }

        @Override
        public char getLastEolChar() {
            return '\n';
        }
    }

    /**
     * Checking end of line with custom arbitrary character sequence.
     */
    class EolCheckCustom implements EolCheck {
        private String lineSeparator;
        private char lastLineSeparatorChar;

        EolCheckCustom(String lineSeparator) {
            this.lineSeparator = lineSeparator;
            this.lastLineSeparatorChar = lineSeparator.charAt(lineSeparator.length()-1);
        }

        @Override
        public int eolMatchSize(char c) {
            return tailOfCellMatches(lineSeparator) ? lineSeparator.length() : 0;
        }

        @Override
        public char getLastEolChar() {
            return lastLineSeparatorChar;
        }
    }

}
