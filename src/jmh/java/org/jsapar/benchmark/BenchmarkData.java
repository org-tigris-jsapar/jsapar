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

    enum Format {CSV, FIXED_WIDTH}

    /**
     * <ul>
     *     <li>STRINGS - eight plain string cells, nothing to convert.</li>
     *     <li>TYPED - integer, decimal, local date and boolean cells mixed with strings.</li>
     *     <li>QUOTED - string cells where every third value contains the cell separator or a quote. CSV only, since
     *     fixed width has no quoting.</li>
     *     <li>WIDE - the TYPED cells repeated four times, 32 cells per line.</li>
     * </ul>
     */
    enum Content {STRINGS, TYPED, QUOTED, WIDE}

    /**
     * The format and content combinations to benchmark. A single parameter instead of one for format and one for
     * content, since JMH runs every combination of parameters and quoted content only makes sense for CSV.
     */
    public enum Data {
        CSV_STRINGS(Format.CSV, Content.STRINGS),
        CSV_TYPED(Format.CSV, Content.TYPED),
        CSV_QUOTED(Format.CSV, Content.QUOTED),
        CSV_WIDE(Format.CSV, Content.WIDE),
        FIXED_WIDTH_STRINGS(Format.FIXED_WIDTH, Content.STRINGS),
        FIXED_WIDTH_TYPED(Format.FIXED_WIDTH, Content.TYPED),
        FIXED_WIDTH_WIDE(Format.FIXED_WIDTH, Content.WIDE);

        final Format format;
        final Content content;

        Data(Format format, Content content) {
            this.format = format;
            this.content = content;
        }

        Schema<?> schema() {
            return BenchmarkData.schema(format, content);
        }

        String input() throws IOException {
            return BenchmarkData.input(format, content);
        }

        List<Line> lines(String input) throws IOException {
            return BenchmarkData.lines(format, content, input);
        }
    }

    private BenchmarkData() {
    }

    static Schema<?> schema(Format format, Content content) {
        return switch (format) {
            case CSV -> csvSchema(content);
            case FIXED_WIDTH -> fixedWidthSchema(content);
        };
    }

    private static boolean typed(Content content) {
        return content == Content.TYPED || content == Content.WIDE;
    }

    private static int groups(Content content) {
        return content == Content.WIDE ? 4 : 1;
    }

    private static String suffix(int group) {
        return group == 0 ? "" : "_" + group;
    }

    private static CsvSchema csvSchema(Content content) {
        boolean typed = typed(content);
        return CsvSchema.builder().withLine("person", l -> {
            l.withCellSeparator(";");
            for (int g = 0; g < groups(content); g++) {
                String s = suffix(g);
                l.withCell("id" + s, c -> typed ? c.withType(CellType.INTEGER) : c)
                        .withCell("firstName" + s)
                        .withCell("lastName" + s)
                        .withCell("city" + s)
                        .withCell("amount" + s, c -> typed ? c.withType(CellType.DECIMAL) : c)
                        .withCell("date" + s, c -> typed ? c.withType(CellType.LOCAL_DATE) : c)
                        .withCell("active" + s, c -> typed ? c.withType(CellType.BOOLEAN) : c)
                        .withCell("note" + s);
            }
            return l;
        }).build();
    }

    private static FixedWidthSchema fixedWidthSchema(Content content) {
        boolean typed = typed(content);
        return FixedWidthSchema.builder().withLine("person", l -> {
            for (int g = 0; g < groups(content); g++) {
                String s = suffix(g);
                l.withCell("id" + s, 8, c -> typed ? c.withType(CellType.INTEGER).withAlignment(Alignment.RIGHT) : c)
                        .withCell("firstName" + s, 12)
                        .withCell("lastName" + s, 14)
                        .withCell("city" + s, 12)
                        .withCell("amount" + s, 12, c -> typed ? c.withType(CellType.DECIMAL).withAlignment(Alignment.RIGHT) : c)
                        .withCell("date" + s, 10, c -> typed ? c.withType(CellType.LOCAL_DATE) : c)
                        .withCell("active" + s, 5, c -> typed ? c.withType(CellType.BOOLEAN) : c)
                        .withCell("note" + s, 30);
            }
            return l;
        }).build();
    }

    /**
     * @return Input text in the given format. The same logical values are used for all formats.
     */
    static String input(Format format, Content content) throws IOException {
        String csv = csvInput(content);
        if (format == Format.CSV)
            return csv;
        // Convert by parsing the csv and composing fixed width, so that values are identical.
        java.io.StringWriter writer = new java.io.StringWriter(LINES * 110 * groups(content));
        org.jsapar.TextComposer composer = new org.jsapar.TextComposer(fixedWidthSchema(content), writer);
        for (Line line : lines(Format.CSV, content, csv))
            composer.composeLine(line);
        return writer.toString();
    }

    private static String csvInput(Content content) {
        StringBuilder sb = new StringBuilder(LINES * 90 * groups(content));
        for (int i = 0; i < LINES; i++) {
            for (int g = 0; g < groups(content); g++) {
                if (g > 0)
                    sb.append(';');
                sb.append(i).append(';')
                        .append("Name").append((i + g) % 977).append(';')
                        .append("Surname").append((i + g) % 313).append(';')
                        .append(CITIES[(i + g) % CITIES.length]).append(';')
                        .append(i % 10_000).append('.').append((i + g) % 100).append(';')
                        .append("2024-").append(twoDigits(1 + (i + g) % 12)).append('-').append(twoDigits(1 + i % 28)).append(';')
                        .append(i % 2 == 0).append(';');
                if (content == Content.QUOTED && i % 3 == 0)
                    sb.append(i % 2 == 0 ? "\"with; separator\"" : "\"with \"\"quote\"\" inside\"");
                else
                    sb.append("plain note ").append(i);
            }
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
