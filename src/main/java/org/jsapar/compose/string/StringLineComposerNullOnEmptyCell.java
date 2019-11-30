package org.jsapar.compose.string;

import org.jsapar.model.Line;
import org.jsapar.schema.SchemaLine;

import java.util.stream.Stream;

public class StringLineComposerNullOnEmptyCell extends StringLineComposer {
    StringLineComposerNullOnEmptyCell(SchemaLine schemaLine) {
        super(schemaLine);
    }

    @Override
    Stream<String> composeStringLine(Line line) {
        return super.composeStringLine(line).map(s->s.isEmpty() ? null : s);
    }
}
