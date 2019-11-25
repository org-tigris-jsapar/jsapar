package org.jsapar.compose.fixed.pad;

import java.io.IOException;
import java.io.Writer;

/**
 * Writes same character specified number of times.
 */
public class Filler {
    /**
     * Prepare a buffer, makes faster writing.
     */
    private final char[] fillBuffer;

    /**
     * @param fillCharacter The character to fill with.
     * @param length The maximum length to fill.
     */
    public Filler(char fillCharacter, int length) {
        fillBuffer = new char[Math.max(0,length)];
        for(int i = 0; i<length; i++){
            fillBuffer[i] = fillCharacter;
        }
    }

    /**
     * @param writer The writer to fill to.
     * @param toFill The number of characters to write.
     * @throws IOException If fails to write to writer
     * @throws IndexOutOfBoundsException if toFill is larger than initialized length.
     */
    public void fill(Writer writer, int toFill) throws IOException, IndexOutOfBoundsException {
        writer.write(fillBuffer, 0, toFill);
    }
}
