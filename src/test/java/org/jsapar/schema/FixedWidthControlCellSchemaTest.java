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

    /**
     * Test method for {@link org.jsapar.schema.FixedWidthControlCellSchema#output(org.jsapar.Document, java.io.Writer)}.
     * @throws JSaParException 
     * @throws IOException 
     */
    @Test
    public void testOutputLine() throws IOException, JSaParException {
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema(1);
        schema.setLineSeparator("");

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name");
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.setLineTypeControlValue("N");
        schema.addSchemaLine(schemaLine);

        Line line = new Line("Name");
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));

        StringWriter writer = new StringWriter();
        schema.outputLineLn(line, writer);

        assertEquals("NJonasStenberg", writer.toString());
    }

    @Test
    public void testOutputLine_minLength() throws IOException, JSaParException {
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema(1);
        schema.setLineSeparator("");

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name");
        schemaLine.setMinLength(25);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.setLineTypeControlValue("N");
        schema.addSchemaLine(schemaLine);

        Line line = new Line("Name");
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));

        StringWriter writer = new StringWriter();
        schema.outputLineLn(line, writer);

        String result = writer.toString();
        assertEquals(25, result.length());
        assertEquals("NJonasStenberg           ", result);
    }

    @Test
    public void testOutputLine_noControl_minLength() throws IOException, JSaParException {
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema(1);
        schema.setLineSeparator("");
        schema.setWriteControlCell(false);

        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine("Name");
        schemaLine.setMinLength(25);
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
        schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
        schemaLine.setLineTypeControlValue("N");
        schema.addSchemaLine(schemaLine);

        Line line = new Line("Name");
        line.addCell(new StringCell("Jonas"));
        line.addCell(new StringCell("Stenberg"));

        StringWriter writer = new StringWriter();
        schema.outputLineLn(line, writer);

        String result = writer.toString();
        assertEquals(25, result.length());
        assertEquals("JonasStenberg            ", result);
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

}
