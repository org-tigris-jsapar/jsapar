package org.jsapar.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Locale;

import org.jsapar.BigDecimalCell;
import org.jsapar.BooleanCell;
import org.jsapar.Cell;
import org.jsapar.CellType;
import org.jsapar.IntegerCell;
import org.jsapar.StringCell;
import org.junit.Test;


public class CsvSchemaCellTest {

    @Test
    public final void testOutput_String() throws IOException {
	CsvSchemaCell schemaElment = new CsvSchemaCell("First name");

	Writer writer = new StringWriter();
	Cell cell = new StringCell("Jonas");
	schemaElment.output(cell, writer, ";",(char)0);

	assertEquals("Jonas", writer.toString());
    }

    @Test
    public final void testOutput_String_ignorewrite() throws IOException {
        CsvSchemaCell schemaElment = new CsvSchemaCell("First name");
        schemaElment.setIgnoreWrite(true);

        Writer writer = new StringWriter();
        Cell cell = new StringCell("Jonas");
        schemaElment.output(cell, writer, ";",(char)0);

        assertEquals("", writer.toString());
    }
    
    @Test
    public final void testOutput_String_quoted() throws IOException {
	CsvSchemaCell schemaElment = new CsvSchemaCell("First name");

	Writer writer = new StringWriter();
	Cell cell = new StringCell("Here; we come");
	schemaElment.output(cell, writer, ";",'\'');

	assertEquals("'Here; we come'", writer.toString());
    }

    @Test
    public final void testOutput_String_quote_not_used() throws IOException {
	CsvSchemaCell schemaElment = new CsvSchemaCell("First name");

	Writer writer = new StringWriter();
	Cell cell = new StringCell("Joho");
	schemaElment.output(cell, writer, ";",'\'');

	assertEquals("Joho", writer.toString());
    }

    @Test
    public final void testOutput_Int() throws IOException {
	CsvSchemaCell schemaElment = new CsvSchemaCell("Shoe size");

	Writer writer = new StringWriter();
	Cell cell = new IntegerCell(new Integer(123));
	schemaElment.output(cell, writer, ";", (char)0);

	assertEquals("123", writer.toString());
    }

    
    @Test
    public final void testOutput_BigDecimal() throws IOException, SchemaException {
	CsvSchemaCell schemaElment = new CsvSchemaCell("Money");
	schemaElment.setCellFormat(new SchemaCellFormat(CellType.DECIMAL, "#,###.##",
		new Locale("sv", "SE")));

	Writer writer = new StringWriter();
	Cell cell = new BigDecimalCell(new BigDecimal("123456.59"));
	schemaElment.output(cell, writer, ";", (char)0);

	// Non breakable space as grouping character.
	assertEquals("123\u00A0456,59", writer.toString());
    }

    @Test
    public final void testOutput_Boolean() throws IOException, SchemaException {
	CsvSchemaCell schemaElment = new CsvSchemaCell("Loves");
	schemaElment.setCellFormat(new SchemaCellFormat(CellType.BOOLEAN));

	Writer writer = new StringWriter();
	Cell cell = new BooleanCell(new Boolean(true));
	schemaElment.output(cell, writer, ";", (char)0);

	assertEquals("true", writer.toString());
    }

    @Test
    public final void testOutput_Replace() throws IOException, SchemaException {
    	CsvSchemaCell schemaElment = new CsvSchemaCell("Greeting");

    	Writer writer = new StringWriter();
    	Cell cell = new StringCell("With;-)love");
    	schemaElment.output(cell, writer, ";-)", (char)0);

    	assertEquals("With\u00A0love", writer.toString());
    }
    
}
