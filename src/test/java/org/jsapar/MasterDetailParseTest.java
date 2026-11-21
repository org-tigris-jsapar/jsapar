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
}
