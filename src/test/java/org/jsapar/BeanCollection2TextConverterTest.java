package org.jsapar;

import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.Test;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 */
public class BeanCollection2TextConverterTest {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testConvert() throws Exception {
        List<TstPerson> people = new LinkedList<>();
        TstPerson testPerson1 = new TstPerson("Nils", "Holgersson", (short)4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));
        people.add(testPerson1);

        TstPerson testPerson2 = new TstPerson("Jonathan", "Lionheart", (short)37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));
        people.add(testPerson2);

        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder(TstPerson.class.getName())
                        .withCells("firstName", "lastName")
                        .build())
                .build();
        StringWriter writer = new StringWriter();
        BeanCollection2TextConverter<TstPerson> converter = new BeanCollection2TextConverter<>(schema);
        converter.convert(people, writer);

        String result1=writer.toString();
        String[] resultLines = result1.split(schema.getLineSeparator());
        //        System.out.println(result);
        assertEquals("Nils;Holgersson", resultLines[0]);
        assertEquals("Jonathan;Lionheart", resultLines[1]);

        writer = new StringWriter();
        long count = converter.convert(people.iterator(), writer);
        assertEquals(2, count);
        String result2=writer.toString();
        assertEquals(result1, result2);
    }


}