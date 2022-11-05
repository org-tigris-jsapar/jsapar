package org.jsapar.compose.fixed.pad;

import java.io.IOException;
import java.io.Writer;

final class PadRight implements Pad {
    private final int length;
    private final Filler filler;

    PadRight(char padCharacter, int length) {
        this.length = length;
        filler = new Filler(padCharacter, length);
    }

    @Override
    public void fit(Writer writer, String sValue) throws IOException {
        writer.write(sValue, 0, length);
    }

    @Override
    public void pad(Writer writer, String value) throws IOException {
        writer.write(value);
        filler.fill(writer,  this.length - value.length());
    }
}
