package org.jsapar.parse.csv;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class CsvParserTest {

    @Test
    public void parse_firstLineAsSchema_mandatory_cell_not_present() throws IOException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine("Person");
        schema.addSchemaLine(schemaLine);
        schemaLine.setFirstLineAsSchema(true);
        schemaLine.addSchemaCell(new CsvSchemaCell("firstName", CellType.STRING));
        final CsvSchemaCell lastNameCell = new CsvSchemaCell("lastName", CellType.STRING);
        lastNameCell.setMandatory(true);
        schemaLine.addSchemaCell(lastNameCell);

        String text = "firstName\njohn";
        CsvParser parser = new CsvParser(new StringReader(text), schema);
        AtomicInteger errorCount = new AtomicInteger(0);
        parser.parse(event -> {
                    assertEquals(1, errorCount.get()); // "Should report an error before first line is parsed.
                    assertEquals(1, event.getLine().size());
                    assertEquals("john", event.getLine().getCell("firstName").map(Cell::getStringValue).orElse(null));
                },
                errorEvent -> errorCount.incrementAndGet());
        assertEquals(1, errorCount.get());
    }
}