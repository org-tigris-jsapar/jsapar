package org.jsapar.compose.string;

import org.jsapar.compose.Composer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Composer that calls a {@link StringComposedConsumer} for each line that is composed.
 * <p>
 * The {@link StringComposedConsumer} provides a
 * {@link java.util.stream.Stream} of {@link java.lang.String} for the current {@link org.jsapar.model.Line} where each
 * string is matches the cell in a schema. Each cell is formatted according to provided
 * {@link org.jsapar.schema.Schema}.
 */
public class StringComposer implements Composer {

    private final StringComposedConsumer stringComposedConsumer;
    private final Map<String, StringLineComposer> lineComposers;


    public StringComposer(Schema<? extends SchemaLine<? extends SchemaCell>> schema, StringComposedConsumer stringComposedConsumer) {
        this(stringComposedConsumer, schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, StringLineComposer::new)));
    }

    @Deprecated
    public StringComposer(Schema<? extends SchemaLine<? extends SchemaCell>> schema, StringComposedEventListener composedEventListener) {
        this(composedEventListener,
                schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, StringLineComposer::new)));
    }

    @Deprecated
    StringComposer(StringComposedEventListener composedEventListener, Map<String, StringLineComposer> lineComposers) {
        this.stringComposedConsumer = (line, lineType, lineNumber) -> composedEventListener.stringComposedEvent(new StringComposedEvent(lineType, lineNumber, line));
        this.lineComposers = lineComposers;
    }

    StringComposer(StringComposedConsumer composedEventListener, Map<String, StringLineComposer> lineComposers) {
        this.stringComposedConsumer = composedEventListener;
        this.lineComposers = lineComposers;
    }

    @Override
    public boolean composeLine(Line line) {
        StringLineComposer lineComposer = lineComposers.get(line.getLineType());
        if (lineComposer == null || lineComposer.isIgnoreWrite())
            return false;
        stringComposedConsumer.accept(lineComposer.composeStringLine(line),
                line.getLineType(),
                line.getLineNumber()
        );
        return true;
    }

    @Override
    public void setErrorConsumer(Consumer<JSaParException> errorConsumer) {
        //        this.errorEventListener = errorListener; // Not used.
    }


}
