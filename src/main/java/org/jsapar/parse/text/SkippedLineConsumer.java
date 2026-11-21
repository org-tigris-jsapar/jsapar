package org.jsapar.parse.text;

import org.jsapar.model.Line;

import java.util.function.Consumer;

/**
 * Internal line consumer that is also told about lines of a known line type that were read but not passed on: lines
 * omitted because of a validation error, see {@link #lineSkipped(String)}, and lines of a line type with ignoreread,
 * see {@link #lineIgnored(String)}. The text parsers call these methods only if the line consumer implements this
 * interface, so there is no overhead for other consumers.
 */
public interface SkippedLineConsumer extends Consumer<Line> {

    /**
     * Called when a line of a known line type was read but omitted, for instance because of a validation error, and
     * therefore not passed on to {@link #accept(Object)}.
     * @param lineType The line type of the skipped line.
     */
    void lineSkipped(String lineType);

    /**
     * Called when a line of a line type with ignoreread was read, and therefore not passed on to
     * {@link #accept(Object)}.
     * @param lineType The line type of the ignored line.
     */
    void lineIgnored(String lineType);

    /**
     * Tells the consumer that a line was skipped, if the consumer implements this interface.
     * @param consumer The line consumer.
     * @param lineType The line type of the skipped line.
     */
    static void notifySkipped(Consumer<? super Line> consumer, String lineType) {
        if (consumer instanceof SkippedLineConsumer skippedLineConsumer)
            skippedLineConsumer.lineSkipped(lineType);
    }

    /**
     * Tells the consumer that a line of a line type with ignoreread was read, if the consumer implements this
     * interface.
     * @param consumer The line consumer.
     * @param lineType The line type of the ignored line.
     */
    static void notifyIgnored(Consumer<? super Line> consumer, String lineType) {
        if (consumer instanceof SkippedLineConsumer skippedLineConsumer)
            skippedLineConsumer.lineIgnored(lineType);
    }
}
