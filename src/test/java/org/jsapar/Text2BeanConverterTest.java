package org.jsapar;

import org.jsapar.compose.bean.BeanComposeConfig;
import org.jsapar.error.JSaParException;
import org.jsapar.compose.bean.BeanFactory;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.text.TextParseConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Text2BeanConverterTest {

    @Test
    public void convertForEach() throws IOException {
        CsvSchema schema = makeTestCsvSchema();

        String input = "John;Doe";
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        try(Reader reader=new StringReader(input)){
            long count = converter.convertForEach(reader, b -> {
                assertEquals("John", b.getFirstName());
                assertEquals("Doe", b.getLastName());
            });
            assertEquals(1, count);
        }
    }

    @Test
    public void stream() throws IOException {
        CsvSchema schema = makeTestCsvSchema();
        String input = "John;Doe";
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        try (Reader reader = new StringReader(input)) {
            TstPerson person = converter.stream(reader).findFirst().orElseThrow();
            assertEquals("John", person.getFirstName());
            assertEquals("Doe", person.getLastName());
        }
    }

    protected CsvSchema makeTestCsvSchema() {
        return CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("org.jsapar.TstPerson")
                        .withCells("firstName", "lastName")
                        .build())
                .build();
    }

    @Test
    public void convert_CustomBeanFactory() throws IOException {
        CsvSchema schema = makeTestCsvSchema();

        String input = "John;Doe";
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        converter.setBeanFactory(new BeanFactory<>() {
            @Override
            public TstPerson createBean(Line line) {
                return new TstPerson();
            }

            @Override
            public void assignCellToBean(String lineType, TstPerson bean, Cell<?> cell) {
                // Just convert to upper case and assign.
                switch (cell.getName()) {
                    case "firstName":
                        bean.setFirstName(cell.getStringValue().toUpperCase());
                        break;
                    case "lastName":
                        bean.setLastName(cell.getStringValue().toUpperCase());
                        break;
                }
            }
        });
        try(Reader reader=new StringReader(input)){
            long count = converter.convertForEach(reader, bean -> {
                assertEquals("JOHN", bean.getFirstName());
                assertEquals("DOE", bean.getLastName());
            });
            assertEquals(1, count);
        }
    }

    private static CsvSchema makeAddressAndPersonSchema() {
        return CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("org.jsapar.TstPostAddress")
                        .withOccurs(1)
                        .withCells("street", "town")
                        .build())
                .withLine(CsvSchemaLine.builder("org.jsapar.TstPerson")
                        .withCells("firstName", "lastName")
                        .build())
                .build();
    }

    @Test
    public void convertForEach_beanClass_otherClassReportedAndSkipped() throws IOException {
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(makeAddressAndPersonSchema());
        converter.setBeanClass(TstPerson.class);
        assertSame(TstPerson.class, converter.getBeanClass());
        List<JSaParException> errors = new ArrayList<>();
        converter.setErrorConsumer(errors::add);
        List<String> firstNames = new ArrayList<>();
        try (Reader reader = new StringReader("Main street;Springfield\nJohn;Doe")) {
            converter.convertForEach(reader, bean -> firstNames.add(bean.getFirstName()));
        }
        assertEquals(List.of("John"), firstNames);
        assertEquals(1, errors.size(), errors.toString());
        assertTrue(errors.getFirst().getCause().getMessage().contains("org.jsapar.TstPostAddress"),
                errors.getFirst().getCause().getMessage());
    }

    @Test
    public void stream_beanClass_otherClassReportedAndSkipped() throws IOException {
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(makeAddressAndPersonSchema());
        converter.setBeanClass(TstPerson.class);
        List<JSaParException> errors = new ArrayList<>();
        converter.setErrorConsumer(errors::add);
        try (Reader reader = new StringReader("Main street;Springfield\nJohn;Doe")) {
            assertEquals(List.of("John"), converter.stream(reader).map(TstPerson::getFirstName).toList());
        }
        assertEquals(1, errors.size(), errors.toString());
    }

    @Test
    public void setGetComposeConfig() {
        CsvSchema schema = CsvSchema.builder().build();
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        BeanComposeConfig composeConfig = new BeanComposeConfig();
        converter.setComposeConfig(composeConfig);
        assertSame(composeConfig, converter.getComposeConfig());
    }


    @Test
    public void setGetParseConfig() {
        CsvSchema schema = CsvSchema.builder().build();
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        TextParseConfig config = new TextParseConfig();
        converter.setParseConfig(config);
        assertSame(config, converter.getParseConfig());
    }
}