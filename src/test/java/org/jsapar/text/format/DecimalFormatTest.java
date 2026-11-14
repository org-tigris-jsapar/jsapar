package org.jsapar.text.format;

import org.jsapar.TextComposer;
import org.jsapar.TextParser;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecimalFormatTest {

    @Test
    void format_defaultPattern_keepsAllDecimals() {
        DecimalFormat format = new DecimalFormat(Locale.US);
        assertEquals("1234.56", format.format(new BigDecimal("1234.56")));
        assertEquals("0.000001", format.format(new BigDecimal("0.000001")));
        assertEquals("-3.14159265358979323846", format.format(new BigDecimal("-3.14159265358979323846")));
        assertEquals("42", format.format(new BigDecimal("42")));
    }

    @Test
    void format_defaultPattern_localeSymbols() {
        DecimalFormat format = new DecimalFormat(Locale.forLanguageTag("sv-SE"));
        assertEquals("1234,56", format.format(new BigDecimal("1234.56")));
    }

    @Test
    void format_explicitPattern_isRespected() {
        DecimalFormat format = new DecimalFormat("0.0", Locale.US);
        assertEquals("1234.6", format.format(new BigDecimal("1234.56")));
    }

    @Test
    void parse_defaultPattern() throws ParseException {
        DecimalFormat format = new DecimalFormat(Locale.US);
        assertEquals(new BigDecimal("1234.56"), format.parse("1234.56"));
    }

    @Test
    void csvRoundTrip_decimalCell_keepsPrecision() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLine("l", l -> l.withCell("amount", c -> c.withType(CellType.DECIMAL)))
                .build();
        String input = "1234.56\n0.125\n";
        List<Line> lines = new ArrayList<>();
        new TextParser(schema).parseForEach(new StringReader(input), lines::add);

        StringWriter writer = new StringWriter();
        TextComposer composer = new TextComposer(schema, writer);
        lines.forEach(composer::composeLine);

        assertEquals("1234.56" + System.lineSeparator() + "0.125", writer.toString().strip());
    }

    /**
     * The default US format uses BigDecimal directly for plain values. Verify that the result is identical to the
     * java.text.DecimalFormat for all kinds of input.
     */
    @Test
    void defaultUsFormat_equivalentToJavaTextFormat() throws ParseException {
        java.text.DecimalFormat textFormat = new java.text.DecimalFormat("0.#", java.text.DecimalFormatSymbols.getInstance(Locale.US));
        textFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
        textFormat.setParseBigDecimal(true);
        NumberFormat reference = new NumberFormat(textFormat, CellType.FLOAT);
        DecimalFormat format = new DecimalFormat(Locale.US);

        List<String> values = new ArrayList<>(List.of("1234.56", "1234.50", "1.0", "100", "0", "-0", "-0.0", "0.00",
                ".5", "5.", "-.5", "00012.30", "-12.340", "123456789012345678901234567890.123456789", "1E3", "1e3",
                "1E-3", "+5", "1.5E+2", "1,234.5", " 1", "1 ", "--1", "-", ".", "-.", "1.2.3", "0x1", "1_0", "1d", ""));
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < 1000; i++)
            values.add(new BigDecimal(random.nextLong()).movePointLeft(random.nextInt(20)).toPlainString());

        for (String value : values) {
            BigDecimal expected;
            try {
                expected = (BigDecimal) reference.parse(value);
            } catch (ParseException e) {
                org.junit.jupiter.api.Assertions.assertThrows(ParseException.class, () -> format.parse(value), value);
                continue;
            }
            BigDecimal actual = format.parse(value);
            assertEquals(expected, actual, value); // BigDecimal.equals also compares scale.
            if (expected != null)
                assertEquals(reference.format(expected), format.format(actual), value);
        }
    }

    @Test
    void format_nonBigDecimalValues() {
        DecimalFormat format = new DecimalFormat(Locale.US);
        assertEquals("42", format.format(42L));
        assertEquals("3.5", format.format(3.5d));
        assertEquals("1000", format.format(new BigDecimal("1E+3")));
    }
}
