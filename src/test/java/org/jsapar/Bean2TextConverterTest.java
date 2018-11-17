package org.jsapar;

import org.jsapar.model.Cell;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.time.LocalDate;
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
            Schema composeSchema = makeOutputSchema();


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
    public void setErrorEventListener() {
    }

    @Test
    public void addLineManipulator() throws IOException {
        Collection<TstPerson> people = makePeople();
        Schema composeSchema = makeOutputSchema();
        StringWriter writer = new StringWriter();
        try (Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(composeSchema, writer)) {
            converter.setErrorEventListener(event -> fail("Got error event"));
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
        CsvSchema outputSchema = new CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("org.jsapar.TstPerson");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("firstName"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("lastName"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);
        outputSchema.setLineSeparator("|");
        return outputSchema;
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