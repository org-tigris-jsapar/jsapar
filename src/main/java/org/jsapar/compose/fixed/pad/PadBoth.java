package org.jsapar.compose.fixed.pad;

import java.io.IOException;
import java.io.Writer;

public class PadBoth implements Pad {
    private final int length;
    private final Filler filler;

    public PadBoth(char fillCharacter, int length) {
        this.length = length;
        filler = new Filler(fillCharacter, length);
    }

    @Override
    public void fit(Writer writer, String value) throws IOException {
        writer.write(value, (value.length()-length)/2, length);
    }

    @Override
    public void pad(Writer writer, String value) throws IOException {
        final int toFill = length - value.length();
        int remaining = toFill / 2;
        filler.fill(writer, remaining);
        writer.write(value);
        filler.fill(writer, toFill - remaining);
    }
}
