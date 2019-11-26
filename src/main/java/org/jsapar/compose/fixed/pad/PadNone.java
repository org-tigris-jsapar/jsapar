package org.jsapar.compose.fixed.pad;

import java.io.Writer;

/**
 * Never writes anything to writer.
 */
public class PadNone implements Pad {

    @Override
    public void fit(Writer writer, String value) {
    }

    @Override
    public void pad(Writer writer, String value) {
    }

}
