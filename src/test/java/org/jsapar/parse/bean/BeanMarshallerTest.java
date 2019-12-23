package org.jsapar.parse.bean;

import org.jsapar.TstGender;
import org.jsapar.TstPerson;
import org.jsapar.TstPostAddress;
import org.jsapar.bean.BeanMap;
import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.model.*;
import org.jsapar.schema.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class BeanMarshallerTest {
    static final Date birthTime = new Date();

    @Test
    public void testBuildLine() {
        TstPerson person = new TstPerson();
        person.setBirthTime(birthTime);
        person.setFirstName("Jonas");
        person.setLastName("Bergsten");
        person.setLuckyNumber(123456787901234567L);
        person.setShoeSize((short)42);
        person.setStreetNumber(4711);
        person.setDoor('A');
        person.setGender(TstGender.M);

        BeanMarshaller<TstPerson> beanMarshaller = new BeanMarshaller<>(makeBeanMap());
        Line line = beanMarshaller.marshal(person, new ExceptionErrorConsumer(), 1).orElse(null);
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        //        assertEquals(8, line.size());
        assertEquals("Jonas", line.getCell("firstName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals(42, ((IntegerCell) line.getCell("shoeSize").orElseThrow(() -> new AssertionError("Should be set"))).getValue().shortValue());
        assertEquals(4711, ((IntegerCell) line.getCell("streetNumber").orElseThrow(() -> new AssertionError("Should be set"))).getValue().intValue());
        assertEquals(birthTime, ((DateCell) line.getCell("birthTime").orElseThrow(() -> new AssertionError("Should be set"))).getValue());
        assertEquals(123456787901234567L, ((IntegerCell) line.getCell("luckyNumber").orElseThrow(() -> new AssertionError("Should be set"))).getValue().longValue());
        assertEquals("A", LineUtils.getStringCellValue(line,"door"));
        assertEquals(EnumCell.class, line.getCell("gender").orElse(null).getClass());
    }

    @Test
    public void testBuildLine_subClass() {
        TstPerson person = new TstPerson();
        person.setFirstName("Jonas");
        person.setAddress(new TstPostAddress("Stigen", "Staden"));
        BeanMarshaller<TstPerson> beanMarshaller = new BeanMarshaller<>(makeBeanMap());
        Line line = beanMarshaller.marshal(person, new ExceptionErrorConsumer(), 1).orElse(null);
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals("Stigen", LineUtils.getStringCellValue(line,"address.street"));
        assertEquals("Staden", LineUtils.getStringCellValue(line,"address.town"));

        // Make sure that loops are avoided.
        Assert.assertFalse(line.containsNonEmptyCell("address.owner.firstName"));
    }

    @Test
    public void testBuildLine_subClass_multiplePaths() {
        TstPerson person = new TstPerson();
        person.setAddress(new TstPostAddress("Stigen", "Staden"));
        person.getAddress().setSubAddress(new TstPostAddress("Road", "Town"));
        person.setWorkAddress(new TstPostAddress("Gatan", "Byn"));
        BeanMarshaller<TstPerson> beanMarshaller = new BeanMarshaller<>(makeBeanMap());
        Line line = beanMarshaller.marshal(person, new ExceptionErrorConsumer(), 1).orElse(null);
        assertEquals("org.jsapar.TstPerson", line.getLineType());
        assertEquals("Stigen", LineUtils.getStringCellValue(line,"address.street"));
        assertEquals("Staden", LineUtils.getStringCellValue(line,"address.town"));
        assertEquals("Road", LineUtils.getStringCellValue(line,"address.subAddress.street"));
        assertEquals("Town", LineUtils.getStringCellValue(line,"address.subAddress.town"));
        assertEquals("Gatan", LineUtils.getStringCellValue(line,"workAddress.street"));
        assertEquals("Byn", LineUtils.getStringCellValue(line,"workAddress.town"));
    }

    public static Schema makeOutputSchema(){
        CsvSchema schema = new CsvSchema();
        CsvSchemaLine schemaLine = new CsvSchemaLine(TstPerson.class.getName());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("firstName").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("lastName").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("shoeSize").withCellType(CellType.INTEGER).build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("streetNumber").withCellType(CellType.INTEGER).build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("luckyNumber").withCellType(CellType.DECIMAL).build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("birthTime").withCellType(CellType.DATE).build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("door").withCellType(CellType.CHARACTER).build());
        schemaLine.addSchemaCell(CsvSchemaCell.builder("gender")
                .withCellType(CellType.ENUM).withPattern("org.jsapar.TstGender")
                .build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("address.street").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("address.town").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("workAddress.street").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("workAddress.town").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("address.subAddress.street").build());
        schemaLine.addSchemaCell( CsvSchemaCell.builder("address.subAddress.town").build());
        schema.addSchemaLine(schemaLine);
        return schema;
    }

    public static BeanMap makeBeanMap() {
        return BeanMap.ofSchema(makeOutputSchema());
    }

}