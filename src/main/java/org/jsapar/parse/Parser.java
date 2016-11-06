package org.jsapar.parse;

import org.jsapar.error.ErrorEventListener;

import java.io.IOException;

/**
 * Common interface for all parsers. An instance of a parser is only useful once. You create an instance, initializes it
 * with the event listeners needed, then call {@link #parse()}.
 */
public interface Parser {

    /**
     * Adds a line event listener to this parser. Each line event listener added to this parser will receive an event for
     * each line that has been parsed.
     * @param eventListener The line event listener.
     */
    void addLineEventListener(LineEventListener eventListener);

    /**
     * Adds an error event listener to this parser. Each error event listener added to this parser will receive an event for
     * each error that occurs.
     * @param errorEventListener The error event listener.
     */
    void addErrorEventListener(ErrorEventListener errorEventListener);

    /**
     * Start the parsing. Should only be called once for each parser. Consecutive calls may have unexpected behavior.
     * @throws IOException
     */
    void parse() throws IOException;

}
