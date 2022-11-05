package org.jsapar.compose.string;

import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.util.stream.Collectors;

/**
 * Composer that calls a  {@link StringComposedConsumer} for each line that is composed.
 * <p>
 * This implementation will return null for all cells of type EmptyCell from the parsed input and
 * where there is no default value.
 * <p>
 * The {@link StringComposedConsumer} provides a
 * {@link java.util.stream.Stream} of {@link java.lang.String} for the current {@link org.jsapar.model.Line} where each
 * string is matches the cell in a schema. Each cell is formatted according to provided
 * {@link org.jsapar.schema.Schema}.
 */
public final class StringComposerNullOnEmptyCell extends StringComposer {

    @Deprecated
    public StringComposerNullOnEmptyCell(Schema<? extends SchemaLine<? extends SchemaCell>>  schema, StringComposedEventListener composedEventListener) {
        super(composedEventListener, schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, StringLineComposerNullOnEmptyCell::new)));
    }

    public StringComposerNullOnEmptyCell(Schema<? extends SchemaLine<? extends SchemaCell>>  schema, StringComposedConsumer composedEventConsumer) {
        super(composedEventConsumer, schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, StringLineComposerNullOnEmptyCell::new)));
    }

}
