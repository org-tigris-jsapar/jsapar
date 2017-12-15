package org.jsapar.compose.string;

import org.jsapar.compose.CellComposer;
import org.jsapar.compose.Composer;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.util.stream.Stream;

/**
 * Composer that creates {@link StringComposedEvent} for each line that is composed.
 * <p>
 * The {@link StringComposedEvent} provides a
 * {@link java.util.stream.Stream} of {@link java.lang.String} for the current {@link org.jsapar.model.Line} where each
 * string is matches the cell in a schema. Each cell is formatted according to provided
 * {@link org.jsapar.schema.Schema}.
 */
public class StringComposer implements Composer {

    private       ErrorEventListener errorEventListener;
    private final Schema             schema;
    private final static CellComposer cellComposer = new CellComposer();
    private final StringComposedEventListener stringComposedEventListener;

    public StringComposer(Schema schema, StringComposedEventListener composedEventListener) {
        this.schema = schema;
        this.stringComposedEventListener = composedEventListener;
    }

    @Override
    public boolean composeLine(Line line) {
        return schema.getSchemaLine(line.getLineType())
                .filter(schemaLine -> !schemaLine.isIgnoreWrite())
                .map(schemaLine -> stringComposedEvent(new StringComposedEvent(
                        line.getLineType(),
                        line.getLineNumber(),
                        composeStringLine(schemaLine, line))))
                .orElse(false);
    }

    private Stream<String> composeStringLine(SchemaLine schemaLine, Line line) {
        return schemaLine.stream().map(schemaCell -> cellComposer
                .format(line.getCell(schemaCell.getName()).orElse(null), schemaCell));
    }

    @Override
    public void setErrorEventListener(ErrorEventListener errorListener) {
        this.errorEventListener = errorListener;
    }

    private boolean stringComposedEvent(StringComposedEvent event) {
        if (this.stringComposedEventListener != null) {
            stringComposedEventListener.stringComposedEvent(event);
            return true;
        }
        return false;
    }

}
