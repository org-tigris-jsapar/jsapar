package org.jsapar.output;

import static org.junit.Assert.assertEquals;

import org.jsapar.DateCell;
import org.jsapar.Document;
import org.jsapar.IntegerCell;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.TestPerson;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.Before;
import org.junit.Test;


public class OutputterTest {
    private Document document;
    private java.util.Date birthTime;

    @Before
    public void setUp() throws Exception {
	document = new Document();
	java.text.DateFormat dateFormat=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	this.birthTime = dateFormat.parse("1971-03-25 23:04:24");

	Line line1 = new Line("org.jsapar.TestPerson");
	line1.addCell(new StringCell("FirstName", "Jonas"));
	line1.addCell(new StringCell("LastName", "Stenberg"));
	line1.addCell(new IntegerCell("ShoeSize", 42));
	line1.addCell(new DateCell("BirthTime", this.birthTime ));
	line1.addCell(new IntegerCell("LuckyNumber", 123456787901234567L));
//	line1.addCell(new StringCell("NeverUsed", "Should not be assigned"));
	

	Line line2 = new Line("org.jsapar.TestPerson");
	line2.addCell(new StringCell("FirstName", "Frida"));
	line2.addCell(new StringCell("LastName", "Bergsten"));

	document.addLine(line1);
	document.addLine(line2);
	
    }

    @Test
    public final void testOutput() throws JSaParException {
	String sExpected = "JonasStenberg" + System.getProperty("line.separator")
		+ "FridaBergsten";
	org.jsapar.schema.FixedWidthSchema schema = new org.jsapar.schema.FixedWidthSchema();
	FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine(2);
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("FirstName", 5));
	schemaLine.addSchemaCell(new FixedWidthSchemaCell("LastName", 8));
	schema.addSchemaLine(schemaLine);

	Outputter outputter = new Outputter();
	java.io.Writer writer = new java.io.StringWriter();
	outputter.output(document, schema, writer);

	assertEquals(sExpected, writer.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects() {
	Outputter outputter = new Outputter();
	java.util.List objects = outputter.createJavaObjects(document);
	assertEquals(2, objects.size());
	assertEquals("Jonas", ((TestPerson) objects.get(0)).getFirstName());
	assertEquals(42, ((TestPerson) objects.get(0)).getShoeSize());
	assertEquals(this.birthTime, ((TestPerson) objects.get(0)).getBirthTime());
	assertEquals(123456787901234567L, ((TestPerson) objects.get(0)).getLuckyNumber());
	assertEquals("Bergsten", ((TestPerson) objects.get(1)).getLastName());
    }
  
}
