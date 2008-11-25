package org.jsapar.schema;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.junit.Test;

public class CSVSchemaLineTest extends TestCase {

    private class NullParsingEventListener implements ParsingEventListener {
	public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
	}

	public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
	}
    }

    @Test
    public void testBuild() throws JSaParException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	String sLine = "Jonas;Stenberg;Hemvägen 19;111 22;Stockholm";
	boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

	    @Override
	    public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
	    }

	    @Override
	    public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
		Line line = event.getLine();
		assertEquals("Jonas", line.getCell(0).getStringValue());
		assertEquals("Stenberg", line.getCell(1).getStringValue());
	    }
	});

	assertEquals(true, rc);
    }

    @Test
    public void testBuild_quoted() throws JSaParException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;\"Hemvägen ;19\";111 22;Stockholm";
	boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

	    @Override
	    public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
	    }

	    @Override
	    public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
		Line line = event.getLine();
		assertEquals("Jonas", line.getCell(0).getStringValue());
		assertEquals("Stenberg", line.getCell(1).getStringValue());
		assertEquals("Hemvägen ;19", line.getCell(2).getStringValue());
	    }
	});

	assertEquals(true, rc);
    }

    @Test
    public void testBuild_quoted_missing_end() throws JSaParException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;\"Hemvägen ;19;111 22;Stockholm";
	try {
	    @SuppressWarnings("unused")
	    boolean rc = schemaLine.parse(1, sLine, new NullParsingEventListener());
	} catch (ParseException e) {
	    return;
	}
	fail("Should throw ParseException for missing end quote");
    }

    @Test
    public void testBuild_quoted_miss_placed_start() throws JSaParException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;H\"emvägen ;19\";111 22;Stockholm";
	try {
	    @SuppressWarnings("unused")
	    boolean rc = schemaLine.parse(1, sLine, new NullParsingEventListener());
	} catch (ParseException e) {
	    return;
	}
	fail("Should throw ParseException for miss-placed quote");
    }

    @Test
    public void testBuild_quoted_miss_placed_end() throws JSaParException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;\"Hemvägen ;1\"9;111 22;Stockholm";
	try {
	    @SuppressWarnings("unused")
	    boolean rc = schemaLine.parse(1, sLine, new NullParsingEventListener());
	} catch (ParseException e) {
	    return;
	}
	fail("Should throw ParseException for miss-placed quote");
    }

    @Test
    public void testBuild_withNames() throws JSaParException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setCellSeparator(";-)");
	schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
	schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

	String sLine = "Jonas;-)Stenberg";
	boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

	    @Override
	    public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
		Line line = event.getLine();
		assertEquals("Jonas", line.getCell(0).getStringValue());
		assertEquals("Stenberg", line.getCell(1).getStringValue());

		assertEquals("First Name", line.getCell(0).getName());
		assertEquals("Last Name", line.getCell(1).getName());
	    }
	});
	assertEquals(true, rc);

    }

    @Test
    public void testOutput() throws ParseException, IOException {

	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setCellSeparator(";-)");
	schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
	schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

	Line line = new Line();
	line.addCell(new StringCell("First Name", "Jonas"));
	line.addCell(new StringCell("Last Name", "Stenberg"));
	StringWriter writer = new StringWriter();

	schemaLine.output(line, writer);

	assertEquals("Jonas;-)Stenberg", writer.toString());

    }

    @Test
    public void testOutput_reorder() throws ParseException, IOException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
	schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
	schemaLine.setCellSeparator(";");

	Line line = new Line();
	line.addCell(new StringCell("Last Name", "Stenberg"));
	line.addCell(new StringCell("First Name", "Jonas"));
	StringWriter writer = new StringWriter();

	schemaLine.output(line, writer);

	assertEquals("Jonas;Stenberg", writer.toString());

    }

}
