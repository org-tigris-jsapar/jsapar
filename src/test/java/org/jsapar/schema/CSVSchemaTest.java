package org.jsapar.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.Cell.CellType;
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

	assertEquals(1, doc.getNumberOfLines());

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell(0).getStringValue());
	assertEquals("Stenberg", line.getCell(1).getStringValue());
	assertEquals("Hemvägen 19", line.getCell(2).getStringValue());
	assertEquals("111 22", line.getCell(3).getStringValue());
	assertEquals("Stockholm", line.getCell(4).getStringValue());
    }

    @Test
    public final void testParse_endingNewLine() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schema.addSchemaLine(schemaLine);
	String sToParse = "Jonas;Stenberg;Hemvägen 19;111 22;Stockholm" + System.getProperty("line.separator");
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);

	assertEquals(1, doc.getNumberOfLines());

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell(0).getStringValue());
	assertEquals("Stenberg", line.getCell(1).getStringValue());
	assertEquals("Hemvägen 19", line.getCell(2).getStringValue());
	assertEquals("111 22", line.getCell(3).getStringValue());
	assertEquals("Stockholm", line.getCell(4).getStringValue());
    }

    @Test
    public final void testParse_twoLines() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schema.addSchemaLine(schemaLine);
	String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + "Nils;Nilsson";
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
    public final void testParse_emptyLine_ignore() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schema.addSchemaLine(schemaLine);
	String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + System.getProperty("line.separator") + "Nils;Nilsson";
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);

	assertEquals(2, doc.getNumberOfLines());

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell(0).getStringValue());
	assertEquals("Stenberg", line.getCell(1).getStringValue());

	line = doc.getLine(1);
	assertEquals("Nils", line.getCell(0).getStringValue());
	assertEquals("Nilsson", line.getCell(1).getStringValue());
    }

    @Test
    public final void testParse_emptyLine_ignore_space() throws IOException, JSaParException {
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        schema.addSchemaLine(schemaLine);
        String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + " \t \t  " + System.getProperty("line.separator") + "Nils;Nilsson";
        java.io.Reader reader = new java.io.StringReader(sToParse);
        DocumentBuilder builder = new DocumentBuilder();
        Document doc = builder.build(reader, schema);

        assertEquals(2, doc.getNumberOfLines());

        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell(0).getStringValue());
        assertEquals("Stenberg", line.getCell(1).getStringValue());

        line = doc.getLine(1);
        assertEquals("Nils", line.getCell(0).getStringValue());
        assertEquals("Nilsson", line.getCell(1).getStringValue());
    }
    
    @Test
    public final void testParse_emptyLine_include() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schemaLine.setIgnoreReadEmptyLines(false);
	schema.addSchemaLine(schemaLine);
	String sToParse = "Jonas;Stenberg" + System.getProperty("line.separator") + System.getProperty("line.separator") + "Nils;Nilsson";
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);

	assertEquals(3, doc.getNumberOfLines());

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell(0).getStringValue());
	assertEquals("Stenberg", line.getCell(1).getStringValue());

	line = doc.getLine(2);
	assertEquals("Nils", line.getCell(0).getStringValue());
	assertEquals("Nilsson", line.getCell(1).getStringValue());
    }

    @Test
    public final void testParse_firstLineAsHeader() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	CsvSchemaCell shoeSizeCell = new CsvSchemaCell("Shoe Size", new SchemaCellFormat(CellType.INTEGER));
	shoeSizeCell.setDefaultValue("43");
	schemaLine.addSchemaCell(shoeSizeCell);
	schemaLine.setFirstLineAsSchema(true);
	schema.addSchemaLine(schemaLine);

	String sLineSep = System.getProperty("line.separator");
	String sToParse = "First Name;Last Name;Shoe Size" + sLineSep + "Jonas;Stenberg;41" + sLineSep + "Nils;Nilsson;";
	java.io.Reader reader = new java.io.StringReader(sToParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.build(reader, schema);

	Line line = doc.getLine(0);
	assertEquals("Jonas", line.getCell("First Name").getStringValue());
	assertEquals("Stenberg", line.getCell("Last Name").getStringValue());
	assertEquals(41, line.getIntCellValue("Shoe Size"));

	line = doc.getLine(1);
	assertEquals("Nils", line.getCell("First Name").getStringValue());
	assertEquals("Nilsson", line.getCell("Last Name").getStringValue());
        assertEquals(43, line.getIntCellValue("Shoe Size"));
    }

    @Test
    public final void testParse_firstLineAsHeader_quoted() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine();
	schemaLine.setFirstLineAsSchema(true);
	schemaLine.setQuoteChar('$');
	schema.addSchemaLine(schemaLine);

	String sLineSep = System.getProperty("line.separator");
	String sToParse = "$First Name$;$Last Name$" + sLineSep + "Jonas;$Stenberg$" + sLineSep + "Nils;Nilsson";
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
    public final void testOutput_firstLineAsHeader() throws IOException, JSaParException {
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
	schema.output(doc.getLineIterator(), writer);

	String sLineSep = System.getProperty("line.separator");
	String sExpected = "First Name;Last Name" + sLineSep + "Jonas;Stenberg" + sLineSep + "Nils;Nilsson";

	assertEquals(sExpected, writer.toString());
    }

    @Test
    public final void testOutputLine() throws IOException, JSaParException {
	org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
	CsvSchemaLine outputSchemaLine = new CsvSchemaLine(1);
	outputSchemaLine.addSchemaCell(new CsvSchemaCell("Header"));
	outputSchema.addSchemaLine(outputSchemaLine);

	outputSchemaLine = new CsvSchemaLine();
	outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
	outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
	outputSchemaLine.setCellSeparator(";");
	outputSchema.addSchemaLine(outputSchemaLine);

	Line line1 = new Line();
	line1.addCell(new StringCell("Jonas"));
	line1.addCell(new StringCell("Stenberg"));

	StringWriter writer = new StringWriter();
	outputSchema.outputLine(line1, 2, writer);

	String sLineSep = System.getProperty("line.separator");
	String sExpected = sLineSep + "Jonas;Stenberg";

	assertEquals(sExpected, writer.toString());
    }

    @Test
    public final void testOutputLine_first() throws IOException, JSaParException {
	org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
	CsvSchemaLine outputSchemaLine = new CsvSchemaLine(1);
	outputSchemaLine.addSchemaCell(new CsvSchemaCell("Header"));
	outputSchema.addSchemaLine(outputSchemaLine);

	outputSchemaLine = new CsvSchemaLine();
	outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
	outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
	outputSchemaLine.setCellSeparator(";");
	outputSchema.addSchemaLine(outputSchemaLine);

	Line line1 = new Line();
	line1.addCell(new StringCell("Header", "TheHeader"));
	line1.addCell(new StringCell("Something", "This should not be written"));

	StringWriter writer = new StringWriter();
	outputSchema.outputLine(line1, 1, writer);

	String sExpected = "TheHeader";

	assertEquals(sExpected, writer.toString());
    }

    @Test
    public final void testOutputLine_firstLineAsHeader() throws IOException, JSaParException {
	CsvSchema schema = new CsvSchema();
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.addSchemaCell(new CsvSchemaCell("HeaderHeader"));
	schema.addSchemaLine(schemaLine);

	schemaLine = new CsvSchemaLine();
	schemaLine.addSchemaCell(new CsvSchemaCell("First name"));
	schemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
	CsvSchemaCell shoeSizeCell = new CsvSchemaCell("Shoe size", new SchemaCellFormat(CellType.INTEGER));
	shoeSizeCell.setDefaultValue("41");
	schemaLine.addSchemaCell(shoeSizeCell);
	schemaLine.setFirstLineAsSchema(true);
	schema.addSchemaLine(schemaLine);

	Line line1 = new Line();
	line1.addCell(new StringCell("First name", "Jonas"));
	line1.addCell(new StringCell("Last name", "Stenberg"));

	StringWriter writer = new StringWriter();
	schema.outputLine(line1, 2, writer);

	String sLineSep = System.getProperty("line.separator");
	String sExpected = sLineSep + "First name;Last name;Shoe size" + sLineSep + "Jonas;Stenberg;41";

	assertEquals(sExpected, writer.toString());
    }

    private class DocumentBuilder {
	private Document document = new Document();
	private ParsingEventListener listener;

	public DocumentBuilder() {
	    listener = new ParsingEventListener() {

		@Override
		public void lineErrorEvent(LineErrorEvent event) throws ParseException {
		    throw new ParseException(event.getCellParseError());
		}

		@Override
		public void lineParsedEvent(LineParsedEvent event) {
		    document.addLine(event.getLine());
		}
	    };
	}

	public Document build(java.io.Reader reader, ParseSchema parser) throws JSaParException, IOException {

	    parser.parse(reader, listener);
	    return this.document;
	}
    }

}
