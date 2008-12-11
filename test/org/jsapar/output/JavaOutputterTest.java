package org.jsapar.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jsapar.DateCell;
import org.jsapar.Document;
import org.jsapar.IntegerCell;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.TestPerson;
import org.jsapar.output.JavaOutputter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JavaOutputterTest {

    private Document document;
    private java.util.Date birthTime;

    @Before
    public void setUp() throws Exception {
	document = new Document();
	java.text.DateFormat dateFormat=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	this.birthTime = dateFormat.parse("1971-03-25 23:04:24");

	Line line1 = new Line("org.jsapar.TestPerson");
	line1.addCell(new StringCell("firstName", "Jonas"));
	line1.addCell(new StringCell("lastName", "Stenberg"));
	line1.addCell(new IntegerCell("shoeSize", 42));
	line1.addCell(new DateCell("birthTime", this.birthTime ));
	line1.addCell(new IntegerCell("luckyNumber", 123456787901234567L));
//	line1.addCell(new StringCell("NeverUsed", "Should not be assigned"));
	

	Line line2 = new Line("org.jsapar.TestPerson");
	line2.addCell(new StringCell("FirstName", "Frida"));
	line2.addCell(new StringCell("LastName", "Bergsten"));

	document.addLine(line1);
	document.addLine(line2);
	
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects() {
	JavaOutputter outputter = new JavaOutputter();
	java.util.List objects = outputter.createJavaObjects(document);
	assertEquals(2, objects.size());
	assertEquals("Jonas", ((TestPerson) objects.get(0)).getFirstName());
	assertEquals(42, ((TestPerson) objects.get(0)).getShoeSize());
	assertEquals(this.birthTime, ((TestPerson) objects.get(0)).getBirthTime());
	assertEquals(123456787901234567L, ((TestPerson) objects.get(0)).getLuckyNumber());
	assertEquals("Bergsten", ((TestPerson) objects.get(1)).getLastName());
    }

    @Test
    @Ignore("Not yet implemented")
    public void testCreateObject() {
	fail("Not yet implemented");
    }

    @Test
    @Ignore("Not yet implemented")
    public void testAssign() {
	fail("Not yet implemented");
    }

}
