package org.jsapar.parse.csv;

import org.jsapar.parse.LineParseException;
import org.jsapar.schema.QuoteSyntax;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This implementation  uses state pattern. It loads characters into a buffer and creates cell value strings from that
 * buffer.
 */
final class CsvLineReaderStates implements CsvLineReader {
    private static final String EMPTY_CELL = "";
    private final int maxLineLength;

    private final State beginCellState;
    private final State foundEndQuoteState;
    private final State foundEndQuoteWithinState;
    private final State quotedCellState;
    private final State unquotedCellState;
    private State state;
    private final List<String> currentLine;
    private final EolCheck eolCheck;
    private final char lastEolChar;

    private boolean eof;
    private boolean reset;

    private String cellSeparator=";";
    private char lastCellSeparatorChar;
    private char quoteChar='"';
    private long lineNumber = 0;

    private final ReadBuffer buffer;

    private final CellCreator currentCellCreator = new CellCreator();
    /**
     * @param lineSeparator  The line separator to use
     * @param reader The reader to read characters from.
     * @param allowReadAhead If true, reading from the reader can be optimized by reading larger chunks of data into a
*                       buffer but that can only be utilized if it is ok to read until the end of the file or if it
     * @param maxLineLength The maximum number of characters in a line. Make sure that all lines fits within this size.
     * @param quoteSyntax Determines the syntax of how quoted cells are parsed.
     */
    CsvLineReaderStates(String lineSeparator, Reader reader, boolean allowReadAhead, int maxLineLength, QuoteSyntax quoteSyntax) {
        eolCheck = Arrays.asList("\n", "\r\n").contains(lineSeparator) ? new EolCheckCRLF() : new EolCheckCustom(lineSeparator);
        lastEolChar = eolCheck.getLastEolChar();

        beginCellState = new BeginCellState();
        switch (quoteSyntax) {
        case FIRST_LAST:
            foundEndQuoteState = new FoundEndQuoteStateFirstLast();
            break;
        case RFC4180:
            foundEndQuoteState = new FoundEndQuoteStateRfc();
            break;
        default:
            throw new AssertionError("Unsupported quote syntax while parsing: " + quoteSyntax);
        }
        foundEndQuoteWithinState = new FoundEndQuoteWithinState();
        quotedCellState = new QuotedCellState();
        unquotedCellState = new UnquotedCellState();

        currentLine = new ArrayList<>();
        this.maxLineLength = maxLineLength;
        buffer = new ReadBuffer(reader, maxLineLength, (allowReadAhead ? maxLineLength : 1));

        beginCellState();
    }

    @Override
    public void reset(){
        this.eof=false;
        this.reset=true;
    }

    @Override
    public void skipLine() throws IOException {
        if(reset){
            reset = false;
            return;
        }
        readLine(cellSeparator, quoteChar);
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
                        currentCellCreator.reset();
                        state=unquotedCellState;
                        continue;
                    }
                    else if (count == 0){
                        throw new LineParseException( lineNumber,
                                "Maximum line size exceeded. More than " + maxLineLength + " bytes were read without finding a line separator or maybe there is a miss-placed start quote without matching end quote.");

                    }
                    this.eof = true;
                    currentCellCreator.addToLine();
                    return lineComplete();
                }
            }
            if(state.processCharAndCheckEol(buffer.nextCharacter()))
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
        currentCellCreator.reset();
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
        currentCellCreator.addToLineExcept(size+skip);
        return true;
    }

    /**
     * Interface for states
     */
    private interface State {
        /**
         * @param c The character to process
         * @return True if end of line has been reached after processing the character, false otherwise.
         */
        boolean processCharAndCheckEol(final char c);
    }


    private class CellCreator {
        private int currentCellOffset =0;
        private int offsetFromEndQuote =0;
        private int ignoresCount = 0;
        private final int[] ignoresAt = new int[128];
        private final StringBuilder stringBuilder = new StringBuilder();

        private void addToLine(){
            addToLineExcept(offsetFromEndQuote);
        }
        /**
         * Adds a completed cell to a line.
         * @param except Number of characters to skip from end while adding cell to line.
         */
        private void addToLineExcept(int except) {
            final int cellStart = buffer.cellMark + currentCellOffset;
            addToLine(cellStart, buffer.cursor-except-cellStart);
        }

        int currentCellSize(){
            return buffer.cursor-(buffer.cellMark+currentCellOffset);
        }

        /**
         * Adds a completed cell to a line.
         * @param offset Begin index
         * @param count Number of characters to add
         */
        void addToLine(int offset, int count) {
            if(count==0)
                currentLine.add(EMPTY_CELL);
            else if (ignoresCount==0)
                currentLine.add(new String(buffer.buffer, offset, count));
            else{
                stringBuilder.delete(0, stringBuilder.length()); // Reset stringBuilder
                for (int i = 0; i<ignoresCount; i++) {
                    int toAdd = ignoresAt[i]-offset;
                    stringBuilder.append(buffer.buffer, offset, toAdd);
                    offset=ignoresAt[i]+1;
                    count-=(1+toAdd);
                }
                stringBuilder.append(buffer.buffer, offset, count);
                currentLine.add(stringBuilder.toString());
            }
        }

        void addEmptyToLine() {
            currentLine.add(EMPTY_CELL);
            buffer.markCell();
            currentCellOffset = 0;
        }

        void ignoreCurrent(){
            if(ignoresCount<=ignoresAt.length)
                ignoresAt[ignoresCount++] = buffer.cursor-1;
        }

        void reset(){
            currentCellOffset=0;
            offsetFromEndQuote=0;
            ignoresCount=0;
        }
    }

    /**
     *
     */
    private final class BeginCellState implements State {
        @Override
        public boolean processCharAndCheckEol(final char c) {
            if (c == quoteChar) {
                state = quotedCellState;
                currentCellCreator.currentCellOffset++;
                return false;
            }
            if (c == lastCellSeparatorChar && cellSeparator.length()==1) {
                currentCellCreator.addEmptyToLine();
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
    private final class UnquotedCellState implements State {
        @Override
        public boolean processCharAndCheckEol(final char c) {
            if( c==lastCellSeparatorChar && tailOfCellMatches(cellSeparator)){
                currentCellCreator.addToLineExcept(cellSeparator.length());
                beginCellState();
                return false;
            }
            return c == lastEolChar && endOfLineAddPending(c, 0);
        }
    }

    /**
     * Quoted cell content expected.
     */
    private final class QuotedCellState implements State {
        @Override
        public boolean processCharAndCheckEol(final char c) {
            if (c == quoteChar) {
                currentCellCreator.offsetFromEndQuote=1;
                state = foundEndQuoteState;
            }
            return false;
        }
    }

    /**
     * End quote was found.
     */
    private final class FoundEndQuoteStateFirstLast implements State {
        @Override
        public boolean processCharAndCheckEol(final char c) {
            if (c==lastCellSeparatorChar && cellSeparator.length()==1) {
                currentCellCreator.addToLineExcept(2);
                beginCellState();
                return false;
            }
            if(c == lastEolChar && endOfLineAddPending(c, 1)){
                return true;
            }
            if (c == quoteChar) {
                currentCellCreator.offsetFromEndQuote=1;
                return false;
            }

            currentCellCreator.offsetFromEndQuote++;
            state = foundEndQuoteWithinState;
            return false;
        }
    }

    /**
     * End quote was found.
     */
    private final class FoundEndQuoteStateRfc implements State {
        @Override
        public boolean processCharAndCheckEol(final char c) {
            if (c==lastCellSeparatorChar && cellSeparator.length()==1) {
                currentCellCreator.addToLineExcept(2);
                beginCellState();
                return false;
            }
            if(c == lastEolChar && endOfLineAddPending(c, 1)){
                return true;
            }
            if (c == quoteChar ) {
                currentCellCreator.ignoreCurrent();
                state = quotedCellState;
                return false;
            }

            currentCellCreator.offsetFromEndQuote++;
            state = foundEndQuoteWithinState;
            return false;
        }
    }

    /**
     * A second quote was found but some other character was found afterwards that was not a single character cell separator or line separator.
     */
    private final class FoundEndQuoteWithinState implements State {
        @Override
        public boolean processCharAndCheckEol(final char c) {
            if (c == quoteChar) {
                state = foundEndQuoteState;
                currentCellCreator.offsetFromEndQuote=1;
                return false;
            }
            if (c == lastCellSeparatorChar && tailOfCellMatches(cellSeparator)) {
                if(cellSeparator.length() == currentCellCreator.offsetFromEndQuote)
                    currentCellCreator.addToLineExcept(cellSeparator.length()+1);
                else
                    currentCellCreator.addToLine(buffer.cellMark, buffer.cursor - buffer.cellMark -cellSeparator.length());
                beginCellState();
                return false;
            }
            if(c == lastEolChar){
                final int eolSize = eolCheck.eolMatchSize(c);
                if( eolSize == currentCellCreator.offsetFromEndQuote){
                    currentCellCreator.addToLineExcept(eolSize+currentCellCreator.offsetFromEndQuote-1);
                    return true;
                }
                else if(eolSize>0) {
                    currentCellCreator.addToLine(buffer.cellMark, buffer.cursor - buffer.cellMark - eolSize);
                    return true;
                }
            }

            currentCellCreator.offsetFromEndQuote++;
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
    final class EolCheckCRLF implements EolCheck {
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
    final class EolCheckCustom implements EolCheck {
        private final String lineSeparator;
        private final char lastLineSeparatorChar;

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
