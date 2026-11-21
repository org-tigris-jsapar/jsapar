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
import java.util.Iterator;
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

    @Override
    public long parse(Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer) throws IOException {
        MasterDetailLineConsumer grouping = makeGrouping(lineConsumer, errorConsumer);
        long count = parser.parse(grouping, errorConsumer);
        grouping.flush(); // The last top level line.
        return count;
    }

    @Override
    public Stream<Line> stream(Consumer<JSaParException> errorConsumer) throws IOException {
        Stream<Line> lineStream = parser.stream(errorConsumer);
        Iterator<Line> lines = lineStream.iterator();
        ArrayDeque<Line> completed = new ArrayDeque<>(2);
        MasterDetailLineConsumer grouping = makeGrouping(completed::add, errorConsumer);

        Spliterator<Line> spliterator = new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE,
                Spliterator.ORDERED | Spliterator.NONNULL) {
            private boolean flushed = false;

            @Override
            public boolean tryAdvance(Consumer<? super Line> action) {
                while (completed.isEmpty()) {
                    if (lines.hasNext()) {
                        grouping.accept(lines.next());
                    } else if (!flushed) {
                        flushed = true;
                        grouping.flush(); // The last top level line.
                    } else {
                        return false;
                    }
                }
                action.accept(completed.poll());
                return true;
            }
        };
        return StreamSupport.stream(spliterator, false).onClose(lineStream::close);
    }

    private MasterDetailLineConsumer makeGrouping(Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer) {
        return new MasterDetailLineConsumer(schema, lineConsumer, errorConsumer, parseConfig.getOnOrphanDetailLine());
    }
}
