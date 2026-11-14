package org.jsapar.parse.csv;

import org.jsapar.schema.QuoteSyntax;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Compares {@link CsvLineReaderStates} with {@link ReferenceCsvLineReaderStates}, a frozen copy of the implementation
 * before it was optimized, on a large number of random inputs. Any difference in cells, line numbers, EOF state or
 * exceptions is a regression.
 */
class CsvLineReaderDifferentialTest {

    private static final String[] CELL_SEPARATORS = {";", ",", "\t", "||", "::", ";-;"};
    private static final String[] LINE_SEPARATORS = {"\n", "\r\n", "|", "##", "\r\n\r\n"};
    private static final char[] QUOTE_CHARS = {'"', '\'', 0};
    private static final int[] MAX_LINE_LENGTHS = {16, 37, 64, 8192};
    private static final String ALPHABET = "abcXYZ019 åä\t";

    @Test
    void randomInputs_sameResultAsReference() throws IOException {
        Random random = new Random(20261114);
        int compared = 0;
        for (int i = 0; i < 6000; i++) {
            String cellSeparator = pick(random, CELL_SEPARATORS);
            String lineSeparator = pick(random, LINE_SEPARATORS);
            char quoteChar = QUOTE_CHARS[random.nextInt(QUOTE_CHARS.length)];
            QuoteSyntax quoteSyntax = random.nextBoolean() ? QuoteSyntax.RFC4180 : QuoteSyntax.FIRST_LAST;
            int maxLineLength = MAX_LINE_LENGTHS[random.nextInt(MAX_LINE_LENGTHS.length)];
            boolean readAhead = random.nextInt(4) != 0;
            boolean useReset = random.nextInt(5) == 0;
            String input = randomInput(random, cellSeparator, lineSeparator, quoteChar == 0 ? '"' : quoteChar);
            long seed = random.nextLong();

            String description = "input=[" + escape(input) + "] cellSep=[" + escape(cellSeparator) + "] lineSep=["
                    + escape(lineSeparator) + "] quote=[" + (int) quoteChar + "] syntax=" + quoteSyntax
                    + " maxLineLength=" + maxLineLength + " readAhead=" + readAhead + " reset=" + useReset;

            List<String> expected = readAll(new ReferenceCsvLineReaderStates(lineSeparator, new StringReader(input), readAhead,
                    maxLineLength, quoteSyntax), cellSeparator, quoteChar, useReset, new Random(seed));
            List<String> actual = readAll(new CsvLineReaderStates(lineSeparator, new StringReader(input), readAhead,
                    maxLineLength, quoteSyntax), cellSeparator, quoteChar, useReset, new Random(seed));
            assertEquals(expected, actual, description);
            compared++;
        }
        assertEquals(6000, compared);
    }

    /**
     * Reads all lines and records everything observable after each call, including exceptions.
     */
    private static List<String> readAll(CsvLineReader reader, String cellSeparator, char quoteChar, boolean useReset,
                                        Random random) {
        List<String> events = new ArrayList<>();
        try {
            for (int line = 0; line < 200 && !reader.eofReached(); line++) {
                if (useReset && line > 0 && random.nextInt(3) == 0) {
                    reader.reset();
                    // Re-read the previous line, sometimes with other settings.
                    String sep = random.nextBoolean() ? cellSeparator : ",";
                    char quote = random.nextBoolean() ? quoteChar : '\'';
                    events.add("reset " + reader.readLine(sep, quote));
                    continue;
                }
                List<String> cells = reader.readLine(cellSeparator, quoteChar);
                events.add(cells + " line=" + reader.currentLineNumber() + " eof=" + reader.eofReached()
                        + " empty=" + reader.lastLineWasEmpty());
            }
        } catch (Exception e) {
            events.add("exception " + e.getClass().getName() + ": " + e.getMessage());
        }
        return events;
    }

    private static String randomInput(Random random, String cellSeparator, String lineSeparator, char quoteChar) {
        StringBuilder sb = new StringBuilder();
        int lines = random.nextInt(6);
        for (int l = 0; l < lines; l++) {
            int cells = random.nextInt(5);
            for (int c = 0; c < cells; c++) {
                if (c > 0)
                    sb.append(cellSeparator);
                sb.append(randomCell(random, cellSeparator, lineSeparator, quoteChar));
            }
            if (l < lines - 1 || random.nextBoolean())
                sb.append(lineSeparator);
        }
        return sb.toString();
    }

    private static String randomCell(Random random, String cellSeparator, String lineSeparator, char quoteChar) {
        StringBuilder cell = new StringBuilder();
        int length = random.nextInt(8);
        for (int i = 0; i < length; i++) {
            int kind = random.nextInt(20);
            if (kind == 0)
                cell.append(quoteChar);
            else if (kind == 1)
                cell.append(cellSeparator.charAt(random.nextInt(cellSeparator.length())));
            else if (kind == 2)
                cell.append(lineSeparator.charAt(random.nextInt(lineSeparator.length())));
            else if (kind == 3)
                cell.append('\r');
            else
                cell.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        int quoting = random.nextInt(6);
        if (quoting == 0)
            return quoteChar + cell.toString() + quoteChar;
        if (quoting == 1) // RFC style escaped quotes
            return quoteChar + cell.toString().replace(String.valueOf(quoteChar), "" + quoteChar + quoteChar) + quoteChar;
        if (quoting == 2) // Unterminated quote
            return quoteChar + cell.toString();
        return cell.toString();
    }

    private static String pick(Random random, String[] values) {
        return values[random.nextInt(values.length)];
    }

    private static String escape(String s) {
        return s.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
    }
}
