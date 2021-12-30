package org.jsapar.concurrent;

import org.jsapar.BeanCollection2TextConverter;
import org.jsapar.TstPerson;
import org.jsapar.TstPostAddress;
import org.jsapar.model.CellType;
import org.jsapar.bean.BeanMap;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.Test;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ConcurrentBeanCollection2TextConverterTest {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testConvert() throws Exception {
        List<TstPerson> people = makePeople();

        CsvSchema schema = makeSchema();
        StringWriter writer = new StringWriter();
        BeanCollection2TextConverter<TstPerson> converter = new ConcurrentBeanCollection2TextConverter<>(schema);
        converter.convert(people, writer);

        String result=writer.toString();
        String[] resultLines = result.split(schema.getLineSeparator());
        //        System.out.println(result);
        assertEquals("Nils;Holgersson", resultLines[0]);
        assertEquals("Jonathan;Lionheart", resultLines[1]);
    }

    @Test
    public void testConvert_iterated() throws Exception {
        List<TstPerson> people = makePeople();
        AtomicInteger started = new AtomicInteger(0);
        AtomicInteger stopped = new AtomicInteger(0);

        CsvSchema schema = makeSchema();
        StringWriter writer = new StringWriter();
        BeanMap beanMap = BeanMap.ofSchema(schema);
        ConcurrentBeanCollection2TextConverter<TstPerson> converter = new ConcurrentBeanCollection2TextConverter<>(schema, beanMap);
        converter.registerOnStart(started::getAndIncrement);
        converter.registerOnStart(started::getAndIncrement);
        converter.registerOnStop(stopped::getAndIncrement);
        converter.convert(people.iterator(), writer);

        String result=writer.toString();
        String[] resultLines = result.split(schema.getLineSeparator());
        //        System.out.println(result);
        assertEquals("Nils;Holgersson", resultLines[0]);
        assertEquals("Jonathan;Lionheart", resultLines[1]);
        assertEquals(2, started.get());
        assertEquals(1, stopped.get());
    }

    private List<TstPerson> makePeople() throws ParseException {
        List<TstPerson> people = new LinkedList<>();
        TstPerson testPerson1 = new TstPerson("Nils", "Holgersson", (short)4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));
        people.add(testPerson1);

        TstPerson testPerson2 = new TstPerson("Jonathan", "Lionheart", (short)37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));
        people.add(testPerson2);
        return people;
    }

    private CsvSchema makeSchema() {
        return CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("org.jsapar.TstPerson")
                        .withCells("firstName", "lastName")
                        .build())
                .build();
    }

}