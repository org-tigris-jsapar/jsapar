package org.jsapar.compose.fixed.pad;

import java.io.IOException;
import java.io.Writer;

final class PadLeft implements Pad {
    private final int length;
    private final Filler filler;

    PadLeft(char fillCharacter, int length) {
        this.length = length;
        filler = new Filler(fillCharacter, length);
    }

    @Override
    public void fit(Writer writer, String value) throws IOException {
        writer.write(value, value.length() - length, length);
    }

    @Override
    public void pad(Writer writer, String value) throws IOException {
        filler.fill(writer, this.length - value.length());
        writer.write(value);
    }
}
