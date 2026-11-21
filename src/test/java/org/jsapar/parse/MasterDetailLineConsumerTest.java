package org.jsapar.parse;

import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MasterDetailLineConsumerTest {

    private final List<Line> result = new ArrayList<>();
    private final List<JSaParException> errors = new ArrayList<>();

    private static final CsvSchema SCHEMA = CsvSchema.builder()
            .withLine(CsvSchemaLine.builder("Header").build())
            .withLine(CsvSchemaLine.builder("Order").build())
            .withLine(CsvSchemaLine.builder("OrderLine").withParent("Order").build())
            .withLine(CsvSchemaLine.builder("Discount").withParent("OrderLine").build())
            .withLine(CsvSchemaLine.builder("HistoryLog").withParent("Order").build())
            .build();

    private MasterDetailLineConsumer grouping(ValidationAction onOrphan) {
        return new MasterDetailLineConsumer(SCHEMA, result::add, errors::add, onOrphan);
    }

    private static Line line(String lineType, long lineNumber) {
        return new Line(lineType, 0, lineNumber);
    }

    @Test
    void oneLevel_twoDetailTypes() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.ERROR);
        Line order = line("Order", 1);
        Line item1 = line("OrderLine", 2);
        Line log = line("HistoryLog", 3);
        Line item2 = line("OrderLine", 4);
        List.of(order, item1, log, item2).forEach(grouping);
        assertTrue(result.isEmpty(), "Nothing is complete until the next top level line or flush");

        grouping.flush();
        assertEquals(List.of(order), result);
        assertEquals(List.of(item1, log, item2), order.getDetails());
        assertTrue(errors.isEmpty());
    }

    @Test
    void lastTopLevelLineOnlyOnFlush() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.ERROR);
        Line order1 = line("Order", 1);
        Line item1 = line("OrderLine", 2);
        Line order2 = line("Order", 3);
        Line item2 = line("OrderLine", 4);
        List.of(order1, item1, order2, item2).forEach(grouping);
        assertEquals(List.of(order1), result);

        grouping.flush();
        assertEquals(List.of(order1, order2), result);
        assertEquals(List.of(item1), order1.getDetails());
        assertEquals(List.of(item2), order2.getDetails());

        grouping.flush();
        assertEquals(2, result.size(), "A second flush does nothing");
    }

    @Test
    void nestedLevels() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.ERROR);
        Line order = line("Order", 1);
        Line item1 = line("OrderLine", 2);
        Line discount1 = line("Discount", 3);
        Line item2 = line("OrderLine", 4);
        Line discount2 = line("Discount", 5);
        Line log = line("HistoryLog", 6);
        List.of(order, item1, discount1, item2, discount2, log).forEach(grouping);
        grouping.flush();

        assertEquals(List.of(order), result);
        assertEquals(List.of(item1, item2, log), order.getDetails());
        assertEquals(List.of(discount1), item1.getDetails());
        assertEquals(List.of(discount2), item2.getDetails());
        assertFalse(log.hasDetails());
    }

    @Test
    void closedLevelIsNotReopened() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.ERROR);
        Line order = line("Order", 1);
        Line item = line("OrderLine", 2);
        Line log = line("HistoryLog", 3);
        Line discount = line("Discount", 4);
        List.of(order, item, log, discount).forEach(grouping);
        grouping.flush();

        // The history log line closes the order line, so the discount line has no open master.
        assertEquals(List.of(item, log), order.getDetails());
        assertFalse(item.hasDetails());
        assertEquals(1, errors.size());
        assertEquals(4, ((LineParseException) errors.getFirst()).getLineNumber());
    }

    @Test
    void topLevelLinesWithoutDetailTypes() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.ERROR);
        Line header = line("Header", 1);
        Line order = line("Order", 2);
        Line unknown = line("Unknown", 3);
        List.of(header, order, unknown).forEach(grouping);
        grouping.flush();
        assertEquals(List.of(header, order, unknown), result);
        assertFalse(header.hasDetails());
    }

    @Test
    void emptyInput() {
        grouping(ValidationAction.ERROR).flush();
        assertTrue(result.isEmpty());
    }

    @Test
    void orphanAtStart_error() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.ERROR);
        Line order = line("Order", 2);
        List.of(line("OrderLine", 1), order).forEach(grouping);
        grouping.flush();

        assertEquals(List.of(order), result);
        assertFalse(order.hasDetails());
        assertEquals(1, errors.size());
        LineParseException error = assertInstanceOf(LineParseException.class, errors.getFirst());
        assertEquals(1, error.getLineNumber());
        assertTrue(error.getMessage().contains("'OrderLine'"), error.getMessage());
    }

    @Test
    void orphanAtWrongLevel_error() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.ERROR);
        Line order = line("Order", 1);
        Line item = line("OrderLine", 3);
        Line discount = line("Discount", 4);
        List.of(order, line("Discount", 2), item, discount).forEach(grouping);
        grouping.flush();

        assertEquals(List.of(item), order.getDetails());
        assertEquals(List.of(discount), item.getDetails());
        assertEquals(1, errors.size());
        assertEquals(2, ((LineParseException) errors.getFirst()).getLineNumber());
    }

    @Test
    void orphan_exception() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.EXCEPTION);
        assertThrows(LineParseException.class, () -> grouping.accept(line("OrderLine", 1)));
    }

    @Test
    void orphan_omitLine() {
        MasterDetailLineConsumer grouping = grouping(ValidationAction.OMIT_LINE);
        grouping.accept(line("OrderLine", 1));
        grouping.flush();
        assertTrue(result.isEmpty());
        assertTrue(errors.isEmpty());
    }
}
