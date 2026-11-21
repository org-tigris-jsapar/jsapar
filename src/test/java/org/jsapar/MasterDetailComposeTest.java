package org.jsapar;

import org.jsapar.bean.BeanMap;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.jsapar.schema.StringSchema;
import org.jsapar.schema.StringSchemaLine;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Composing master-detail lines, using the example files examples/09_*.
 */
class MasterDetailComposeTest {

    private static Schema<?> schema(String fileName) throws IOException {
        try (Reader reader = new FileReader("examples/" + fileName, StandardCharsets.UTF_8)) {
            return Schema.ofXml(reader);
        }
    }

    private static String example(String fileName) throws IOException {
        return Files.readString(Path.of("examples", fileName), StandardCharsets.UTF_8);
    }

    private static String textToText(String schemaFile, String dataFile) throws IOException {
        Schema<?> schema = schema(schemaFile);
        StringWriter writer = new StringWriter();
        try (Reader reader = new FileReader("examples/" + dataFile, StandardCharsets.UTF_8)) {
            assertEquals(7, new Text2TextConverter(schema, schema).convert(reader, writer));
        }
        return writer.toString();
    }

    @Test
    void textToText_csv_sameAsInput() throws IOException {
        assertEquals(example("09_Orders.csv").stripTrailing(), textToText("09_CsvSchemaMasterDetail.xml", "09_Orders.csv"));
    }

    @Test
    void textToText_fixedWidth_sameAsInput() throws IOException {
        assertEquals(example("09_Orders.txt").stripTrailing(),
                textToText("09_FixedWidthSchemaMasterDetail.xml", "09_Orders.txt").stripTrailing());
    }

    @Test
    void textComposer_skipsDetailsOfLineNotWritten() {
        CsvSchema schema = CsvSchema.builder()
                .withLineSeparator("\n")
                .withLine(CsvSchemaLine.builder("Order").withCells("id").build())
                .withLine(CsvSchemaLine.builder("Hidden").withIgnoreWrite(true).withCells("id").build())
                .withLine(CsvSchemaLine.builder("OrderLine").withParent("Order").withCells("sku").build())
                .build();
        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        composer.composeLine(new Line("Order").addCell(new StringCell("id", "1"))
                .addDetail(new Line("OrderLine").addCell(new StringCell("sku", "A")))
                .addDetail(new Line("OrderLine").addCell(new StringCell("sku", "B"))));
        composer.composeLine(new Line("Hidden").addCell(new StringCell("id", "2"))
                .addDetail(new Line("OrderLine").addCell(new StringCell("sku", "C"))));
        composer.composeLine(new Line("Order").addCell(new StringCell("id", "3")));
        assertEquals("1\nA\nB\n3", writer.toString());
    }

    private static StringSchema stringSchema() {
        return StringSchema.builder()
                .withLine(StringSchemaLine.builder("Order").withCells("orderId").build())
                .withLine(StringSchemaLine.builder("OrderLine").withCells("sku").build())
                .withLine(StringSchemaLine.builder("Discount").withCells("percent").build())
                .withLine(StringSchemaLine.builder("HistoryLog").withCells("text").build())
                .build();
    }

    private static final List<String> STRING_LINES = List.of("1001", "SKU-1", "10", "Created by web shop", "SKU-7", "1002", "SKU-3");

    @Test
    void textToString_convertForEach() throws IOException {
        Text2StringConverter converter = new Text2StringConverter(schema("09_CsvSchemaMasterDetail.xml"), stringSchema());
        List<String> lines = new ArrayList<>();
        try (Reader reader = new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8)) {
            converter.convertForEach(reader, (Stream<String> cells) -> lines.add(cells.collect(Collectors.joining(","))));
        }
        assertEquals(STRING_LINES, lines);
    }

    @Test
    void textToString_stream() throws IOException {
        Text2StringConverter converter = new Text2StringConverter(schema("09_CsvSchemaMasterDetail.xml"), stringSchema());
        try (Reader reader = new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8);
             Stream<Stream<String>> stream = converter.stream(reader)) {
            assertEquals(STRING_LINES, stream.map(cells -> cells.collect(Collectors.joining(","))).toList());
        }
    }

    @Test
    void textToXml_detailsAsSiblingLines() throws IOException {
        Text2XmlConverter converter = new Text2XmlConverter(schema("09_CsvSchemaMasterDetail.xml"));
        StringWriter writer = new StringWriter();
        try (Reader reader = new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8)) {
            converter.convert(reader, writer);
        }
        List<String> lineTypes = new ArrayList<>();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("<line linetype=\"(\\w+)\"").matcher(writer.toString());
        while (m.find())
            lineTypes.add(m.group(1));
        assertEquals(List.of("Order", "OrderLine", "Discount", "HistoryLog", "OrderLine", "Order", "OrderLine"), lineTypes);
    }

    @Test
    void textToBeansToText() throws IOException {
        Schema<?> schema = schema("09_CsvSchemaMasterDetail.xml");
        List<MasterDetailBeanTest.Order> orders = new ArrayList<>();
        try (Reader reader = new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8)) {
            new Text2BeanConverter<>(schema, MasterDetailBeanTest.Order.class)
                    .convertForEach(reader, (MasterDetailBeanTest.Order order) -> orders.add(order));
        }

        StringWriter writer = new StringWriter();
        BeanCollection2TextConverter<MasterDetailBeanTest.Order> converter =
                new BeanCollection2TextConverter<>(schema, BeanMap.ofClass(MasterDetailBeanTest.Order.class));
        assertEquals(2, converter.convert(orders, writer));
        // Detail types are written property by property: order lines before history logs.
        assertEquals("""
                O;1001;2026-11-02;ACME
                L;SKU-1;2
                D;10
                L;SKU-7;1
                H;Created by web shop
                O;1002;2026-11-03;Globex
                L;SKU-3;5""", writer.toString());
    }

    @Test
    void beanToText_nullCollectionsAndUnmappedClasses() {
        MasterDetailBeanTest.Order order = new MasterDetailBeanTest.Order();
        order.setOrderId(1);
        order.setDate(java.time.LocalDate.of(2026, 1, 2));
        order.setCustomer("ACME");
        order.setHistory(new ArrayList<>());
        order.getHistory().add(null);

        StringWriter writer = new StringWriter();
        Bean2TextConverter<MasterDetailBeanTest.Order> converter = new Bean2TextConverter<>(
                schemaUnchecked(), BeanMap.ofClass(MasterDetailBeanTest.Order.class), writer);
        assertTrue(converter.convert(order));
        assertEquals("O;1;2026-01-02;ACME", writer.toString());
    }

    private static Schema<?> schemaUnchecked() {
        try {
            return schema("09_CsvSchemaMasterDetail.xml");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void lineManipulators_seeDetailLinesInFileOrder() throws IOException {
        Schema<?> schema = schema("09_CsvSchemaMasterDetail.xml");
        Text2TextConverter converter = new Text2TextConverter(schema, schema);
        List<String> seen = new ArrayList<>();
        converter.addLineManipulator(line -> seen.add(line.getLineType()));
        // Drops the order line with quantity 1, the second order with all its details, and changes the discounts.
        converter.addLineManipulator(line -> !line.getLineType().equals("OrderLine")
                || line.getNonEmptyCellValue("quantity", Long.class).orElseThrow() != 1L);
        converter.addLineManipulator(line -> !line.getNonEmptyCellValue("orderId", Long.class).map(id -> id == 1002L).orElse(false));
        converter.addLineManipulator(line -> {
            if (line.getLineType().equals("Discount"))
                line.putCell(new org.jsapar.model.IntegerCell("percent", 20));
            return true;
        });
        StringWriter writer = new StringWriter();
        converter.convert(new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8), writer);

        assertEquals(List.of("Order", "OrderLine", "Discount", "HistoryLog", "OrderLine", "Order"), seen,
                "Each line is manipulated once in file order, until a manipulator returns false for it or its master");
        assertEquals("O;1001;2026-11-02;ACME\nL;SKU-1;2\nD;20\nH;Created by web shop", writer.toString());
    }

    @Test
    void lineManipulators_beanToText() throws IOException {
        Schema<?> schema = schema("09_CsvSchemaMasterDetail.xml");
        BeanMap beanMap = BeanMap.ofSchema(schema, BeanMap.ofClass(MasterDetailBeanTest.Order.class));
        List<MasterDetailBeanTest.Order> orders = new ArrayList<>();
        Text2BeanConverter<MasterDetailBeanTest.Order> text2Bean = new Text2BeanConverter<>(schema, beanMap);
        text2Bean.convertForEach(new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8), (MasterDetailBeanTest.Order order) -> orders.add(order));

        StringWriter writer = new StringWriter();
        Bean2TextConverter<MasterDetailBeanTest.Order> bean2Text = new Bean2TextConverter<>(schema, beanMap, writer);
        bean2Text.addLineManipulator(line -> !line.getLineType().equals("Discount"));
        orders.forEach(bean2Text::convert);
        assertFalse(writer.toString().contains("\nD;"), writer.toString());
        assertTrue(writer.toString().contains("L;SKU-1;2"), writer.toString());
    }
}
