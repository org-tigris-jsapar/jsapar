package org.jsapar;

import org.jsapar.bean.BeanMap;
import org.jsapar.model.Cell;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class Bean2TextConverterTest {

    @Test
    public void convert() throws IOException {
        Collection<TstPerson> people = makePeople();
        try (StringWriter writer = new StringWriter()
        ) {
            CsvSchema composeSchema = makeOutputSchema();


            Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(composeSchema, writer);
            for (TstPerson person : people) {
                assertTrue(converter.convert(person));
            }

            String result = writer.toString();
            assertEquals("Nisse;Holgersson|Jonte;Lionheart", result);
            String[] lines = result.split(Pattern.quote(composeSchema.getLineSeparator()));
            assertEquals(2, lines.length);
        }

    }

    @Test
    public void convert_BeanMap() throws IOException {
        Collection<TstPerson> people = makePeople();
        try (StringWriter writer = new StringWriter()
        ) {
            CsvSchema composeSchema = CsvSchema.builder()
                    .withLineSeparator("|")
                    .withLine( CsvSchemaLine.builder("Person")
                            .withCells("First Name", "Last Name")
                            .withCellSeparator(";")
                            .build())
                    .build();

            BeanMap beanMap= BeanMap.builder()
                    .withLine("Person", TstPerson.class, l->l
                            .withCell("First Name", "firstName")
                            .withCell("Last Name", "lastName"))
                    .build();

            Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(composeSchema, beanMap, writer);
            for (TstPerson person : people) {
                assertTrue(converter.convert(person));
            }

            String result = writer.toString();
            assertEquals("Nisse;Holgersson|Jonte;Lionheart", result);
            String[] lines = result.split(Pattern.quote(composeSchema.getLineSeparator()));
            assertEquals(2, lines.length);
        }
    }

    @Test
    public void convert_BeanMap_cellsBySchema_linesByBeanMap() throws IOException {
        Collection<TstPerson> people = makePeople();
        try (StringWriter writer = new StringWriter()
        ) {
            CsvSchema composeSchema = CsvSchema.builder()
                    .withLineSeparator("|")
                    .withLine( CsvSchemaLine.builder("Person")
                            .withCells("firstName", "lastName")
                            .withCellSeparator(";")
                            .build())
                    .build();

            BeanMap beanMap = BeanMap.ofSchema(composeSchema, BeanMap.builder()
                    .withLine("Person", TstPerson.class
                    ).build());

            Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(composeSchema, beanMap, writer);
            for (TstPerson person : people) {
                assertTrue(converter.convert(person));
            }

            String result = writer.toString();
            assertEquals("Nisse;Holgersson|Jonte;Lionheart", result);
            String[] lines = result.split(Pattern.quote(composeSchema.getLineSeparator()));
            assertEquals(2, lines.length);
        }

    }


    @Test
    public void addLineManipulator() throws IOException {
        Collection<TstPerson> people = makePeople();
        CsvSchema composeSchema = makeOutputSchema();
        StringWriter writer = new StringWriter();
        try (Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(composeSchema, writer)) {
            converter.setErrorConsumer(error -> fail("Got error event"));
            converter.addLineManipulator(line -> {
                if(line.getNonEmptyCell("firstName").map(Cell::getStringValue).orElse("").equals("Nisse"))
                    return false;
                line.forEach(cell -> line.putCell(new StringCell(cell.getName(), cell.getStringValue().toUpperCase())));
                return true;
            });
            int count = 0;
            for (TstPerson person : people) {
                if(converter.convert(person))
                    count++;
            }
            assertEquals(1, count);
            String result = writer.toString();
            assertEquals("JONTE;LIONHEART", result);
            String[] lines = result.split(Pattern.quote(composeSchema.getLineSeparator()));
            assertEquals(1, lines.length);
        }
    }

    private CsvSchema makeOutputSchema() {
        return CsvSchema.builder()
                .withLineSeparator("|")
                .withLine( CsvSchemaLine.builder("org.jsapar.TstPerson")
                        .withCells("firstName", "lastName")
                        .withCellSeparator(";")
                        .build())
                .build();
    }

    private Collection<TstPerson> makePeople() {
        List<TstPerson> people = new LinkedList<>();
        TstPerson testPerson1 = new TstPerson("Nisse", "Holgersson", (short) 42, 17, null, 12, 'A');
        people.add(testPerson1);

        TstPerson testPerson2 = new TstPerson("Jonte", "Lionheart", (short) 46, 19, null, 12, 'A');
        people.add(testPerson2);

        return people;
    }

}