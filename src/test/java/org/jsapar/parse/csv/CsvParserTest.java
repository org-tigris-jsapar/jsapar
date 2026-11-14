package org.jsapar.parse.csv;

import org.jsapar.error.ValidationAction;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.text.TextParseConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvParserTest {

    /**
     * Two line types that are told apart by line condition. The order matters for the empty line tests below, since
     * the matcher of the line type that matched last is tested first.
     */
    private static CsvSchema makeTwoLineTypesSchema() {
        return CsvSchema.builder().withLineSeparator("\n")
                .withLine("P", l -> l.withCellSeparator(";")
                        .withCell("Type", c -> c.withLineCondition(v -> v.equals("P")))
                        .withCell("Value"))
                .withLine("Q", l -> l.withCellSeparator(";")
                        .withCell("Type", c -> c.withLineCondition(v -> v.equals("Q")))
                        .withCell("Value"))
                .build();
    }

    @Test
    public void parse_emptyLineBetweenLinesWithLineCondition() throws IOException {
        for (String text : new String[]{"P;1\n\nP;2\n", "P;1\n\n\nP;2\n", "P;1\nQ;1\n\nP;2\n", "\nP;1\n\nQ;1\n"}) {
            CsvParser parser = new CsvParser(new StringReader(text), makeTwoLineTypesSchema());
            List<Line> lines = new java.util.ArrayList<>();
            List<Exception> errors = new java.util.ArrayList<>();
            parser.parse(lines::add, errors::add);
            assertEquals(List.of(), errors, text);
            assertEquals(text.replace("\n\n", "\n").replace("\n\n", "\n").strip().split("\n").length, lines.size(), text);
        }
        CsvParser parser = new CsvParser(new StringReader("P;1\n\nP;2\n"), makeTwoLineTypesSchema());
        List<Line> lines = new java.util.ArrayList<>();
        parser.parse(lines::add, e -> {throw e;});
        assertEquals("2", lines.get(1).getCell("Value").map(Cell::getStringValue).orElse(null));
        assertEquals(3, lines.get(1).getLineNumber());
    }

    @Test
    public void stream_emptyLineBetweenLinesWithLineCondition() throws IOException {
        CsvParser parser = new CsvParser(new StringReader("P;1\n\nP;2\n"), makeTwoLineTypesSchema());
        List<Line> lines = parser.stream(e -> {throw e;}).toList();
        assertEquals(2, lines.size());
        assertEquals("2", lines.get(1).getCell("Value").map(Cell::getStringValue).orElse(null));
    }

    @Test
    public void parse_undefinedLineAfterEmptyLineIsStillReported() throws IOException {
        TextParseConfig config = new TextParseConfig();
        config.setOnUndefinedLineType(ValidationAction.ERROR);
        CsvParser parser = new CsvParser(new StringReader("P;1\n\nX;2\nP;3\n"), makeTwoLineTypesSchema(), config);
        List<Line> lines = new java.util.ArrayList<>();
        List<Exception> errors = new java.util.ArrayList<>();
        parser.parse(lines::add, errors::add);
        assertEquals(2, lines.size());
        assertEquals(1, errors.size());
        assertEquals(true, errors.getFirst().getMessage().contains("line number 3"), errors.getFirst().getMessage());
    }

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
    public void parse_CommaAsSeparator_defaultBySchema() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withDefaultCellSeparator(",")
                .withLine("a", l->l
                        .withFirstLineAsSchema(true)
                        .withCell("gg")
                        .withCell("type")
                )
                .build();

        String text = "type,gg  \nx,yyy\nA,BBB\nX,YYY";
        TextParseConfig config = new TextParseConfig();
        CsvParser parser = new CsvParser(new StringReader(text), schema, config);
        parser.parse(line -> {
                    if(line.getLineNumber() == 2)
                        assertEquals("yyy", line.getCell("gg").map(Cell::getStringValue).orElse(null));
                    if(line.getLineNumber() == 3)
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