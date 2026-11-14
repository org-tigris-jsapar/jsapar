package org.jsapar.compose.fixed.pad;

import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.io.Writer;

public interface Pad {
    /**
     * Writes a truncated value, cut in the correct end.
     * @param writer The writer to write to.
     * @param value The value to write.
     * @throws IOException In case of io error.
     */
    void fit(Writer writer, String value) throws IOException;

    /**
     * Pads the supplied value to fill out the correct length.
     * @param writer  The writer to write to.
     * @param value The value to write.
     * @throws IOException In case of io error.
     */
    void pad(Writer writer, String value) throws IOException;

    /**
     * Creates a pad instance for specified alignment.
     * @param alignment  The alignment.
     * @param padCharacter The character to pad with.
     * @param length The expected length to pad or fit to.
     * @return A Pad instance of supplied parameters.
     */
    static Pad ofAlignment(FixedWidthSchemaCell.Alignment alignment, char padCharacter, int length){
        if(length <=0 )
            return new PadNone();
        return switch (alignment) {
            case LEFT -> new PadRight(padCharacter, length);
            case CENTER -> new PadBoth(padCharacter, length);
            case RIGHT -> new PadLeft(padCharacter, length);
        };
    }
}
