package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
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
    public void testParse() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        String sLine = "Jonas;Stenberg;Hemvägen 19;111 22;Stockholm";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
            }
        }, null);
        assertEquals(true, rc);
    }

    @Test
    public void testParse_2byte_unicode() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator("\uFFD0");
        String sLine = "Jonas\uFFD0Stenberg\uFFD0Hemvägen 19\uFFD0111 22\uFFD0Stockholm";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }
    
    @Test
    public void testParse_quoted() throws JSaParException, IOException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;\"\";\"Hemvägen ;19\";\"\"111 22\"\";Stockholm";
	boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

	    @Override
	    public void lineErrorEvent(LineErrorEvent event) throws ParseException {
	    }

	    @Override
	    public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
		Line line = event.getLine();
		assertEquals(6, line.getNumberOfCells());
		assertEquals("Jonas", line.getCell(0).getStringValue());
		assertEquals("Stenberg", line.getCell(1).getStringValue());
		assertEquals("", line.getCell(2).getStringValue());
		assertEquals("Hemvägen ;19", line.getCell(3).getStringValue());
		assertEquals("\"111 22\"", line.getCell(4).getStringValue());
		assertEquals("Stockholm", line.getCell(5).getStringValue());
	    }
	}, null);

	assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted_last() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19\"";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(3, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("Hemvägen ;19", line.getCell(2).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }

    @Test
    public void testParse_quoted_after_empty() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;;\"Hemvägen ;19\"";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(4, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("", line.getCell(2).getStringValue());
                assertEquals("Hemvägen ;19", line.getCell(3).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }

    @Test
    public void testParse_one_unquoted_empty_between_quoted() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;\"Stenberg\";;\"Hemvägen ;19\"";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(4, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("", line.getCell(2).getStringValue());
                assertEquals("Hemvägen ;19", line.getCell(3).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }
    
    @Test
    public void testParse_quoted_after_unquoted() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;\"Stenberg\";Not quoted;\"Hemvägen ;19\"";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(4, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("Not quoted", line.getCell(2).getStringValue());
                assertEquals("Hemvägen ;19", line.getCell(3).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }
    
    @Test
    public void testParse_quoted_last_cellsep() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setQuoteChar('\"');
        String sLine = "Jonas;Stenberg;\"Hemvägen ;19\";";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(3, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("Hemvägen ;19", line.getCell(2).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }
    
    @Test(expected=JSaParException.class)
    public void testParse_quoted_missing_end() throws JSaParException, IOException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;\"Hemvägen ;19;111 22;Stockholm";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(6, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("\"Hemvägen ", line.getCell(2).getStringValue());
                assertEquals("19", line.getCell(3).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }

    @Test(expected=JSaParException.class)
    public void testParse_quoted_line_break() throws JSaParException, IOException {
    CsvSchemaLine schemaLine = new CsvSchemaLine(1);
    schemaLine.setQuoteChar('\"');
    String sLine = "Jonas;Stenberg;\"";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(3, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("\"", line.getCell(2).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }    
    
    
    @Test
    public void testParse_quoted_miss_placed_start() throws JSaParException, IOException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;H\"emvägen ;19;111 \"22\";\"Stoc\"kholm\"";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(6, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("H\"emvägen ", line.getCell(2).getStringValue());
                assertEquals("19", line.getCell(3).getStringValue());
                assertEquals("111 \"22\"", line.getCell(4).getStringValue());
                assertEquals("Stoc\"kholm", line.getCell(5).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }

    @Test(expected=JSaParException.class)
    public void testParse_quoted_miss_placed_end() throws JSaParException, IOException {
	CsvSchemaLine schemaLine = new CsvSchemaLine(1);
	schemaLine.setQuoteChar('\"');
	String sLine = "Jonas;Stenberg;\"Hemvägen ;1\"9;111 22;Stockholm";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals(6, line.getNumberOfCells());
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("\"Hemvägen ", line.getCell(2).getStringValue());
                assertEquals("1\"9", line.getCell(3).getStringValue());
                assertEquals("111 22", line.getCell(4).getStringValue());
            }
        }, null);

        assertEquals(true, rc);
    }

    @Test
    public void testParse_withNames() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

        String sLine = "Jonas;-)Stenberg";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
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
        }, null);
        assertEquals(true, rc);

    }
    
    @Test
    public void testParse_maxLength() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";");
        CsvSchemaCell schemaFirstName = new CsvSchemaCell("First Name");
        schemaFirstName.setMaxLength(15);
        schemaLine.addSchemaCell(schemaFirstName);
        CsvSchemaCell schemaLastName = new CsvSchemaCell("Last Name");
        schemaLastName.setMaxLength(5);
        schemaLine.addSchemaCell(schemaLastName);

        String sLine = "Jonas;Stenberg";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                // TODO Auto-generated method stub

            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Stenb", line.getCell(1).getStringValue());
            }
        }, null);
        assertEquals(true, rc);

    }
    

    @Test
    public void testParse_withDefault() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setDefaultValue("yes");
        schemaLine.addSchemaCell(happyCell);
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

        String sLine = "Jonas;-);-)Stenberg";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Jonas", line.getStringCellValue("First Name"));
                assertEquals("Stenberg", line.getCell(2).getStringValue());
                assertEquals("yes", line.getStringCellValue("Happy"));

                assertEquals("First Name", line.getCell(0).getName());
                assertEquals("Last Name", line.getCell(2).getName());
            }
        }, null);
        assertEquals(true, rc);

    }

    @Test
    public void testParse_default_and_mandatory() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setDefaultValue("yes");
        happyCell.setMandatory(true);
        schemaLine.addSchemaCell(happyCell);
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));

        String sLine = "Jonas;-);-)Stenberg";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                assertEquals("Happy", event.getCellParseError().getCellName());
                foundError = true;
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Jonas", line.getStringCellValue("First Name"));
                assertEquals("Stenberg", line.getCell(2).getStringValue());
                assertEquals("yes", line.getStringCellValue("Happy"));

                assertEquals("First Name", line.getCell(0).getName());
                assertEquals("Last Name", line.getCell(2).getName());
            }
        }, null);
        assertEquals(true, rc);
        assertEquals(true, foundError);
    }
    
    @Test
    public void testParse_withDefaultLast() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setDefaultValue("yes");
        schemaLine.addSchemaCell(happyCell);

        String sLine = "Jonas;-)Stenberg";
        boolean rc = schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                // TODO Auto-generated method stub

            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                Line line = event.getLine();
                assertEquals("Jonas", line.getCell(0).getStringValue());
                assertEquals("Jonas", line.getStringCellValue("First Name"));
                assertEquals("Stenberg", line.getCell(1).getStringValue());
                assertEquals("yes", line.getStringCellValue("Happy"));

                assertEquals("First Name", line.getCell(0).getName());
                assertEquals("Last Name", line.getCell(1).getName());
            }
        }, null);
        assertEquals(true, rc);

    }
    
    @Test(expected=ParseException.class)
    public void testParse_withMandatoryLast() throws JSaParException, IOException {
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        schemaLine.addSchemaCell(new CsvSchemaCell("First Name"));
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        CsvSchemaCell happyCell = new CsvSchemaCell("Happy");
        happyCell.setMandatory(true);
        schemaLine.addSchemaCell(happyCell);

        String sLine = "Jonas;-)Stenberg";
        schemaLine.parse(1, sLine, new ParsingEventListener() {

            @Override
            public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                throw new ParseException(event.getCellParseError());
            }

            @Override
            public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
                fail("Expects an error");
            }
        }, null);
        fail("Expects an error");

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
