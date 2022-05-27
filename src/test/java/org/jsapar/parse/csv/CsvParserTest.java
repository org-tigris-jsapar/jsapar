package org.jsapar.parse.csv;

import org.jsapar.error.ValidationAction;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.text.TextParseConfig;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CsvParserTest {

    @Test
    public void parse_firstLineAsSchema_mandatory_cell_not_present() throws IOException {
        CsvSchema schema = makePersonCsvSchema();

        String text = "firstName\njohn";
        CsvParser parser = new CsvParser(new StringReader(text), schema);
        AtomicInteger errorCount = new AtomicInteger(0);
        parser.parse(line -> {
                    assertEquals(1, errorCount.get()); // Should report an error before first line is parsed.
                    assertEquals(1, line.size());
                    assertEquals("john", line.getCell("firstName").map(Cell::getStringValue).orElse(null));
                },
                errorEvent -> errorCount.incrementAndGet());
        assertEquals(1, errorCount.get());
    }

    @Test
    public void parse_IgnoreUndefinedLine() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLine("a", l->l
                        .withCell("type", c->c.withLineCondition(v->v.equals("A")))
                        .withCell("gg"))
                .build();

        String text = "x;yyy\nA;BBB\nX;YYY";
        TextParseConfig config = new TextParseConfig();
        config.setOnUndefinedLineType(ValidationAction.OMIT_LINE);
        CsvParser parser = new CsvParser(new StringReader(text), schema, config);
        parser.parse(line -> {
                    assertEquals(2, line.getLineNumber());
                    assertEquals("BBB", line.getCell("gg").map(Cell::getStringValue).orElse(null));
                },
                e -> {throw e;});
    }

    @Test
    public void stream_IgnoreUndefinedLine() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLine("a", l->l
                        .withCell("type", c->c.withLineCondition(v->v.equals("A")))
                        .withCell("gg"))
                .build();

        String text = "x;yyy\nA;BBB\nX;YYY\nA;CCC";
        TextParseConfig config = new TextParseConfig();
        config.setOnUndefinedLineType(ValidationAction.OMIT_LINE);
        CsvParser parser = new CsvParser(new StringReader(text), schema, config);
        List<Line> result = parser.stream(e -> {
            throw e;
        }).collect(Collectors.toList());
        assertEquals(2, result.size());
        assertEquals("BBB", result.get(0).getCell("gg").map(Cell::getStringValue).orElse(null));

    }


    @Test
    public void parse_firstLineAsSchema_empty_cell_name() throws IOException {
        CsvSchema schema = makePersonCsvSchema();

        String text = "firstName;;lastName\njohn;L;doe";
        CsvParser parser = new CsvParser(new StringReader(text), schema);
        AtomicInteger errorCount = new AtomicInteger(0);
        parser.parse(line -> {
                    assertEquals(0, errorCount.get()); // Should report an error before first line is parsed.
                    assertEquals(2, line.size());
                    assertEquals("john", line.getCell("firstName").map(Cell::getStringValue).orElse(null));
                    assertEquals("doe", line.getCell("lastName").map(Cell::getStringValue).orElse(null));
                },
                errorEvent -> errorCount.incrementAndGet());
        assertEquals(0, errorCount.get());
    }

    public CsvSchema makePersonCsvSchema() {
        return CsvSchema.builder()
                    .withLine(CsvSchemaLine.builder("Person")
                            .withFirstLineAsSchema(true)
                            .withCell("firstName")
                            .withCell(CsvSchemaCell.builder("lastName").withMandatory(true).build())
                            .build())
                    .build();
    }

}