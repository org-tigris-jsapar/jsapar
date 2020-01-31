package org.jsapar.compose.string;

import org.jsapar.model.Line;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.util.stream.Stream;

public class StringLineComposerNullOnEmptyCell extends StringLineComposer {
    StringLineComposerNullOnEmptyCell(SchemaLine<? extends SchemaCell> schemaLine) {
        super(schemaLine);
    }

    @Override
    Stream<String> composeStringLine(Line line) {
        return getCellComposers().stream().map(cellComposer ->
             line.getCell(cellComposer.getName())
                    .map(cellComposer::compose)
                    .map(s->s.isEmpty() && !cellComposer.isDefaultValue() ? null : s)
                    .orElseGet(()-> cellComposer.isDefaultValue() ? cellComposer.compose(cellComposer.makeEmptyCell()) : null)

        );
    }
}
