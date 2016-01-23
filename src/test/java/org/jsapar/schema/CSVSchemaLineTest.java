package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.junit.Test;

public class CSVSchemaLineTest  {

    boolean foundError=false;
    
    @Test
    public final void testCSVSchemaLine()  {
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        assertEquals("", schemaLine.getLineType());
        assertEquals("", schemaLine.getLineTypeControlValue());
    }

    @Test
    public final void testCSVSchemaLine_String()  {
        CsvSchemaLine schemaLine = new CsvSchemaLine("LineType");
        assertEquals("LineType", schemaLine.getLineType());
        assertEquals("LineType", schemaLine.getLineTypeControlValue());
    }
    
    
    
    @Test
    public void testOutput() throws IOException, JSaParException {

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
    public void testOutput_ignoreWrite() throws IOException, JSaParException {

        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        CsvSchemaCell firstNameSchema = new CsvSchemaCell("First Name");
        firstNameSchema.setIgnoreWrite(true);
        schemaLine.addSchemaCell(firstNameSchema);
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

        Line line = new Line();
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        StringWriter writer = new StringWriter();

        schemaLine.output(line, writer);

        assertEquals(";-)Stenberg", writer.toString());

    }    

    @Test
    public void testOutput_2byte_unicode() throws IOException, JSaParException {

        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator("\uFFD0");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

        Line line = new Line();
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        StringWriter writer = new StringWriter();

        schemaLine.output(line, writer);

        assertEquals("Jonas\uFFD0Stenberg", writer.toString());
    }

    
    @Test
    public void testOutput_not_found_in_line() throws IOException, JSaParException {

        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Shoe size"));

        Line line = new Line();
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        StringWriter writer = new StringWriter();

        schemaLine.output(line, writer);

        assertEquals("Jonas;-)Stenberg;-)", writer.toString());

    }

    @Test
    public void testOutput_null_value() throws IOException, JSaParException {

        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Shoe size"));

        Line line = new Line();
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        line.addCell(new StringCell("Shoe size", null));
        StringWriter writer = new StringWriter();

        schemaLine.output(line, writer);

        assertEquals("Jonas;-)Stenberg;-)", writer.toString());

    }
    
    @Test
    public void testOutput_reorder() throws IOException, JSaParException {
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

    @Test
    public void testOutput_default() throws IOException, JSaParException {

        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        CsvSchemaCell lastNameSchema = new CsvSchemaCell("Last Name");
        lastNameSchema.setDefaultValue("Svensson");
        schemaLine.addSchemaCell(lastNameSchema);

        Line line = new Line();
        line.addCell(new StringCell("First Name", "Jonas"));
        StringWriter writer = new StringWriter();

        schemaLine.output(line, writer);

        assertEquals("Jonas;-)Svensson", writer.toString());

    }
    
    @Test
    public void testGetSchemaCell(){
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        CsvSchemaCell cell1 = new CsvSchemaCell("First Name");
        schemaLine.addSchemaCell(cell1);
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        
        assertNull(schemaLine.getSchemaCell("Does not exist"));
        assertSame(cell1, schemaLine.getSchemaCell("First Name"));
        
    }
}
