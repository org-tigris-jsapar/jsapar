package org.jsapar.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.input.Parser;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.ParsingEventListener;
import org.junit.Test;

public class CSVSchemaTest {

    @Test
    public final void testParse_oneLine() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schema.addSchemaLine(schemaLine);
	String sToParse = "Jonas;Stenberg;Hemvägen 19;111 22;Stockholm";
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);
	Line line = doc.getLine(0);

	assertEquals("Jonas", line.getCell(0).getStringValue());
	assertEquals("Stenberg", line.getCell(1).getStringValue());
	assertEquals("Hemvägen 19", line.getCell(2).getStringValue());
	assertEquals("111 22", line.getCell(3).getStringValue());
	assertEquals("Stockholm", line.getCell(4).getStringValue());
    }

    @Test
    public final void testBuild_twoLines() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schema.addSchemaLine(schemaLine);
	String sToParse = "Jonas;Stenberg"
		+ System.getProperty("line.separator") + "Nils;Nilsson";
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell(0).getStringValue());
	assertEquals("Stenberg", line.getCell(1).getStringValue());

	line = doc.getLine(1);
	assertEquals("Nils", line.getCell(0).getStringValue());
	assertEquals("Nilsson", line.getCell(1).getStringValue());
    }

    @Test
    public final void testBuild_firstLineAsHeader() throws IOException,
	    JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schemaLine.setFirstLineAsSchema(true);
	schema.addSchemaLine(schemaLine);

	String sLineSep = System.getProperty("line.separator");
	String sToParse = "First Name;Last Name" + sLineSep + "Jonas;Stenberg"
		+ sLineSep + "Nils;Nilsson";
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell("First Name").getStringValue());
	assertEquals("Stenberg", line.getCell("Last Name").getStringValue());

	line = doc.getLine(1);
	assertEquals("Nils", line.getCell("First Name").getStringValue());
	assertEquals("Nilsson", line.getCell("Last Name").getStringValue());
    }

    @Test
    public final void testBuild_firstLineAsHeader_quoted() throws IOException,
	    JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schemaLine.setFirstLineAsSchema(true);
	schemaLine.setQuoteChar('$');
	schema.addSchemaLine(schemaLine);

	String sLineSep = System.getProperty("line.separator");
	String sToParse = "$First Name$;$Last Name$" + sLineSep
		+ "Jonas;$Stenberg$" + sLineSep + "Nils;Nilsson";
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell("First Name").getStringValue());
	assertEquals("Stenberg", line.getCell("Last Name").getStringValue());

	line = doc.getLine(1);
	assertEquals("Nils", line.getCell("First Name").getStringValue());
	assertEquals("Nilsson", line.getCell("Last Name").getStringValue());
    }

    @Test
    public final void testOutput_firstLineAsHeader() throws ParseException,
	    IOException {
	CsvSchema schema = new CsvSchema();

	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
	schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
	schemaLine.setFirstLineAsSchema(true);
	schema.addSchemaLine(schemaLine);

	Document doc = new Document();

	Line line1 = new Line();
	line1.addCell(new StringCell("Jonas"));
	line1.addCell(new StringCell("Stenberg"));
	doc.addLine(line1);

	Line line2 = new Line();
	line2.addCell(new StringCell("Nils"));
	line2.addCell(new StringCell("Nilsson"));
	doc.addLine(line2);

	StringWriter writer = new StringWriter();
	schema.output(doc, writer);

	String sLineSep = System.getProperty("line.separator");
	String sExpected = "First Name;Last Name" + sLineSep + "Jonas;Stenberg"
		+ sLineSep + "Nils;Nilsson";

	assertEquals(sExpected, writer.toString());
    }

    private class DocumentBuilder {
	private Document document = new Document();
	private ParsingEventListener listener;

	public DocumentBuilder() {
	    listener = new ParsingEventListener() {

		@Override
		public void lineErrorErrorEvent(LineErrorEvent event)
			throws ParseException {
		    throw new ParseException(event.getCellParseError());
		}

		@Override
		public void lineParsedEvent(LineParsedEvent event) {
		    document.addLine(event.getLine());
		}
	    };
	}

	public Document build(java.io.Reader reader, ParseSchema parser)
		throws JSaParException, IOException {

	    parser.parse(reader, listener);
	    return this.document;
	}
    }

}
