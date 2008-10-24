package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.junit.Test;


public class FixedWidthSchemaTest {

	@Test
	public final void testBuild_Flat() throws JSaParException, IOException {
		String toParse = "JonasStenbergFridaStenberg";
		org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
		FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
		schema.setLineSeparator("");
		
		schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
		schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
		schema.addSchemaLine(schemaLine);
		
		Reader reader = new StringReader(toParse);
		Document doc=schema.build(reader, null);
		
		assertEquals("Jonas", doc.getLine(0).getCell(0).getStringValue());
		assertEquals("Stenberg", doc.getLine(0).getCell("Last name").getStringValue());

		assertEquals("Frida", doc.getLine(1).getCell(0).getStringValue());
		assertEquals("Stenberg", doc.getLine(1).getCell("Last name").getStringValue());
	}

	@Test
	public final void testOutput_Flat() throws IOException, JSaParException {
		String sExpected = "JonasStenbergFridaBergsten";
		org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
		FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
		schema.setLineSeparator("");
		
		schemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 5));
		schemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 8));
		schema.addSchemaLine(schemaLine);

		Line line1 = new Line();
		line1.addCell(new StringCell("Jonas"));
		line1.addCell(new StringCell("Stenberg"));

		Line line2 = new Line();
		line2.addCell(new StringCell("Frida"));
		line2.addCell(new StringCell("Bergsten"));
		
		Document doc=new Document();
		doc.addLine(line1);
		doc.addLine(line2);

		java.io.Writer writer = new java.io.StringWriter();
		schema.output(doc, writer);
		
		assertEquals(sExpected, writer.toString());
	}

    @Test
    public final void testClone() throws CloneNotSupportedException{
        FixedWidthSchema schema = new FixedWidthSchema();
        schema.setFillCharacter('*');
		schema.setLineSeparator("");
		FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
		schemaLine.setLineType("Joho");
		
		schema.addSchemaLine(schemaLine);
        
        FixedWidthSchema theClone = schema.clone();
        
        assertEquals(schema.getFillCharacter(), theClone.getFillCharacter());
        assertEquals(schema.getLineSeparator(), theClone.getLineSeparator());
        
        // Does not clone strings values yet. Might do that in the future.
        assertTrue(schema.getLineSeparator()== theClone.getLineSeparator());
        
        assertEquals(schema.getSchemaLines().get(0).getLineType(), theClone.getSchemaLines().get(0).getLineType());
        assertFalse(schema.getSchemaLines().get(0) == theClone.getSchemaLines().get(0));
    }
}
