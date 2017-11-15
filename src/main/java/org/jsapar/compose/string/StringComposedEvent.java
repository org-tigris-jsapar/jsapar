package org.jsapar.compose.string;

import java.util.stream.Stream;

/**
 * Generated once for each line that is composed.
 */
public class StringComposedEvent {
    private final String lineType;
    private final Stream<String> line;

    public StringComposedEvent(String lineType, Stream<String> line) {
        this.lineType = lineType;
        this.line = line;
    }

    /**
     * Can only be called once for each instance of this event.
     *
     * @return A stream of String values for all cells in this line.
     */
    public Stream<String> stream() {
        return line;
    }

    /**
     * @return The type of the line that generated this event.
     */
    public String getLineType() {
        return lineType;
    }
}
