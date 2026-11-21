package org.jsapar.parse.text;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.MasterDetailLineConsumer;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Internal text parser that groups detail lines under their master lines, see {@link MasterDetailLineConsumer}, for
 * schemas where some line types have a parent line type. Wraps the format specific parser.
 */
final class MasterDetailTextParser implements TextSchemaParser {

    private final TextSchemaParser parser;
    private final Schema<? extends SchemaLine<? extends SchemaCell>> schema;
    private final TextParseConfig parseConfig;

    MasterDetailTextParser(TextSchemaParser parser,
                           Schema<? extends SchemaLine<? extends SchemaCell>> schema,
                           TextParseConfig parseConfig) {
        this.parser = parser;
        this.schema = schema;
        this.parseConfig = parseConfig;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If parsing stops with an exception, the current top level line is passed on, with the detail lines read so
     * far, before the exception is thrown.
     */
    @Override
    public long parse(Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer) throws IOException {
        Grouping grouping = new Grouping(makeGrouping(lineConsumer, errorConsumer));
        long count;
        try {
            count = parser.parse(grouping, errorConsumer);
        } catch (IOException | RuntimeException e) {
            flushAfter(grouping.consumer(), e);
            throw e;
        }
        grouping.consumer().flush(); // The last top level line.
        return count;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If parsing stops with an exception, the current top level line is returned, with the detail lines read so
     * far, before the exception is thrown.
     */
    @Override
    public Stream<Line> stream(Consumer<JSaParException> errorConsumer) throws IOException {
        ArrayDeque<Line> completed = new ArrayDeque<>(2);
        Grouping grouping = new Grouping(makeGrouping(completed::add, errorConsumer));
        Stream<Line> lineStream = parser.stream(errorConsumer);
        // Not Stream.iterator(): the format parsers may advance without passing on a line, for instance for an empty
        // line, which an iterator would take as the end of the stream.
        Spliterator<Line> lines = lineStream.spliterator();

        Spliterator<Line> spliterator = new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE,
                Spliterator.ORDERED | Spliterator.NONNULL) {
            private boolean done = false;
            private RuntimeException pending = null;

            @Override
            public boolean tryAdvance(Consumer<? super Line> action) {
                while (completed.isEmpty()) {
                    if (pending != null) {
                        RuntimeException e = pending;
                        pending = null;
                        done = true;
                        throw e;
                    }
                    if (done)
                        return false;
                    try {
                        if (!lines.tryAdvance(grouping)) {
                            done = true;
                            grouping.consumer().flush(); // The last top level line.
                        }
                    } catch (RuntimeException e) {
                        // Pass on the current top level line first, then throw on the next call.
                        flushAfter(grouping.consumer(), e);
                        pending = e;
                    }
                }
                action.accept(completed.poll());
                return true;
            }
        };
        return StreamSupport.stream(spliterator, false).onClose(lineStream::close);
    }

    /**
     * Passes on the current top level line after parsing failed. An exception while doing so is added as suppressed
     * to the original exception.
     */
    private static void flushAfter(MasterDetailLineConsumer grouping, Exception e) {
        try {
            grouping.flush();
        } catch (RuntimeException flushException) {
            e.addSuppressed(flushException);
        }
    }

    private MasterDetailLineConsumer makeGrouping(Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer) {
        return new MasterDetailLineConsumer(schema, lineConsumer, errorConsumer, parseConfig.getOnOrphanDetailLine());
    }

    /**
     * Passes lines to the grouping consumer, and also tells it about lines that the format parser skipped.
     */
    private record Grouping(MasterDetailLineConsumer consumer) implements SkippedLineConsumer {
        @Override
        public void accept(Line line) {
            consumer.accept(line);
        }

        @Override
        public void lineSkipped(String lineType) {
            consumer.lineSkipped(lineType);
        }

        @Override
        public void lineIgnored(String lineType) {
            consumer.lineIgnored(lineType);
        }
    }
}
