package org.jsapar.parse.text;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Internal interface for text parser that can parse text based on a schema.
 */
public interface TextSchemaParser {

    /**
     * This method should only be called by a TextParseTask class. Don't use this
     * directly in your code. Use a TextParseTask instead.
     * <p>
     * Sends line parse events to the supplied lineEventListener while parsing.
     *
     * @param lineConsumer      The line consumer which will receive events for each parsed line.
     * @param errorConsumer The error consumer that will receive events for each error.
     * @return Number of lines parsed
     * @throws IOException If there is an error reading from the input reader.
     */
    long parse(Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer) throws IOException;

}
