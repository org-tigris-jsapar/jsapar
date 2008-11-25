package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.Cell.CellType;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.junit.Test;

public class FixedWidthSchemaLineTest {

    private class NullParsingEventListener implements ParsingEventListener {

	@Override
	public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
	    throw new ParseException(event.getCellParseError());
	}

	@Override
	public void lineParsedEvent(LineParsedEvent event) {
	}
    };

    @Test
    public void testBuild() throws IOException, JSaParException {
	String toParse = "JonasStenbergSpiselvägen 19141 59Huddinge";
	org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("City", 8));
	schema.addSchemaLine(schemaLine);

	Reader reader = new StringReader(toParse);
	boolean rc = schemaLine.parse(1, reader, new ParsingEventListener() {

	    @Override
	    public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
		throw new ParseException(event.getCellParseError());
	    }

	    @Override
	    public void lineParsedEvent(LineParsedEvent event) {
		Line line = event.getLine();
		assertEquals("Jonas", line.getCell(0).getStringValue());
		assertEquals("Stenberg", line.getCell("Last name").getStringValue());
		assertEquals("Spiselvägen 19", line.getCell(2).getStringValue());
		assertEquals("141 59", line.getCell("Zip code").getStringValue());
		assertEquals("Huddinge", line.getCell(4).getStringValue());

		assertEquals("Last name", line.getCell(1).getName());
		assertEquals("Zip code", line.getCell(3).getName());
	    }
	});

	assertEquals(true, rc);
    }

    @Test(expected = org.jsapar.input.ParseException.class)
    public void testBuild_parseError() throws IOException, JSaParException {
	String toParse = "JonasStenbergFortione";
	org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));

	FixedWidthSchemaCell shoeSizeSchema = new FixedWidthSchemaCell("Shoe size", 8);
	shoeSizeSchema.setCellFormat(new SchemaCellFormat(CellType.INTEGER));
	schemaLine.addSchemaCell(shoeSizeSchema);

	schema.addSchemaLine(schemaLine);

	Reader reader = new StringReader(toParse);
	@SuppressWarnings("unused")
	boolean rc = schemaLine.parse(1, reader, new NullParsingEventListener());
    }

    @Test
    public void testOutput() throws IOException, JSaParException {
	Line line = new Line();
	line.addCell(new StringCell("Jonas"));
	line.addCell(new StringCell("Stenberg"));
	line.addCell(new StringCell("Street address", "Spiselvägen 19"));
	// line.addCell(new StringCell("141 59"));
	line.addCell(new StringCell("City", "Huddinge"));

	org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(1);
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street address", 14));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("City", 8));
	schema.addSchemaLine(schemaLine);

	java.io.Writer writer = new java.io.StringWriter();
	schemaLine.output(line, writer);
	String sResult = writer.toString();

	assertEquals("JonasStenbergSpiselvägen 19      Huddinge", sResult);
    }

    @Test
    public final void testClone() throws CloneNotSupportedException {
	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine();
	schemaLine.setLineType("Nisse");
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));

	FixedWidthSchemaLine clone = schemaLine.clone();

	assertEquals(schemaLine.getLineType(), clone.getLineType());

	// Does not clone strings values yet. Might do that in the future.
	assertTrue(schemaLine.getLineType() == clone.getLineType());
	assertEquals(schemaLine.getSchemaCells().get(0).getName(), clone.getSchemaCells().get(0).getName());
	assertFalse(schemaLine.getSchemaCells().get(0) == clone.getSchemaCells().get(0));
    }

}
