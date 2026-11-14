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
}
