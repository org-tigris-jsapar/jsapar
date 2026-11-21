package org.jsapar.schema;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SchemaLineParentTest {

    private static CsvSchema orderSchema() {
        return CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Order").withCells("type", "orderId").build())
                .withLine(CsvSchemaLine.builder("OrderLine").withParent("Order").withCells("type", "sku").build())
                .withLine(CsvSchemaLine.builder("Discount").withParent("OrderLine").withCells("type", "percent").build())
                .withLine(CsvSchemaLine.builder("HistoryLog").withParent("Order").withCells("type", "text").build())
                .build();
    }

    @Test
    void parentIsNotSetByDefault() {
        CsvSchemaLine line = CsvSchemaLine.builder("Order").build();
        assertEquals(Optional.empty(), line.getParentLineType());
        CsvSchema schema = CsvSchema.builder().withLine(line).build();
        assertFalse(schema.hasDetailLines());
    }

    @Test
    void withParent() {
        CsvSchema schema = orderSchema();
        assertTrue(schema.hasDetailLines());
        assertEquals(Optional.empty(), schema.getSchemaLine("Order").orElseThrow().getParentLineType());
        assertEquals(Optional.of("Order"), schema.getSchemaLine("OrderLine").orElseThrow().getParentLineType());
        assertEquals(Optional.of("OrderLine"), schema.getSchemaLine("Discount").orElseThrow().getParentLineType());
        assertEquals(Optional.of("Order"), schema.getSchemaLine("HistoryLog").orElseThrow().getParentLineType());
    }

    @Test
    void copyBuilderAndCloneKeepParent() {
        CsvSchemaLine line = CsvSchemaLine.builder("OrderLine").withParent("Order").build();
        assertEquals(Optional.of("Order"), CsvSchemaLine.builder("OrderLine2", line).build().getParentLineType());
        assertEquals(Optional.of("Order"), line.clone().getParentLineType());

        FixedWidthSchemaLine fwLine = FixedWidthSchemaLine.builder("OrderLine").withParent("Order").build();
        assertEquals(Optional.of("Order"), FixedWidthSchemaLine.builder("OrderLine2", fwLine).build().getParentLineType());
    }

    @Test
    void unknownParentIsRejected() {
        CsvSchema.Builder builder = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("OrderLine").withParent("Order").build());
        SchemaException e = assertThrows(SchemaException.class, builder::build);
        assertTrue(e.getMessage().contains("'Order'"), e.getMessage());
    }

    @Test
    void ownParentIsRejected() {
        CsvSchema.Builder builder = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Order").withParent("Order").build());
        assertThrows(SchemaException.class, builder::build);
    }

    @Test
    void parentLoopIsRejected() {
        CsvSchema.Builder builder = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Root").build())
                .withLine(CsvSchemaLine.builder("A").withParent("B").build())
                .withLine(CsvSchemaLine.builder("B").withParent("C").build())
                .withLine(CsvSchemaLine.builder("C").withParent("A").build());
        assertThrows(SchemaException.class, builder::build);

        FixedWidthSchema.Builder fwBuilder = FixedWidthSchema.builder()
                .withLine(FixedWidthSchemaLine.builder("A").withParent("B").build())
                .withLine(FixedWidthSchemaLine.builder("B").withParent("A").build());
        assertThrows(SchemaException.class, fwBuilder::build);
    }

    @Test
    void readParentFromXml() {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<schema xmlns='http://jsapar.tigris.org/JSaParSchema/2.0'>"
                + "<fixedwidthschema lineseparator='\\n'>"
                + "<line linetype='Order'><cell name='type' length='1'><linecondition><match pattern='O'/></linecondition></cell></line>"
                + "<line linetype='OrderLine' parent='Order'><cell name='type' length='1'><linecondition><match pattern='L'/></linecondition></cell></line>"
                + "</fixedwidthschema></schema>";
        Schema<?> schema = Schema.ofXml(new StringReader(xml));
        assertTrue(schema.hasDetailLines());
        assertEquals(Optional.empty(), schema.getSchemaLine("Order").orElseThrow().getParentLineType());
        assertEquals(Optional.of("Order"), schema.getSchemaLine("OrderLine").orElseThrow().getParentLineType());
    }

    @Test
    void unknownParentInXmlIsRejected() {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<schema xmlns='http://jsapar.tigris.org/JSaParSchema/2.0'>"
                + "<csvschema><line linetype='OrderLine' parent='Order'><cell name='sku'/></line></csvschema></schema>";
        assertThrows(SchemaException.class, () -> Schema.ofXml(new StringReader(xml)));
    }

    @Test
    void writeAndReadParentAsXml() {
        StringWriter writer = new StringWriter();
        orderSchema().toXml(writer);
        String xml = writer.toString();
        assertTrue(xml.contains("parent=\"Order\""), xml);

        Schema<?> schema = Schema.ofXml(new StringReader(xml));
        assertEquals(Optional.empty(), schema.getSchemaLine("Order").orElseThrow().getParentLineType());
        assertEquals(Optional.of("Order"), schema.getSchemaLine("OrderLine").orElseThrow().getParentLineType());
        assertEquals(Optional.of("OrderLine"), schema.getSchemaLine("Discount").orElseThrow().getParentLineType());
        assertEquals(Optional.of("Order"), schema.getSchemaLine("HistoryLog").orElseThrow().getParentLineType());
    }
}
