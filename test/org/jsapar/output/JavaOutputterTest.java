package org.jsapar.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.jsapar.DateCell;
import org.jsapar.Document;
import org.jsapar.IntegerCell;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.TestPerson;
import org.jsapar.input.CellParseError;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JavaOutputterTest {

    private java.util.Date birthTime;

    @Before
    public void setUp() throws ParseException {
	java.text.DateFormat dateFormat=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	this.birthTime = dateFormat.parse("1971-03-25 23:04:24");

	
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TestPerson");
        line1.addCell(new StringCell("firstName", "Jonas"));
        line1.addCell(new StringCell("lastName", "Stenberg"));
        line1.addCell(new IntegerCell("shoeSize", 42));
        line1.addCell(new DateCell("birthTime", this.birthTime ));
        line1.addCell(new IntegerCell("luckyNumber", 123456787901234567L));
        line1.addCell(new StringCell("door", "A"));
//      line1.addCell(new StringCell("NeverUsed", "Should not be assigned"));
        

        Line line2 = new Line("org.jsapar.TestPerson");
        line2.addCell(new StringCell("FirstName", "Frida"));
        line2.addCell(new StringCell("LastName", "Bergsten"));

        document.addLine(line1);
        document.addLine(line2);

        JavaOutputter outputter = new JavaOutputter();
	List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        java.util.List<TestPerson> objects = outputter.createJavaObjects(document, parseErrors);
        assertEquals("The errors: " + parseErrors, 0, parseErrors.size());
	assertEquals(2, objects.size());
	assertEquals("Jonas",  objects.get(0).getFirstName());
	assertEquals(42,  objects.get(0).getShoeSize());
	assertEquals(this.birthTime, ((TestPerson) objects.get(0)).getBirthTime());
	assertEquals(123456787901234567L, ((TestPerson) objects.get(0)).getLuckyNumber());
        assertEquals('A',  objects.get(0).getDoor());
	assertEquals("Bergsten", ((TestPerson) objects.get(1)).getLastName());
    }


    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_Long_to_int() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TestPerson");
        line1.addCell(new IntegerCell("shoeSize", 42L));
        

        document.addLine(line1);

        JavaOutputter outputter = new JavaOutputter();
        List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        java.util.List objects = outputter.createJavaObjects(document, parseErrors);
        assertEquals("The errors: " + parseErrors, 0, parseErrors.size());
        assertEquals(1, objects.size());
        assertEquals(42, ((TestPerson) objects.get(0)).getShoeSize());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_Int_to_long() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TestPerson");
        line1.addCell(new IntegerCell("luckyNumber", 1234));
        

        document.addLine(line1);

        JavaOutputter outputter = new JavaOutputter();
        List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        java.util.List<TestPerson> objects = outputter.createJavaObjects(document, parseErrors);
        assertEquals(0, parseErrors.size());
        assertEquals(1, objects.size());
        assertEquals(1234, (objects.get(0)).getLuckyNumber());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_wrongType() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TestPerson");
        line1.addCell(new IntegerCell("firstName", 1234));
        

        document.addLine(line1);

        JavaOutputter outputter = new JavaOutputter();
        List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        java.util.List<TestPerson> objects = outputter.createJavaObjects(document, parseErrors);
        assertEquals(1, parseErrors.size());
        Assert.assertNull(objects.get(0).getFirstName());
        System.out.println("The (expected) error: " + parseErrors);
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
