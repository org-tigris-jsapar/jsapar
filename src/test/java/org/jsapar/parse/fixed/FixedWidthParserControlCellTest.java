package org.jsapar.parse.fixed;

import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Document;
import org.jsapar.parse.DocumentBuilderLineConsumer;
import org.jsapar.parse.LineParseException;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.MatchingCellValueCondition;
import org.jsapar.text.TextParseConfig;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class FixedWidthParserControlCellTest {

    @Test
    public void testParse() throws IOException {
        String toParse = "NJonasStenbergAStorgatan 123 45NFred Bergsten";
        FixedWidthSchema schema = schemaBuilderOneCharControl().withLineSeparator("").build();

        Reader reader = new StringReader(toParse);
        TextParseConfig config = new TextParseConfig();
        config.setMaxLineLength(20);
        FixedWidthParser parser = new FixedWidthParser(reader, schema, config);
        DocumentBuilderLineConsumer builder = new DocumentBuilderLineConsumer();
        parser.parse(builder, new ExceptionErrorConsumer());
        Document doc = builder.getDocument();

        checkResult(doc);
    }

    private FixedWidthSchema.Builder schemaBuilderOneCharControl() {
        return FixedWidthSchema.builder()
                .withLine("Name", l->l
                        .withCell("Type", 1, c->c.withLineCondition(new MatchingCellValueCondition("N")))
                        .withCell("First name", 5)
                        .withCell("Last name", 8))
                .withLine("Address", l->l
                        .withCell("Type", 1, c->c.withLineCondition(new MatchingCellValueCondition("A")))
                        .withCell("Street", 10)
                        .withCell("Zip code", 6));
    }

    private FixedWidthSchema.Builder schemaBuilderTwoCharsControl() {
        return FixedWidthSchema.builder()
                .withLine("Name", l->l
                        .withCell("Type", 2, c->c.withLineCondition(new MatchingCellValueCondition("N")))
                        .withCell("First name", 5)
                        .withCell("Last name", 8))
                .withLine("Address", l->l
                        .withCell("Type", 2, c->c.withLineCondition(new MatchingCellValueCondition("AA")))
                        .withCell("Street", 10)
                        .withCell("Zip code", 6));
    }

    @Test
    public void testParse_separatedLines() throws IOException {
        String toParse = "NJonasStenberg   \r\nAStorgatan 123 45          \r\nNFred Bergsten\r\n";
        FixedWidthSchema schema = schemaBuilderOneCharControl().withLineSeparator("\r\n").build();

        Reader reader = new StringReader(toParse);
        Document doc = build(reader, schema);

        checkResult(doc);
    }

    @Test
    public void testParse_separatedLines_custom() throws IOException {
        String toParse = "NJonasStenberg   $^AStorgatan 123 45          $^NFred Bergsten$^";
        FixedWidthSchema schema = schemaBuilderOneCharControl().withLineSeparator("$^").build();

        Reader reader = new StringReader(toParse);
        Document doc = build(reader, schema);

        checkResult(doc);
    }
    private void checkResult(Document doc) {
        assertEquals("Jonas", doc.getLine(0).getCell("First name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());

        assertEquals("Storgatan", doc.getLine(1).getCell("Street").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals("123 45", doc.getLine(1).getCell("Zip code").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());

        assertEquals("Fred", doc.getLine(2).getCell("First name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals("Bergsten", doc.getLine(2).getCell("Last name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
    }

    @Test
    public void testParse_spaceInLineType() throws IOException {
        String toParse = "N JonasStenberg   \r\nAAStorgatan 123 45          \r\nN Fred Bergsten";
        FixedWidthSchema schema = schemaBuilderTwoCharsControl().withLineSeparator("\r\n").build();

        Reader reader = new StringReader(toParse);
        Document doc = build(reader, schema);

        checkResult(doc);
    }

    @Test(expected = LineParseException.class)
    public void testParse_errorOnUndefinedLineType() throws IOException {
        String toParse = "X JonasStenberg   ";
        FixedWidthSchema schema = schemaBuilderTwoCharsControl().withLineSeparator("\r\n").build();

        Reader reader = new StringReader(toParse);
        TextParseConfig config = new TextParseConfig();
        config.setOnUndefinedLineType(ValidationAction.EXCEPTION);
        build(reader, schema, config);
    }

    @Test
    public void testParse_noErrorIfUndefinedLineType() throws IOException {
        String toParse = "N JonasStenberg   \r\nXXStorgatan 123 45          \r\n\r\nN Fred Bergsten";
        FixedWidthSchema schema = schemaBuilderTwoCharsControl().withLineSeparator("\r\n").build();

        Reader reader = new StringReader(toParse);
        TextParseConfig config = new TextParseConfig();
        config.setOnUndefinedLineType(ValidationAction.OMIT_LINE);
        Document doc = build(reader, schema, config);

        assertEquals(2, doc.size());
        assertEquals("Jonas", doc.getLine(0).getCell("First name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals("Stenberg", doc.getLine(0).getCell("Last name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());

        assertEquals("Fred", doc.getLine(1).getCell("First name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals("Bergsten", doc.getLine(1).getCell("Last name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
    }

    private Document build(Reader reader, FixedWidthSchema schema) throws IOException {
        TextParseConfig config = new TextParseConfig();
        config.setMaxLineLength(32);
        return build(reader, schema, config);
    }

    private Document build(Reader reader, FixedWidthSchema schema, TextParseConfig config) throws IOException {
        FixedWidthParser parser = new FixedWidthParser(reader, schema, config);
        DocumentBuilderLineConsumer builder = new DocumentBuilderLineConsumer();
        parser.parse(builder, new ExceptionErrorConsumer());
        return builder.getDocument();
    }


}
