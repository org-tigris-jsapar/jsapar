package org.jsapar.parse.csv;

import java.io.IOException;
import java.io.Reader;

/**
 * Internal class that acts as a read buffer while parsing csv.
 */
final class ReadBuffer {
    private final Reader reader;
    private final int maxLoadSize;
    private int lineMark=0;

    final char[] buffer;
    int cellMark=0;
    int cursor=0;
    int bufferSize=0;

    /**
     * @param reader The reader to read from
     * @param bufferSize The buffer size to use.
     * @param maxLoadSize The maximum number of characters to load at a time to the buffer.
     */
    ReadBuffer(Reader reader, int bufferSize, int maxLoadSize) {
        this.reader = reader;
        this.buffer = new char[bufferSize];
        this.maxLoadSize = Math.min(maxLoadSize, bufferSize);

    }

    /**
     * Loads new characters to the buffer.
     * @return The number of new characters added to the buffer. 0 if there was no room in the buffer to load. -1 if end of file was reached.
     * @throws IOException In case of underlying io error.
     */
    @SuppressWarnings("Duplicates")
    int load() throws IOException {
        final int remaining = bufferSize - lineMark;
        int toLoad=buffer.length - remaining;
        if(toLoad < maxLoadSize) {
            if (toLoad==0){
                // Max line size reached. No more space to load.
                return 0;
            }
            // Shift remaining to the left
            System.arraycopy(buffer, lineMark, buffer, 0, remaining);
            cursor -= lineMark;
            cellMark -= lineMark;
            bufferSize -= lineMark;
            lineMark = 0;
        }
        else {
            if(lineMark==bufferSize){
                cursor = 0;
                cellMark = 0;
                bufferSize = 0;
                lineMark = 0;
            }
            toLoad = maxLoadSize;
        }
        final int count = reader.read(buffer, bufferSize, toLoad);
        if(count > 0) {
            bufferSize += count;
        }

        return count;
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
        cellMark = lineMark;
    }

    /**
     * Place a cell mark.
     */
    void markCell(){
        cellMark = cursor;
    }

    /**
     * Resets cursor to last cell mark.
     */
    void resetCell(){
        cursor = cellMark;
    }
}
