package org.jsapar.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.junit.Test;

public class CSVSchemaTest {

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
	schema.write(doc.getLineIterator(), writer);

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
	outputSchema.writeLine(line1, 2, writer);

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
	outputSchema.writeLine(line1, 1, writer);

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
	
    schemaLine.addSchemaCell(new CsvSchemaCell("Birth date", new SchemaCellFormat(CellType.DATE, new SimpleDateFormat("yyyy-MM-dd"))));
    
	schemaLine.setFirstLineAsSchema(true);
	schema.addSchemaLine(schemaLine);

	Line line1 = new Line();
	line1.addCell(new StringCell("First name", "Jonas"));
	line1.addCell(new StringCell("Last name", "Stenberg"));

	StringWriter writer = new StringWriter();
	schema.writeLine(line1, 2, writer);

	String sLineSep = System.getProperty("line.separator");
	String sExpected = sLineSep + "First name;Last name;Shoe size;Birth date" + sLineSep + "Jonas;Stenberg;41;";

	assertEquals(sExpected, writer.toString());
    }


}
