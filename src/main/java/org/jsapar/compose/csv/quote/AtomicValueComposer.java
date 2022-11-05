package org.jsapar.compose.csv.quote;

import java.io.IOException;
import java.io.Writer;

/**
 */
final public class AtomicValueComposer implements ValueComposer {

    @Override
    public void writeValue(Writer writer, String value) throws IOException {
        writer.write(value);
    }
}
