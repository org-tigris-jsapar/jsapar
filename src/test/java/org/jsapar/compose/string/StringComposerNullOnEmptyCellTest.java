package org.jsapar.compose.string;

import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StringComposerNullOnEmptyCellTest {

    @Test
    public void composeLine() {

        CsvSchema schema = new CsvSchema();
        final CsvSchemaCell cellWithDefault = new CsvSchemaCell("On head");
        cellWithDefault.setDefaultValue("hat");

        schema.addSchemaLine(new CsvSchemaLine("row")
                .addSchemaCell(new CsvSchemaCell("First name"))
                .addSchemaCell(new CsvSchemaCell("Last name"))
                .addSchemaCell(cellWithDefault));
        AtomicBoolean called = new AtomicBoolean(false);
        StringComposerNullOnEmptyCell composer = new StringComposerNullOnEmptyCell(schema, event -> {
            assertEquals(Arrays.asList("name1", null, "hat"), event.stream().collect(Collectors.toList()));
            called.getAndSet(true);
        });
        assertFalse(called.get());
        composer.composeLine(new Line("row").addCell(new StringCell("First name", "name1"))
                .addCell(StringCell.emptyOf("Last name")));
        assertTrue(called.get());
    }
}