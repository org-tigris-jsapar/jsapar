package org.jsapar.parse.fixed;

import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * Internal class that acts as a read buffer while parsing csv.
 */
@SuppressWarnings("Duplicates")
class ReadBuffer {
    private static final String EMPTY_STRING = "";
    private final Reader reader;
    private final LineLoader lineLoader;
    private int maxLoadSize;
    private int lineMark=0;
    private int lineEnd=0;
    private int nextLineBegin=0;

    final char[] buffer;
    int cursor=0;
    int bufferSize=0;

    /**
     * @param reader The reader to read from
     * @param bufferSize The buffer size to use.
     * @param maxLoadSize The maximum number of characters to load a a time to the buffer.
     */
    ReadBuffer(String lineSeparator, Reader reader, int bufferSize, int maxLoadSize) {
        this.reader = reader;
        this.buffer = new char[bufferSize];
        this.maxLoadSize = Math.min(maxLoadSize, bufferSize);
        this.lineLoader = makeLineLoader(lineSeparator);
    }

    private LineLoader makeLineLoader(String lineSeparator) {
        if(lineSeparator.isEmpty())
            return new LineLoaderFlat();
        if(Arrays.asList("\n", "\r\n").contains(lineSeparator)){
            return new LineLoaderCRLF();
        }
        return new LineLoaderCustom(lineSeparator);
    }

    public int loadLine(int lineSize) throws IOException {
        return lineLoader.loadLine(lineSize);
    }
    /**
     * Loads new characters to the buffer.
     * @return The number of new characters added to the buffer. 0 if there was no room in the buffer to load. -1 if end of file was reached.
     * @throws IOException In case of underlying io error.
     * @param required
     */
    int load(int required) throws IOException {
        final int remaining = bufferSize - lineMark;
        int toLoad=buffer.length - remaining;
        int maxLoad = Math.max(maxLoadSize, required);
        if(toLoad < maxLoad) {
            if (toLoad==0){
                // Max line size reached. No more space to load.
                return 0;
            }
            // Shift remaining to the left
            System.arraycopy(buffer, lineMark, buffer, 0, remaining);
            cursor -= lineMark;
            bufferSize -= lineMark;
            lineEnd -= lineEnd;
            nextLineBegin -= nextLineBegin;
            lineMark = 0;
        }
        else {
            if(lineMark==bufferSize){
                cursor = 0;
                bufferSize = 0;
                lineMark = 0;
            }
            toLoad = maxLoad;
        }
        final int count = reader.read(buffer, cursor, toLoad);
        if(count > 0) {
            bufferSize += count;
        }

        return count;
    }

    boolean nextCharacterNotLoaded(){
        return cursor >= bufferSize;
    }

    /**
     * Returns the character that the cursor points to and increments the cursor to next position.
     * @return The character that the cursor points to.
     */
    char nextCharacter(){
        return buffer[cursor++];
    }
    /**
     * Place a line mark.
     */
    void markLine(){
        lineMark = cursor;
    }

    /**
     * Reset cursor to last line mark.
     */
    void resetLine(){
        cursor = lineMark;
    }

    void skip(int toSkipp){
        cursor+=toSkipp;
    }
    /**
     * @return The string value of the cell read from the reader at the position pointed to by the offset. Null if end
     * of input stream was reached.
     * @throws IOException If there is a problem while reading the input reader.
     */
    String readToString(FixedWidthSchemaCell schemaCell, int offset) throws IOException {
        int length = schemaCell.getLength(); // The actual length
        if(length == 0)
            return EMPTY_STRING;

        int required = cursor+offset+length - bufferSize;
        if(required > 0){
            int loaded = load(required);
            if(loaded < 0)
                return null; // EOF
        }
        int readOffset = lineMark+offset;
        char padCharacter = schemaCell.getPadCharacter();
        if(schemaCell.getAlignment() != FixedWidthSchemaCell.Alignment.LEFT) {
            while (readOffset < length && buffer[readOffset] == padCharacter) {
                readOffset++;
            }
        }
        if(schemaCell.getAlignment() != FixedWidthSchemaCell.Alignment.RIGHT) {
            while (length > readOffset && buffer[length - 1] == padCharacter) {
                length--;
            }
        }
        length -= readOffset;
        if(length == 0){
            if(padCharacter == '0' && schemaCell.getCellFormat().getCellType().isNumber())
                return String.valueOf(padCharacter);
            return EMPTY_STRING;
        }
        return new String(buffer, readOffset, length);
    }


    interface LineLoader{
        int loadLine(int lineSize) throws IOException;
    }

    /**
     *
     */
    class LineLoaderFlat implements LineLoader{
        @Override
        public int loadLine(int lineSize) throws IOException {
            int spaceRequired = lineMark+lineSize-bufferSize;
            if(spaceRequired>0) {
                int loaded = load(spaceRequired);
                lineSize = loaded > lineSize ? lineSize : loaded;
            }
            lineEnd = lineMark + lineSize;
            nextLineBegin = lineEnd;
            return lineSize;
        }
    }

    private class LineLoaderCRLF implements LineLoader {
        @Override
        public int loadLine(int lineSize) throws IOException {
            return 0;
        }
    }

    private class LineLoaderCustom implements LineLoader {
        private String lineSeparator;

        public LineLoaderCustom(String lineSeparator) {
            this.lineSeparator = lineSeparator;
        }

        @Override
        public int loadLine(int lineSize) throws IOException {
            return 0;
        }
    }
}
