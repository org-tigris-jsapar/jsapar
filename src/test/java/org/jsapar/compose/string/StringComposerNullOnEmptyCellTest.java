package org.jsapar.compose.string;

import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StringComposerNullOnEmptyCellTest {

    @Test
    public void composeLine() {

        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("row")
                        .withCell("First name")
                        .withCell("Last name")
                        .withCell("On head", c->c.withDefaultValue("hat"))
                        .withCell("Pet", c->c.withDefaultValue(""))
                        .build())
                .build();
        AtomicBoolean called = new AtomicBoolean(false);
        StringComposerNullOnEmptyCell composer = new StringComposerNullOnEmptyCell(schema, (cells, lineType, lineNumber)  -> {
            assertEquals(Arrays.asList("name1", null, "hat", ""), cells.collect(Collectors.toList()));
            called.getAndSet(true);
        });
        assertFalse(called.get());
        composer.composeLine(new Line("row")
                .addCell(new StringCell("First name", "name1"))
                .addCell(StringCell.emptyOf("Last name")));
        assertTrue(called.get());
    }
}