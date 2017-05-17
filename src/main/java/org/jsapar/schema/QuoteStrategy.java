package org.jsapar.schema;

/**
 * Defines how to quote csv cells.
 */
public enum QuoteStrategy {
    /**
     * If quote character is specified: Quote cells only when needed.
     * Otherwise replace illegal characters with non-breakable space.
     */
    SMART,

    /**
     * Never quote. Keep cell content as is even when there are illegal characters.
     */
    NEVER,

    /**
     * Don't quote but replace illegal characters with non-breakable space.
     */
    REPLACE,

    /**
     * Always quote, regardless of content.
     */
    ALWAYS
}
