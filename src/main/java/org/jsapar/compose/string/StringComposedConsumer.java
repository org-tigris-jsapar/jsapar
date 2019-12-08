package org.jsapar.compose.string;

import java.util.stream.Stream;

@FunctionalInterface
public interface StringComposedConsumer {

    /**
     * @param cellStream   A stream of String values for all cells in this line.
     * @param lineType The type of the line that was parsed.
     * @param lineNumber The line number within the parsed source.
     */
    void accept(Stream<String> cellStream, String lineType, long lineNumber);
}
