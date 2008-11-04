package org.jsapar;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.jsapar.input.Parser;
import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.XmlDocumentParser;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaException;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.junit.Test;


/**
 * These tests are not unit tests!!<br>
 * The tests in this class uses the sample files provided in the folder
 * resources/samples. The tests below show how JSaPar can be used to parse
 * files.
 * 
 * @author stejon0
 * 
 */
public class JSaParExamplesTest {

    @Test
    public final void testExampleCsv01() throws SchemaException, IOException,
	    JSaParException {
	Reader schemaReader = new FileReader(
		"samples/01_CsvSchema.xml");
	Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
	Schema schema = xmlBuilder.build(schemaReader);
	Reader fileReader = new FileReader("samples/01_Names.csv");
	Parser parser = new Parser();
	Document document = parser.build(fileReader, schema);

	assertEquals("Erik", document.getLine(0).getCell(0).getStringValue());
	assertEquals("Svensson", document.getLine(0).getCell(1)
		.getStringValue());
	assertEquals("Fredrik", document.getLine(1).getCell(0).getStringValue());
	assertEquals("Larsson", document.getLine(1).getCell(1).getStringValue());

	assertEquals("Person", document.getLine(0).getLineType());
	assertEquals("Person", document.getLine(1).getLineType());
    }

    @Test
    public final void testExampleFixedWidth02() throws SchemaException,
	    IOException, JSaParException {
	Reader schemaReader = new FileReader(
		"samples/02_FixedWidthSchema.xml");
	Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
	Schema schema = builder.build(schemaReader);
	System.out.println("Schema: " + schema);
	Reader fileReader = new FileReader("samples/02_Names.txt");
	Parser docBuilder = new Parser();
	Document document = docBuilder.build(fileReader, schema);

	assertEquals("Erik    ", document.getLine(0).getCell(0)
		.getStringValue());
	assertEquals("Svensson ", document.getLine(0).getCell(1)
		.getStringValue());
	assertEquals("Fredrik ", document.getLine(1).getCell(0)
		.getStringValue());
	assertEquals("Larsson  ", document.getLine(1).getCell(1)
		.getStringValue());
    }

    @Test
    public final void testExampleFlatFile03() throws SchemaException,
	    IOException, JSaParException {
	/*
	 * This test fails. JAXP does not seem to recognise the name space
	 * prefix correctly. I don't know what I am doing wrong.
	 */
	Reader schemaReader = new FileReader(
		"samples/03_FlatFileSchema.xml");
	Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
	Schema schema = builder.build(schemaReader);
	Reader fileReader = new FileReader(
		"samples/03_FlatFileNames.txt");
	Parser docBuilder = new Parser();
	Document document = docBuilder.build(fileReader, schema);

	assertEquals("Erik", document.getLine(0).getCell(0).getStringValue());
	assertEquals("Svensson", document.getLine(0).getCell(1)
		.getStringValue());
	assertEquals("Fredrik", document.getLine(1).getCell(0).getStringValue());
	assertEquals("Larsson", document.getLine(1).getCell(1).getStringValue());
    }

    @Test
    public final void testExampleFixedWidth04() throws SchemaException,
	    IOException, JSaParException {
	Reader schemaReader = new FileReader(
		"samples/04_FixedWidthSchemaControlCell.xml");
	Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
	Schema schema = builder.build(schemaReader);
	Reader fileReader = new FileReader("samples/04_Names.txt");
	Parser docBuilder = new Parser();
	Document document = docBuilder.build(fileReader, schema);

	assertEquals("04_Names.txt", document.getLine(0).getCell("FileName")
		.getStringValue());
	assertEquals("2007-07-07", document.getLine(0).getCell("Created date")
		.getStringValue());
	assertEquals("Header", document.getLine(0).getLineType());
	assertEquals("Svensson", document.getLine(1).getCell(1)
		.getStringValue());
	assertEquals("Erik", document.getLine(1).getCell(0).getStringValue());
	assertEquals("Svensson", document.getLine(1).getCell(1)
		.getStringValue());
	assertEquals("Fredrik", document.getLine(2).getCell(0).getStringValue());
	assertEquals("Larsson", document.getLine(2).getCell(1).getStringValue());
	assertEquals("2", document.getLine(3).getCell(0).getStringValue());
    }

    @Test
    public final void testExampleXml05() throws SchemaException, IOException,
	    JSaParException {
	java.util.List<CellParseError> parseErrors = new java.util.LinkedList<CellParseError>();
	ParseSchema parser = new XmlDocumentParser();
	Reader fileReader = new FileReader("samples/05_Names.xml");
	Parser docBuilder = new Parser();
	Document document = docBuilder.build(fileReader, parser, parseErrors);

//	System.out.println("Errors: " + parseErrors.toString());

	assertEquals(2, document.getNumberOfLines());
	assertEquals("Hans", document.getLine(0).getCell("FirstName")
		.getStringValue());
	assertEquals("Hugge", document.getLine(0).getCell("LastName")
		.getStringValue());
	assertEquals(48,
		((IntegerCell) document.getLine(0).getCell("ShoeSize"))
			.getNumberValue().intValue());
	assertEquals("Greta", document.getLine(1).getCell("FirstName")
		.getStringValue());
	assertEquals("Skog", document.getLine(1).getCell("LastName")
		.getStringValue());
	assertEquals(31,
		((IntegerCell) document.getLine(1).getCell("ShoeSize"))
			.getNumberValue().intValue());
    }

    @Test
    public final void testExampleCsvControlCell06() throws SchemaException,
	    IOException, JSaParException {
	Reader schemaReader = new FileReader(
		"samples/06_CsvSchemaControlCell.xml");
	Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
	Schema schema = builder.build(schemaReader);
	Reader fileReader = new FileReader(
		"samples/06_NamesControlCell.csv");
	Parser docBuilder = new Parser();
	Document document = docBuilder.build(fileReader, schema);

	assertEquals("06_NamesControlCell.csv", document.getLine(0).getCell(
		"FileName").getStringValue());
	assertEquals("2007-07-07", document.getLine(0).getCell("Created date")
		.getStringValue());
	assertEquals("Header", document.getLine(0).getLineType());
	assertEquals("Person", document.getLine(1).getLineType());
	assertEquals("Svensson", document.getLine(1).getCell(1)
		.getStringValue());
	assertEquals("Erik", document.getLine(1).getCell(0).getStringValue());
	assertEquals("Svensson", document.getLine(1).getCell(1)
		.getStringValue());
	assertEquals("Fredrik", document.getLine(2).getCell(0).getStringValue());
	assertEquals("Larsson", document.getLine(2).getCell(1).getStringValue());
	assertEquals("2", document.getLine(3).getCell(0).getStringValue());
    }
}
