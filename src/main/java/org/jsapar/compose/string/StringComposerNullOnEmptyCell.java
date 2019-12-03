package org.jsapar.compose.string;

import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.util.stream.Collectors;

/**
 * Composer that creates {@link StringComposedEvent} for each line that is composed.
 * <p>
 * This implementation will return null for all cells of type EmptyCell from the parsed input and
 * where there is no default value.
 * <p>
 * The {@link StringComposedEvent} provides a
 * {@link java.util.stream.Stream} of {@link String} for the current {@link Line} where each
 * string is matches the cell in a schema. Each cell is formatted according to provided
 * {@link Schema}.
 */
public class StringComposerNullOnEmptyCell extends StringComposer {

    public StringComposerNullOnEmptyCell(Schema schema, StringComposedEventListener composedEventListener) {
        super(composedEventListener, schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, StringLineComposerNullOnEmptyCell::new)));
    }


}
