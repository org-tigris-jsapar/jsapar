package org.jsapar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.jsapar.compose.bean.BeanComposer;
import org.jsapar.compose.bean.RecordingBeanEventListener;
import org.jsapar.error.RecordingErrorEventListener;
import org.jsapar.parse.CellParseError;
import org.jsapar.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BeanComposerTest {

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
    public final void testCreateJavaObjects() throws JSaParException, IOException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("firstName", "Jonas"));
        line1.addCell(new StringCell("lastName", "Stenberg"));
        line1.addCell(new IntegerCell("shoeSize", 42));
        line1.addCell(new DateCell("birthTime", this.birthTime ));
        line1.addCell(new IntegerCell("luckyNumber", 123456787901234567L));
        line1.addCell(new CharacterCell("door", 'A'));
        line1.addCell(new FloatCell("length", 123.45D));
        line1.addCell(new StringCell("gender", "M"));
//      line1.addCell(new StringCell("NeverUsed", "Should not be assigned"));
        

        Line line2 = new Line("org.jsapar.TstPerson");
        line2.addCell(new StringCell("FirstName", "Frida"));
        line2.addCell(new StringCell("LastName", "Bergsten"));
        line2.addCell(new EmptyCell("length", CellType.FLOAT));

        document.addLine(line1);
        document.addLine(line2);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(2, objects.size());
        TstPerson firstPerson = objects.get(0);
        assertEquals("Jonas", firstPerson.getFirstName());
        assertEquals(42, firstPerson.getShoeSize());
        assertEquals(this.birthTime,  firstPerson.getBirthTime());
        assertEquals(TstGender.M, firstPerson.getGender());
        assertEquals(123456787901234567L, firstPerson.getLuckyNumber());
        assertEquals('A', firstPerson.getDoor());
        assertEquals(123.45D, firstPerson.getLength(), 0.01);
        TstPerson secondPerson = objects.get(1);
        assertEquals("Bergsten", secondPerson.getLastName());
        assertEquals(0.0D, secondPerson.getLength(), 0.01);
    }


    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_Long_to_int() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new IntegerCell("shoeSize", 42L));
        

        document.addLine(line1);

        BeanComposer outputter = new BeanComposer();
        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(1, objects.size());
        assertEquals(42, (objects.get(0)).getShoeSize());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_Int_to_long() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new IntegerCell("luckyNumber", 1234));
        

        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(1, objects.size());
        assertEquals(1234, (objects.get(0)).getLuckyNumber());
    }

    @Test(expected = ParseException.class)
    public final void testCreateJavaObjects_wrongType() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new IntegerCell("firstName", 1234));
        

        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
    }

    /**
     * @throws JSaParException
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_subclass() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("address.street", "Stigen"));
        line1.addCell(new StringCell("address.town", "Staden"));
        line1.addCell(new StringCell("address.subAddress.town", "By"));
        

        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertNotNull((objects.get(0)).getAddress());
        assertEquals("Stigen", (objects.get(0)).getAddress().getStreet());
        assertEquals("Staden", (objects.get(0)).getAddress().getTown());
        assertEquals("By", (objects.get(0)).getAddress().getSubAddress().getTown());
    }

    /**
     * @throws JSaParException
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_subclass_error() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("address.doNotExist", "Stigen"));
        line1.addCell(new StringCell("address.town", "Staden"));
        line1.addCell(new StringCell("address.subAddress.town", "By"));
        

        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        RecordingErrorEventListener errorEventListener = new RecordingErrorEventListener();
        composer.addComposedEventListener(beanEventListener);
        composer.addErrorEventListener(errorEventListener);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(1, errorEventListener.getErrors().size());
        assertEquals(1, objects.size());
        assertNotNull((objects.get(0)).getAddress());
        assertEquals("Staden", (objects.get(0)).getAddress().getTown());
        assertEquals("By", (objects.get(0)).getAddress().getSubAddress().getTown());
        System.out.println("The (expected) error: " + errorEventListener.getErrors());
    }
    
    /**
     * @throws JSaParException
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testCreateJavaObjects_null_value() throws JSaParException {
        Document document = new Document();
        Line line1 = new Line("org.jsapar.TstPerson");
        line1.addCell(new StringCell("firstName", "Jonas"));
        line1.addCell(new StringCell("lastName", null));
        line1.addCell(new IntegerCell("shoeSize", 42));
       
        document.addLine(line1);

        BeanComposer composer = new BeanComposer();
        RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
        composer.addComposedEventListener(beanEventListener);
        java.util.List<TstPerson> objects = beanEventListener.getBeans();
        assertEquals(1, objects.size());
        assertEquals("Jonas", objects.get(0).getFirstName());
        assertNull(objects.get(0).getLastName());
        assertEquals(42, objects.get(0).getShoeSize());
    }    
    

}
