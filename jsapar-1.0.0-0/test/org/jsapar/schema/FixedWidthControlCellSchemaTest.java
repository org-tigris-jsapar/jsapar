/**
 * 
 */
package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.ParsingEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author stejon0
 *
 */
public class FixedWidthControlCellSchemaTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.jsapar.schema.FixedWidthControlCellSchema#output(org.jsapar.Document, java.io.Writer)}.
     * @throws JSaParException 
     * @throws IOException 
     */
    @Test
    public void testOutput() throws IOException, JSaParException {
	FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema(1);
	schema.setLineSeparator("");
	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Address");
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("ZipCode", 6));
	schema.addSchemaLine(schemaLine);

	schemaLine = new FixedWidthSchemaLine("Name");
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
	schema.addSchemaLine(schemaLine);

	Document doc = new Document();

	Line line = new Line("Name");
	line.addCell(new StringCell("Jonas"));
	line.addCell(new StringCell("Stenberg"));
	doc.addLine(line);

	line = new Line("Address");
	line.addCell(new StringCell("Storgatan"));
	line.addCell(new StringCell("123 45"));
	doc.addLine(line);

	line = new Line("Name");
	line.addCell(new StringCell("Fred"));
	line.addCell(new StringCell("Bergsten"));
	doc.addLine(line);

	StringWriter writer = new StringWriter();
	schema.output(doc.getLineIterator(), writer);

	String sExpected = "NJonasStenbergAStorgatan 123 45NFred Bergsten";
	assertEquals(sExpected, writer.toString());
    }

    @Test
    public void testOutput_DontWriteControlCell() throws IOException, JSaParException {
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema(1);
        schema.setWriteControlCell(false);
        schema.setLineSeparator("");
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Address");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("ZipCode", 6));
        schema.addSchemaLine(schemaLine);

        schemaLine = new FixedWidthSchemaLine("Name");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schema.addSchemaLine(schemaLine);

        Document doc = new Document();

        Line line = new Line("Name");
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));
        doc.addLine(line);

        line = new Line("Address");
        line.addCell(new StringCell("Storgatan"));
        line.addCell(new StringCell("123 45"));
        doc.addLine(line);

        line = new Line("Name");
        line.addCell(new StringCell("Fred"));
        line.addCell(new StringCell("Bergsten"));
        doc.addLine(line);

        StringWriter writer = new StringWriter();
        schema.output(doc.getLineIterator(), writer);

        String sExpected = "JonasStenbergStorgatan 123 45Fred Bergsten";
        assertEquals(sExpected, writer.toString());
    }

    /**
     * Test method for {@link org.jsapar.schema.FixedWidthControlCellSchema#parse(java.io.Reader, org.jsapar.input.ParsingEventListener)}.
     * @throws IOException 
     * @throws JSaParException 
     */
    @Test
    public void testParse() throws JSaParException, IOException {
	String toParse = "NJonasStenbergAStorgatan 123 45NFred Bergsten";
	org.jsapar.schema.FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
	schema.setLineSeparator("");
	schema.setControlCellLength(1);

	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name", "N");
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
	schema.addSchemaLine(schemaLine);
	
	schemaLine = new FixedWidthSchemaLine("Address", "A");
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Street", 10));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("Zip code", 6));
	schema.addSchemaLine(schemaLine);	

	Reader reader = new StringReader(toParse);
	DocumentBuilder builder = new DocumentBuilder();
	Document doc = builder.parse(reader, schema);

	assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
	assertEquals("Stenberg", doc.getLine(0).getCell("Last name")
		.getStringValue());

	assertEquals("Storgatan ", doc.getLine(1).getCell(0).getStringValue());
	assertEquals("123 45", doc.getLine(1).getCell("Zip code")
		.getStringValue());

	assertEquals("Fred ", doc.getLine(2).getCell(0).getStringValue());
	assertEquals("Bergsten", doc.getLine(2).getCell("Last name")
		.getStringValue());
    }

    /**
     * Test method for {@link org.jsapar.schema.FixedWidthControlCellSchema#toString()}.
     */
    @Test
    @Ignore
    public void testToString() {
	fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.jsapar.schema.FixedWidthControlCellSchema#getSchemaLineByControlValue(java.lang.String)}.
     */
    @Test
    public void testGetSchemaLine() {
	FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();
	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Address");
	schemaLine.setLineTypeControlValue("A");
	schema.addSchemaLine(schemaLine);
	assertEquals("Address", schema.getSchemaLineByControlValue("A").getLineType());
    }

    /**
     * Test method for {@link org.jsapar.schema.FixedWidthControlCellSchema#clone()}.
     */
    @Test
    @Ignore
    public void testClone() {
	fail("Not yet implemented");
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

	public Document parse(java.io.Reader reader, ParseSchema parser)
		throws JSaParException, IOException {

	    parser.parse(reader, listener);
	    return this.document;
	}
    }
}
