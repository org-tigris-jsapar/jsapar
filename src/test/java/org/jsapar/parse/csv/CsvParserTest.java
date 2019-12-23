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
        schemaLine.addSchemaCell( CsvSchemaCell.builder("firstName").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("lastName").withMandatory(true).build());

        String text = "firstName\njohn";
        CsvParser parser = new CsvParser(new StringReader(text), schema);
        AtomicInteger errorCount = new AtomicInteger(0);
        parser.parse(line -> {
                    assertEquals(1, errorCount.get()); // "Should report an error before first line is parsed.
                    assertEquals(1, line.size());
                    assertEquals("john", line.getCell("firstName").map(Cell::getStringValue).orElse(null));
                },
                errorEvent -> errorCount.incrementAndGet());
        assertEquals(1, errorCount.get());
    }

    @Test
    public void parse_firstLineAsSchema_empty_cell_name() throws IOException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine("Person");
        schema.addSchemaLine(schemaLine);
        schemaLine.setFirstLineAsSchema(true);
        schemaLine.addSchemaCell( CsvSchemaCell.builder("firstName").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("lastName").withMandatory(true).build());

        String text = "firstName;;lastName\njohn;L;doe";
        CsvParser parser = new CsvParser(new StringReader(text), schema);
        AtomicInteger errorCount = new AtomicInteger(0);
        parser.parse(line -> {
                    assertEquals(0, errorCount.get()); // "Should report an error before first line is parsed.
                    assertEquals(2, line.size());
                    assertEquals("john", line.getCell("firstName").map(Cell::getStringValue).orElse(null));
                    assertEquals("doe", line.getCell("lastName").map(Cell::getStringValue).orElse(null));
                },
                errorEvent -> errorCount.incrementAndGet());
        assertEquals(0, errorCount.get());
    }

}