package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Line;
import org.jsapar.parse.LineParseException;
import org.jsapar.schema.Schema;
import org.jsapar.text.TextParseConfig;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parsing of master-detail lines using the example files examples/09_*.
 */
class MasterDetailParseTest {

    private static Schema<?> schema(String fileName) throws IOException {
        try (Reader reader = new FileReader("examples/" + fileName, StandardCharsets.UTF_8)) {
            return Schema.ofXml(reader);
        }
    }

    private static List<Line> parseForEach(String schemaFile, String dataFile) throws IOException {
        TextParser parser = new TextParser(schema(schemaFile));
        List<Line> lines = new ArrayList<>();
        try (Reader reader = new FileReader("examples/" + dataFile, StandardCharsets.UTF_8)) {
            long count = parser.parseForEach(reader, lines::add);
            assertEquals(7, count, "Count is the number of parsed lines, including detail lines");
        }
        return lines;
    }

    private static List<Line> stream(String schemaFile, String dataFile) throws IOException {
        TextParser parser = new TextParser(schema(schemaFile));
        try (Reader reader = new FileReader("examples/" + dataFile, StandardCharsets.UTF_8);
             Stream<Line> stream = parser.stream(reader)) {
            return stream.toList();
        }
    }

    private static void assertOrders(List<Line> orders) {
        assertEquals(2, orders.size());

        Line order1 = orders.get(0);
        assertEquals("Order", order1.getLineType());
        assertEquals(1, order1.getLineNumber());
        assertEquals(1001L, order1.getNonEmptyCellValue("orderId", Long.class).orElseThrow());
        assertEquals(LocalDate.of(2026, 11, 2), order1.getNonEmptyCellValue("date", LocalDate.class).orElseThrow());
        assertEquals(List.of("OrderLine", "HistoryLog", "OrderLine"),
                order1.getDetails().stream().map(Line::getLineType).toList());

        List<Line> items = order1.getDetails("OrderLine");
        assertEquals("SKU-1", items.get(0).getNonEmptyCellValue("sku", String.class).orElseThrow());
        assertEquals(2, items.get(0).getLineNumber());
        assertEquals(1, items.get(0).getDetails().size());
        Line discount = items.get(0).getDetails().getFirst();
        assertEquals("Discount", discount.getLineType());
        assertEquals(10L, discount.getNonEmptyCellValue("percent", Long.class).orElseThrow());
        assertEquals("SKU-7", items.get(1).getNonEmptyCellValue("sku", String.class).orElseThrow());
        assertFalse(items.get(1).hasDetails());
        assertEquals("Created by web shop",
                order1.getDetails("HistoryLog").getFirst().getNonEmptyCellValue("text", String.class).orElseThrow());

        Line order2 = orders.get(1);
        assertEquals(1002L, order2.getNonEmptyCellValue("orderId", Long.class).orElseThrow());
        assertEquals(1, order2.getDetails().size());
        assertEquals(5L, order2.getDetails().getFirst().getNonEmptyCellValue("quantity", Long.class).orElseThrow());
    }

    @Test
    void parseForEach_csv() throws IOException {
        assertOrders(parseForEach("09_CsvSchemaMasterDetail.xml", "09_Orders.csv"));
    }

    @Test
    void parseForEach_fixedWidth() throws IOException {
        assertOrders(parseForEach("09_FixedWidthSchemaMasterDetail.xml", "09_Orders.txt"));
    }

    @Test
    void stream_csv() throws IOException {
        assertOrders(stream("09_CsvSchemaMasterDetail.xml", "09_Orders.csv"));
    }

    @Test
    void stream_fixedWidth() throws IOException {
        assertOrders(stream("09_FixedWidthSchemaMasterDetail.xml", "09_Orders.txt"));
    }

    @Test
    void stream_isLazy() throws IOException {
        TextParser parser = new TextParser(schema("09_CsvSchemaMasterDetail.xml"));
        try (Stream<Line> stream = parser.stream(new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8))) {
            Line first = stream.findFirst().orElseThrow();
            assertEquals(1001L, first.getNonEmptyCellValue("orderId", Long.class).orElseThrow());
            assertEquals(3, first.getDetails().size());
        }
    }

    @Test
    void emptyInput() throws IOException {
        TextParser parser = new TextParser(schema("09_CsvSchemaMasterDetail.xml"));
        List<Line> lines = new ArrayList<>();
        assertEquals(0, parser.parseForEach(new StringReader(""), lines::add));
        assertTrue(lines.isEmpty());
        assertEquals(0, parser.stream(new StringReader("")).count());
    }

    @Test
    void orphanDetailLine_errorByDefault() throws IOException {
        TextParser parser = new TextParser(schema("09_CsvSchemaMasterDetail.xml"));
        List<JSaParException> errors = new ArrayList<>();
        parser.setErrorConsumer(errors::add);
        List<Line> lines = new ArrayList<>();
        parser.parseForEach(new StringReader("L;SKU-1;2\nO;1001;2026-11-02;ACME\nL;SKU-2;3\n"), lines::add);

        assertEquals(1, lines.size());
        assertEquals(1, lines.getFirst().getDetails().size());
        assertEquals(1, errors.size());
        assertEquals(1, ((LineParseException) errors.getFirst()).getLineNumber());
    }

    @Test
    void orphanDetailLine_exception() throws IOException {
        TextParseConfig config = new TextParseConfig();
        config.setOnOrphanDetailLine(ValidationAction.EXCEPTION);
        TextParser parser = new TextParser(schema("09_CsvSchemaMasterDetail.xml"), config);
        assertThrows(LineParseException.class,
                () -> parser.parseForEach(new StringReader("D;10\n"), l -> fail("No line expected")));
    }

    private static final String ORDER_1 = "O;1001;2026-11-02;ACME\n";
    private static final String ORDER_2_INSUFFICIENT = "O;1002\n";

    private static TextParser parserWithConfig(TextParseConfig config, List<JSaParException> errors) throws IOException {
        TextParser parser = new TextParser(schema("09_CsvSchemaMasterDetail.xml"), config);
        parser.setErrorConsumer(errors::add);
        return parser;
    }

    private static TextParseConfig omitInsufficient() {
        TextParseConfig config = new TextParseConfig();
        config.setOnLineInsufficient(ValidationAction.OMIT_LINE);
        return config;
    }

    @Test
    void omittedMasterLine_detailsAreOrphans() throws IOException {
        String input = ORDER_1 + "L;SKU-1;2\n" + ORDER_2_INSUFFICIENT + "L;SKU-3;5\nD;10\n";
        List<JSaParException> errors = new ArrayList<>();
        TextParser parser = parserWithConfig(omitInsufficient(), errors);
        List<Line> lines = new ArrayList<>();
        parser.parseForEach(new StringReader(input), lines::add);

        assertEquals(1, lines.size());
        assertEquals(1, lines.getFirst().getDetails().size(), "Detail lines of the omitted order are not added to the previous order");
        assertEquals(2, errors.size());
        assertEquals(List.of(4L, 5L), errors.stream().map(e -> ((LineParseException) e).getLineNumber()).toList());

        errors.clear();
        try (Stream<Line> stream = parser.stream(new StringReader(input))) {
            List<Line> streamed = stream.toList();
            assertEquals(1, streamed.size());
            assertEquals(1, streamed.getFirst().getDetails().size());
        }
        assertEquals(2, errors.size());
    }

    @Test
    void omittedDetailLine_closesItsLevel() throws IOException {
        String input = ORDER_1 + "L;SKU-1;2\nL;SKU-2\nD;10\nH;Log\n";
        List<JSaParException> errors = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        parserWithConfig(omitInsufficient(), errors).parseForEach(new StringReader(input), lines::add);

        assertEquals(1, lines.size());
        Line order = lines.getFirst();
        assertEquals(List.of("OrderLine", "HistoryLog"), order.getDetails().stream().map(Line::getLineType).toList());
        assertFalse(order.getDetails().getFirst().hasDetails(), "The discount does not belong to the previous order line");
        assertEquals(1, errors.size());
        assertEquals(4, ((LineParseException) errors.getFirst()).getLineNumber());
    }

    @Test
    void omittedMasterLine_fixedWidth() throws IOException {
        TextParseConfig config = new TextParseConfig();
        config.setOnLineOverflow(ValidationAction.OMIT_LINE);
        TextParser parser = new TextParser(schema("09_FixedWidthSchemaMasterDetail.xml"), config);
        List<JSaParException> errors = new ArrayList<>();
        parser.setErrorConsumer(errors::add);
        List<String> input;
        try (Reader reader = new FileReader("examples/09_Orders.txt", StandardCharsets.UTF_8)) {
            input = new java.io.BufferedReader(reader).lines().toList();
        }
        // Make the second order line too long so that it is omitted.
        String text = String.join("\n", input.subList(0, 5)) + "\n" + input.get(5) + "EXTRA\n" + input.get(6) + "\n";
        List<Line> lines = new ArrayList<>();
        parser.parseForEach(new StringReader(text), lines::add);

        assertEquals(1, lines.size());
        assertEquals(3, lines.getFirst().getDetails().size());
        assertEquals(1, errors.size(), errors.toString());
    }

    @Test
    void stream_skipsEmptyLines() throws IOException {
        TextParser parser = new TextParser(schema("09_CsvSchemaMasterDetail.xml"));
        try (Stream<Line> stream = parser.stream(new StringReader(ORDER_1 + "\nL;SKU-1;2\n\n" + ORDER_1))) {
            List<Line> lines = stream.toList();
            assertEquals(2, lines.size());
            assertEquals(1, lines.getFirst().getDetails().size());
        }
    }

    @Test
    void exceptionOnLaterLine_completedMasterIsPassedOn() throws IOException {
        TextParseConfig config = new TextParseConfig();
        config.setOnLineInsufficient(ValidationAction.EXCEPTION);
        TextParser parser = new TextParser(schema("09_CsvSchemaMasterDetail.xml"), config);
        String input = ORDER_1 + "L;SKU-1;2\n" + ORDER_2_INSUFFICIENT;

        List<Line> lines = new ArrayList<>();
        assertThrows(LineParseException.class, () -> parser.parseForEach(new StringReader(input), lines::add));
        assertEquals(1, lines.size());
        assertEquals(1, lines.getFirst().getDetails().size());

        try (Stream<Line> stream = parser.stream(new StringReader(input))) {
            java.util.Iterator<Line> it = stream.iterator();
            assertTrue(it.hasNext());
            assertEquals(1, it.next().getDetails().size());
            assertThrows(LineParseException.class, it::hasNext);
        }
    }

    @Test
    void addedSchemaLineWithUnknownParent_failsWhenParsing() throws IOException {
        Schema<?> schema = schema("09_CsvSchemaMasterDetail.xml");
        ((org.jsapar.schema.CsvSchema) schema).addSchemaLine(
                org.jsapar.schema.CsvSchemaLine.builder("Note").withParent("Ordr").build());
        assertThrows(org.jsapar.schema.SchemaException.class, schema::validateParentLineTypes);
        TextParser parser = new TextParser(schema);
        assertThrows(org.jsapar.schema.SchemaException.class,
                () -> parser.parseForEach(new StringReader(ORDER_1), l -> {}));
        assertThrows(org.jsapar.schema.SchemaException.class, () -> parser.stream(new StringReader(ORDER_1)));
    }

    @Test
    void changedParentFormingLoop_failsWhenParsing() throws IOException {
        Schema<?> schema = schema("09_CsvSchemaMasterDetail.xml");
        schema.getSchemaLine("Order").orElseThrow().setParentLineType("Discount");
        TextParser parser = new TextParser(schema);
        assertThrows(org.jsapar.schema.SchemaException.class,
                () -> parser.parseForEach(new StringReader(ORDER_1), l -> {}));
    }

    private static List<Line> parseIgnoringRead(String schemaFile, String dataFile, String lineType,
                                                List<JSaParException> errors, boolean stream) throws IOException {
        Schema<?> schema = schema(schemaFile);
        schema.getSchemaLine(lineType).orElseThrow().setIgnoreRead(true);
        TextParser parser = new TextParser(schema);
        parser.setErrorConsumer(errors::add);
        try (Reader reader = new FileReader("examples/" + dataFile, StandardCharsets.UTF_8)) {
            if (stream) {
                try (Stream<Line> lines = parser.stream(reader)) {
                    return lines.toList();
                }
            }
            List<Line> lines = new ArrayList<>();
            parser.parseForEach(reader, lines::add);
            return lines;
        }
    }

    @Test
    void ignoreReadMaster_detailsIgnoredWithoutErrors() throws IOException {
        for (String[] files : new String[][]{{"09_CsvSchemaMasterDetail.xml", "09_Orders.csv"},
                {"09_FixedWidthSchemaMasterDetail.xml", "09_Orders.txt"}}) {
            for (boolean stream : new boolean[]{false, true}) {
                List<JSaParException> errors = new ArrayList<>();
                assertEquals(List.of(), parseIgnoringRead(files[0], files[1], "Order", errors, stream));
                assertEquals(List.of(), errors, files[0]);
            }
        }
    }

    @Test
    void ignoreReadDetail_itsDetailsIgnoredWithoutErrors() throws IOException {
        for (String[] files : new String[][]{{"09_CsvSchemaMasterDetail.xml", "09_Orders.csv"},
                {"09_FixedWidthSchemaMasterDetail.xml", "09_Orders.txt"}}) {
            for (boolean stream : new boolean[]{false, true}) {
                List<JSaParException> errors = new ArrayList<>();
                List<Line> orders = parseIgnoringRead(files[0], files[1], "OrderLine", errors, stream);
                assertEquals(List.of(), errors, files[0]);
                assertEquals(2, orders.size());
                assertEquals(List.of("HistoryLog"),
                        orders.getFirst().getDetails().stream().map(Line::getLineType).toList(),
                        "Order lines and their discounts are ignored, the history log is kept");
                assertFalse(orders.get(1).hasDetails());
            }
        }
    }
}
