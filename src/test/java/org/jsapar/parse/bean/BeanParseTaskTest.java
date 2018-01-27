/**
 * 
 */
package org.jsapar.parse.bean;

import org.jsapar.TstPerson;
import org.jsapar.TstPostAddress;
import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.model.*;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author stejon0
 * 
 */
public class BeanParseTaskTest {
    static final Date birthTime = new Date();

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

    private Schema makeOutputSchema(){
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(TstPerson.class.getName());
        schemaLine.addSchemaCell(new CsvSchemaCell("firstName", CellType.STRING));
        schemaLine.addSchemaCell(new CsvSchemaCell("lastName", CellType.STRING));
        schemaLine.addSchemaCell(new CsvSchemaCell("shoeSize", CellType.INTEGER));
        schemaLine.addSchemaCell(new CsvSchemaCell("streetNumber", CellType.INTEGER));
        schemaLine.addSchemaCell(new CsvSchemaCell("luckyNumber", CellType.DECIMAL));
        schemaLine.addSchemaCell(new CsvSchemaCell("birthTime", CellType.DATE));
        schemaLine.addSchemaCell(new CsvSchemaCell("door", CellType.CHARACTER));
        schemaLine.addSchemaCell(new CsvSchemaCell("address.street", CellType.STRING));
        schemaLine.addSchemaCell(new CsvSchemaCell("address.town", CellType.STRING));
        schemaLine.addSchemaCell(new CsvSchemaCell("workAddress.street", CellType.STRING));
        schemaLine.addSchemaCell(new CsvSchemaCell("workAddress.town", CellType.STRING));
        schemaLine.addSchemaCell(new CsvSchemaCell("address.subAddress.street", CellType.STRING));
        schemaLine.addSchemaCell(new CsvSchemaCell("address.subAddress.town", CellType.STRING));
        schema.addSchemaLine(schemaLine);
        return schema;
    }

    private BeanMap makeBeanMap() throws IntrospectionException, ClassNotFoundException {
        return BeanMap.ofSchema(makeOutputSchema());
    }

    @Test
    public void testBuild() throws IOException, IntrospectionException, ClassNotFoundException {
        List<TstPerson> people = new ArrayList<>(2);
        TstPerson person = new TstPerson();
        person.setFirstName("Jonas");
        people.add(person);

        person = new TstPerson();
        person.setFirstName("Test2");
        people.add(person);

        BeanParseTask<TstPerson> parser = new BeanParseTask<>(people.stream(), makeBeanMap());
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.setLineEventListener(listener);
        parser.execute();
        Document doc = listener.getDocument();

        assertEquals(2, doc.size());
        Line line = doc.getLine(0);
        assertEquals("Jonas",
                line.getCell("firstName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());

        line = doc.getLine(1);
        assertEquals("Test2", line.getCell("firstName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());

    }

    @Test
    public void testBuildLine() throws IntrospectionException, ClassNotFoundException {
        TstPerson person = new TstPerson();
        person.setBirthTime(birthTime);
        person.setFirstName("Jonas");
        person.setLastName("Bergsten");
        person.setLuckyNumber(123456787901234567L);
        person.setShoeSize((short)42);
        person.setStreetNumber(4711);
        person.setDoor('A');


        BeanParseTask<TstPerson> builder = new BeanParseTask<>(Arrays.asList(new TstPerson[]{person}).iterator(), makeBeanMap());
        Line line = builder.parseBean(person, new ExceptionErrorEventListener(), 1).orElse(null);
        assertEquals("org.jsapar.TstPerson", line.getLineType());
//        assertEquals(8, line.size());
        assertEquals("Jonas", line.getCell("firstName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals(42, ((IntegerCell) line.getCell("shoeSize").orElseThrow(() -> new AssertionError("Should be set"))).getValue().shortValue());
        assertEquals(4711, ((IntegerCell) line.getCell("streetNumber").orElseThrow(() -> new AssertionError("Should be set"))).getValue().intValue());
        assertEquals(birthTime, ((DateCell) line.getCell("birthTime").orElseThrow(() -> new AssertionError("Should be set"))).getValue());
        assertEquals(123456787901234567L, ((IntegerCell) line.getCell("luckyNumber").orElseThrow(() -> new AssertionError("Should be set"))).getValue().longValue());
        assertEquals("A", LineUtils.getStringCellValue(line,"door"));
    }

    @Test
    public void testBuildLine_subClass() throws IntrospectionException, ClassNotFoundException {
        TstPerson person = new TstPerson();
        person.setFirstName("Jonas");
        person.setAddress(new TstPostAddress("Stigen", "Staden"));
        BeanParseTask<TstPerson> builder = new BeanParseTask<>(Arrays.asList(new TstPerson[]{person}).iterator(), makeBeanMap());
        Line line = builder.parseBean(person, new ExceptionErrorEventListener(), 1).orElse(null);
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals("Stigen", LineUtils.getStringCellValue(line,"address.street"));
        assertEquals("Staden", LineUtils.getStringCellValue(line,"address.town"));
        
        // Make sure that loops are avoided.
        Assert.assertFalse(line.isCellSet("address.owner.firstName"));
    }

    @Test
    public void testBuildLine_subClass_multiplePaths() throws IntrospectionException, ClassNotFoundException {
        TstPerson person = new TstPerson();
        person.setAddress(new TstPostAddress("Stigen", "Staden"));
        person.getAddress().setSubAddress(new TstPostAddress("Road", "Town"));
        person.setWorkAddress(new TstPostAddress("Gatan", "Byn"));
        BeanParseTask<TstPerson> builder = new BeanParseTask<>(Arrays.asList(new TstPerson[]{person}).iterator(), makeBeanMap());
        Line line = builder.parseBean(person, new ExceptionErrorEventListener(), 1).orElse(null);
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals("Stigen", LineUtils.getStringCellValue(line,"address.street"));
        assertEquals("Staden", LineUtils.getStringCellValue(line,"address.town"));
        assertEquals("Road", LineUtils.getStringCellValue(line,"address.subAddress.street"));
        assertEquals("Town", LineUtils.getStringCellValue(line,"address.subAddress.town"));
        assertEquals("Gatan", LineUtils.getStringCellValue(line,"workAddress.street"));
        assertEquals("Byn", LineUtils.getStringCellValue(line,"workAddress.town"));
    }


    
}
