package org.jsapar.compose.csv;

import org.jsapar.compose.csv.quote.NeverQuote;
import org.jsapar.compose.csv.quote.NeverQuoteButReplace;
import org.jsapar.compose.csv.quote.QuoteIfNeeded;
import org.jsapar.model.*;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.QuoteSyntax;
import org.jsapar.schema.SchemaException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class CsvCellComposerTest {

    @Test
    public final void testOutput_String() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("First name").build();

        Writer writer = new StringWriter();
        Cell cell = new StringCell("First name", "Jonas");

        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuote(33));
        composer.compose(writer, cell);

        assertEquals("Jonas", writer.toString());
    }

    @Test
    public final void testOutput_String_ignorewrite() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("First name")
                .withIgnoreWrite(true)
                .build();

        Writer writer = new StringWriter();
        Cell cell = new StringCell("First name", "Jonas");
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuote(33));
        composer.compose(writer, cell);

        assertEquals("", writer.toString());
    }

    @Test
    public final void testOutput_String_quoted_contains_separator() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("First name").build();

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "Here; we come");
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new QuoteIfNeeded('\'', 33, ";", "\n",
                QuoteSyntax.FIRST_LAST));
        composer.compose(writer, cell);

        assertEquals("'Here; we come'", writer.toString());
    }

    @Test
    public final void testOutput_String_quoted_starts_with_quote() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("First name").build();

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "'Here we come");
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new QuoteIfNeeded('\'', 33, ";", "\n", QuoteSyntax.FIRST_LAST));
        composer.compose(writer, cell);

        assertEquals("''Here we come'", writer.toString());
    }

    @Test
    public final void testOutput_String_quote_not_used() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("First name").build();

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "Joho");
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new QuoteIfNeeded('\'', 33, ";", "\n", QuoteSyntax.FIRST_LAST));
        composer.compose(writer, cell);

        assertEquals("Joho", writer.toString());
    }

    @Test
    public final void testOutput_Int() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("Shoe size").build();

        Writer writer = new StringWriter();
        Cell cell = new IntegerCell("Shoe size", 123);
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuote(33));
        composer.compose(writer, cell);

        assertEquals("123", writer.toString());
    }

    @Test
    public final void testOutput_BigDecimal() throws IOException, SchemaException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("Money")
                .withType(CellType.DECIMAL)
                .withPattern("#,###.##")
                .withLocale(new Locale("sv", "SE"))
                .build();

        Writer writer = new StringWriter();
        Cell cell = new BigDecimalCell("test", new BigDecimal("123456.59"));
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuote(33));
        composer.compose(writer, cell);

        // Non breakable space as grouping character.
        assertEquals("123\u00A0456,59", writer.toString());
    }

    @Test
    public final void testOutput_int_us() throws IOException, SchemaException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("Integer")
                .withType(CellType.INTEGER)
                .build();

        Writer writer = new StringWriter();
        Cell cell = new IntegerCell("Integer", 123456);
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuote(33));
        composer.compose(writer, cell);

        // Non breakable space as grouping character.
        assertEquals("123456", writer.toString());
    }

    @Test
    public final void testOutput_Boolean() throws IOException, SchemaException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("Loves")
                .withType(CellType.BOOLEAN)
                .build();

        Writer writer = new StringWriter();
        Cell cell = new BooleanCell("Loves", true);
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuote(33));
        composer.compose(writer, cell);

        assertEquals("true", writer.toString());
    }

    @Test
    public final void testOutput_Replace() throws IOException, SchemaException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("Greeting").build();

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "With;-)love");
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuoteButReplace(33, ";-)", "\n", "*"));
        composer.compose(writer, cell);

        assertEquals("With*love", writer.toString());
    }

    @Test
    public final void testOutput_maxLength() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("First name").withMaxLength(4).build();
        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "Jonas");
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new NeverQuote(4));
        composer.compose(writer, cell);

        assertEquals("Jona", writer.toString());
    }

    @Test
    public final void testOutput_maxLength_quoted() throws IOException {
        CsvSchemaCell schemaElement = CsvSchemaCell.builder("First name").withMaxLength(4).build();
        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "J;onas");
        CsvCellComposer composer = new CsvCellComposer(schemaElement, new QuoteIfNeeded('"', 4, ";", "\n", QuoteSyntax.FIRST_LAST));
        composer.compose(writer, cell);

        assertEquals("\"J;\"", writer.toString());
    }

    @Test
    public final void testOutput_maxLength_replace() throws IOException {
        CsvSchemaCell schemaCell = CsvSchemaCell.builder("First name")
                .withMaxLength(4)
                .build();
        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "J;-)onas");
        CsvCellComposer composer = new CsvCellComposer(schemaCell, new NeverQuoteButReplace(4, ";-)", "\n", "*"));
        composer.compose(writer, cell);

        // Replaces ; with non breaking space.
        assertEquals("J*on", writer.toString());
    }

}
