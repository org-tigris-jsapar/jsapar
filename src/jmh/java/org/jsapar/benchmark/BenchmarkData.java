package org.jsapar.benchmark;

import org.jsapar.TextParser;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell.Alignment;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic schemas and input data shared by the benchmarks.
 */
public final class BenchmarkData {

    /** Number of lines in each generated input. Benchmarks report time per line. */
    static final int LINES = 10_000;

    private static final String[] CITIES = {"Stockholm", "Göteborg", "Malmö", "Uppsala", "Västerås"};

    public enum Format {CSV, FIXED_WIDTH}

    /**
     * <ul>
     *     <li>STRINGS - eight plain string cells, nothing to convert.</li>
     *     <li>TYPED - integer, decimal, local date and boolean cells mixed with strings.</li>
     *     <li>QUOTED - string cells where every third value contains the cell separator or a quote. Only differs from
     *     STRINGS for CSV.</li>
     * </ul>
     */
    public enum Content {STRINGS, TYPED, QUOTED}

    private BenchmarkData() {
    }

    static Schema<?> schema(Format format, Content content) {
        return switch (format) {
            case CSV -> csvSchema(content);
            case FIXED_WIDTH -> fixedWidthSchema(content);
        };
    }

    private static CsvSchema csvSchema(Content content) {
        return CsvSchema.builder().withLine("person", l -> {
            l.withCellSeparator(";");
            if (content == Content.TYPED) {
                return l.withCell("id", c -> c.withType(CellType.INTEGER))
                        .withCell("firstName")
                        .withCell("lastName")
                        .withCell("city")
                        .withCell("amount", c -> c.withType(CellType.DECIMAL))
                        .withCell("date", c -> c.withType(CellType.LOCAL_DATE))
                        .withCell("active", c -> c.withType(CellType.BOOLEAN))
                        .withCell("note");
            }
            return l.withCells("id", "firstName", "lastName", "city", "amount", "date", "active", "note");
        }).build();
    }

    private static FixedWidthSchema fixedWidthSchema(Content content) {
        boolean typed = content == Content.TYPED;
        return FixedWidthSchema.builder().withLine("person", l -> l
                .withCell("id", 8, c -> typed ? c.withType(CellType.INTEGER).withAlignment(Alignment.RIGHT) : c)
                .withCell("firstName", 12)
                .withCell("lastName", 14)
                .withCell("city", 12)
                .withCell("amount", 12, c -> typed ? c.withType(CellType.DECIMAL).withAlignment(Alignment.RIGHT) : c)
                .withCell("date", 10, c -> typed ? c.withType(CellType.LOCAL_DATE) : c)
                .withCell("active", 5, c -> typed ? c.withType(CellType.BOOLEAN) : c)
                .withCell("note", 30)).build();
    }

    /**
     * @return Input text in the given format. The same logical values are used for all formats.
     */
    static String input(Format format, Content content) throws IOException {
        String csv = csvInput(content);
        if (format == Format.CSV)
            return csv;
        // Convert by parsing the csv and composing fixed width, so that values are identical.
        java.io.StringWriter writer = new java.io.StringWriter(LINES * 110);
        org.jsapar.TextComposer composer = new org.jsapar.TextComposer(fixedWidthSchema(content), writer);
        for (Line line : lines(Format.CSV, content, csv))
            composer.composeLine(line);
        return writer.toString();
    }

    private static String csvInput(Content content) {
        StringBuilder sb = new StringBuilder(LINES * 90);
        for (int i = 0; i < LINES; i++) {
            sb.append(i).append(';')
                    .append("Name").append(i % 977).append(';')
                    .append("Surname").append(i % 313).append(';')
                    .append(CITIES[i % CITIES.length]).append(';')
                    .append(i % 10_000).append('.').append(i % 100).append(';')
                    .append("2024-").append(twoDigits(1 + i % 12)).append('-').append(twoDigits(1 + i % 28)).append(';')
                    .append(i % 2 == 0).append(';');
            if (content == Content.QUOTED && i % 3 == 0)
                sb.append(i % 2 == 0 ? "\"with; separator\"" : "\"with \"\"quote\"\" inside\"");
            else
                sb.append("plain note ").append(i);
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String twoDigits(int i) {
        return i < 10 ? "0" + i : Integer.toString(i);
    }

    static List<Line> lines(Format format, Content content, String input) throws IOException {
        List<Line> lines = new ArrayList<>(LINES);
        new TextParser(schema(format, content)).parseForEach(new StringReader(input), lines::add);
        if (lines.size() != LINES)
            throw new IllegalStateException("Expected " + LINES + " lines but parsed " + lines.size());
        return lines;
    }
}
