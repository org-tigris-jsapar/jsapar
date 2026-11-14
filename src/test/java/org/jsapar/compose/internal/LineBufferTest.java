package org.jsapar.compose.internal;

import org.jsapar.TextComposer;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.text.Format;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class LineBufferTest {

    @Test
    void writeAndTransfer() throws IOException {
        LineBuffer buffer = new LineBuffer();
        buffer.write('a');
        buffer.write("bcd", 1, 2);
        buffer.write(new char[]{'x', 'e', 'y'}, 1, 1);
        buffer.append("fg").append('h').append("xijx", 1, 3);
        buffer.write("k".repeat(1000)); // Forces growth
        StringWriter target = new StringWriter();
        buffer.writeTo(target);
        assertEquals("acdefghij" + "k".repeat(1000), target.toString());
        assertEquals(0, buffer.size());
        buffer.writeTo(target);
        assertEquals(1009, target.toString().length());
    }

    @Test
    void clearDiscards() throws IOException {
        LineBuffer buffer = new LineBuffer();
        buffer.write("abc");
        buffer.clear();
        StringWriter target = new StringWriter();
        buffer.writeTo(target);
        assertEquals("", target.toString());
    }

    /**
     * Each line must be written to the writer before composeLine returns, so that writes made directly to the writer
     * by the caller between lines keep their order.
     */
    @Test
    void composeLine_writesBeforeReturning() {
        CsvSchema schema = CsvSchema.builder().withLine("l", l -> l.withCells("a", "b")).build();
        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        composer.composeLine(new Line("l").addCell(new StringCell("a", "1")).addCell(new StringCell("b", "2")));
        assertEquals("1;2", writer.toString());
        writer.write("|");
        composer.composeLine(new Line("l").addCell(new StringCell("a", "3")).addCell(new StringCell("b", "4")));
        assertEquals("1;2|" + System.lineSeparator() + "3;4", writer.toString());
    }

    @Test
    void composeLine_fixedWidth_writesBeforeReturning() {
        FixedWidthSchema schema = FixedWidthSchema.builder().withLine("l", l -> l.withCell("a", 3).withCell("b", 2)).build();
        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        composer.composeLine(new Line("l").addCell(new StringCell("a", "1")).addCell(new StringCell("b", "2")));
        assertEquals("1  2 ", writer.toString());
    }

    /**
     * If composing a line fails, nothing of that line is written and the next line is not affected.
     */
    @Test
    void composeLine_failure_discardsPartialLine() {
        Format<String> failing = new Format<>() {
            @Override
            public CellType cellType() {
                return CellType.STRING;
            }

            @Override
            public String parse(String stringValue) {
                return stringValue;
            }

            @Override
            public String format(Object value) {
                if ("fail".equals(value))
                    throw new IllegalArgumentException("Failing");
                return value.toString();
            }
        };
        CsvSchema schema = CsvSchema.builder().withLine("l", l -> l.withCell("a").withCell("b", c -> c.withFormat(failing))).build();
        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        assertThrows(IllegalArgumentException.class, () ->
                composer.composeLine(new Line("l").addCell(new StringCell("a", "partial")).addCell(new StringCell("b", "fail"))));
        assertEquals("", writer.toString());
        composer.composeLine(new Line("l").addCell(new StringCell("a", "1")).addCell(new StringCell("b", "2")));
        assertFalse(writer.toString().contains("partial"));
        assertTrue(writer.toString().endsWith("1;2"));
    }
}
