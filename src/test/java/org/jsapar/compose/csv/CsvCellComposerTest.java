package org.jsapar.compose.csv;

import org.jsapar.model.*;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.SchemaException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class CsvCellComposerTest {

    @Test
    public final void testOutput_String() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");

        Writer writer = new StringWriter();
        Cell cell = new StringCell("First name", "Jonas");

        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", (char) 0);

        assertEquals("Jonas", writer.toString());
    }

    @Test
    public final void testOutput_String_ignorewrite() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");
        schemaElement.setIgnoreWrite(true);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("First name", "Jonas");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", (char) 0);

        assertEquals("", writer.toString());
    }

    @Test
    public final void testOutput_String_quoted_contains_separator() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "Here; we come");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", '\'');

        assertEquals("'Here; we come'", writer.toString());
    }

    @Test
    public final void testOutput_String_quoted_starts_with_quote() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "'Here we come'");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", '\'');

        assertEquals("''Here we come''", writer.toString());
    }

    @Test
    public final void testOutput_String_quote_not_used() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "Joho");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", '\'');

        assertEquals("Joho", writer.toString());
    }

    @Test
    public final void testOutput_Int() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("Shoe size");

        Writer writer = new StringWriter();
        Cell cell = new IntegerCell("Shoe size", 123);
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", '\'');

        assertEquals("123", writer.toString());
    }

    @Test
    public final void testOutput_BigDecimal() throws IOException, SchemaException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("Money", CellType.DECIMAL, "#,###.##", new Locale("sv", "SE"));

        Writer writer = new StringWriter();
        Cell cell = new BigDecimalCell("test", new BigDecimal("123456.59"));
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", '\'');

        // Non breakable space as grouping character.
        assertEquals("123\u00A0456,59", writer.toString());
    }

    @Test
    public final void testOutput_Boolean() throws IOException, SchemaException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("Loves");
        schemaElement.setCellFormat(CellType.BOOLEAN);

        Writer writer = new StringWriter();
        Cell cell = new BooleanCell("Loves", true);
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", '\'');

        assertEquals("true", writer.toString());
    }

    @Test
    public final void testOutput_Replace() throws IOException, SchemaException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("Greeting");

        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "With;-)love");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement,";-)", (char) 0);

        assertEquals("With\u00A0love", writer.toString());
    }

    @Test
    public final void testOutput_maxLength() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");
        schemaElement.setMaxLength(4);
        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "Jonas");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement, ";", (char)0);

        assertEquals("Jona", writer.toString());
    }

    @Test
    public final void testOutput_maxLength_quoted() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");
        schemaElement.setMaxLength(4);
        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "J;onas");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement,";", '"');

        assertEquals("\"J;\"", writer.toString());
    }

    @Test
    public final void testOutput_maxLength_replace() throws IOException {
        CsvSchemaCell schemaElement = new CsvSchemaCell("First name");
        schemaElement.setMaxLength(4);
        Writer writer = new StringWriter();
        Cell cell = new StringCell("test", "J;onas");
        CsvCellComposer composer = new CsvCellComposer(writer);
        composer.compose(cell, schemaElement,";", (char) 0);

        // Replaces ; with non breaking space.
        assertEquals("J\u00A0on", writer.toString());
    }

}
