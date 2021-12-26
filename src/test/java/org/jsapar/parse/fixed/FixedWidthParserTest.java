package org.jsapar.parse.fixed;

import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Cell;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.model.LineUtils;
import org.jsapar.parse.DocumentBuilderLineConsumer;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.parse.csv.CsvParser;
import org.jsapar.schema.CsvSchema;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class FixedWidthParserTest {

    @Test
    public final void testParse_Flat() throws IOException {
        String toParse = "JonasStenbergFridaStenberg";
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLineSeparator("")
                .withLine("Person", line->line
                        .withOccurs(2)
                        .withCell("First name", 5)
                        .withCell("Last name", 8)
                ).build();

        Reader reader = new StringReader(toParse);

        Document doc = build(reader, schema);

        assertEquals("Jonas", LineUtils.getStringCellValue(doc.getLine(0), "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(doc.getLine(0), "Last name"));

        assertEquals("Frida", LineUtils.getStringCellValue(doc.getLine(1), "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(doc.getLine(1), "Last name"));
    }

    @Test
    public final void testStream() throws IOException {
        String toParse = "JonasStenbergFridaStenberg";
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLineSeparator("")
                .withLine("Person", line->line
                        .withOccurs(2)
                        .withCell("First name", 5)
                        .withCell("Last name", 8)
                ).build();

        Reader reader = new StringReader(toParse);

        FixedWidthParser parser = new FixedWidthParser(reader, schema, new TextParseConfig());
        List<Line> lines = parser.stream(false, e -> {
            throw e;
        }).collect(Collectors.toList());

        assertEquals("Jonas", LineUtils.getStringCellValue(lines.get(0), "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(lines.get(0), "Last name"));

        assertEquals("Frida", LineUtils.getStringCellValue(lines.get(1), "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(lines.get(1), "Last name"));
    }

    @Test
    public void parse_IgnoreUndefinedLine() throws IOException {
        FixedWidthSchema schema = FixedWidthSchema.builder()
                .withLine("a", l->l
                        .withCell("type", 1, c->c.withLineCondition(v->v.equals("A")))
                        .withCell("gg", 3))
                .build();

        String text = "xyyy\nABBB\nXYYY";
        TextParseConfig config = new TextParseConfig();
        config.setOnUndefinedLineType(ValidationAction.OMIT_LINE);
        FixedWidthParser parser = new FixedWidthParser(new StringReader(text), schema, config);
        parser.parse(line -> {
                    assertEquals(2, line.getLineNumber());
                    assertEquals("BBB", line.getCell("gg").map(Cell::getStringValue).orElse(null));
                },
                e -> {throw e;});
    }

    private Document build(Reader reader, FixedWidthSchema schema) throws IOException {
        FixedWidthParser parser = new FixedWidthParser(reader, schema, new TextParseConfig());
        DocumentBuilderLineConsumer builder = new DocumentBuilderLineConsumer();
        parser.parse(builder, new ExceptionErrorConsumer());
        return builder.getDocument();
    }


}
