package org.jsapar;

import org.jsapar.compose.bean.BeanComposeConfig;
import org.jsapar.compose.bean.BeanFactory;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class Text2BeanConverterTest {

    @Test
    public void convert() throws IOException {
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

    protected CsvSchema makeTestCsvSchema() {
        CsvSchema schema = new CsvSchema();
        schema.addSchemaLine( CsvSchemaLine.builder("org.jsapar.TstPerson")
                .withCell(CsvSchemaCell.builder("firstName").build())
                .withCell(CsvSchemaCell.builder("lastName").build())
                .build());
        return schema;
    }

    @Test
    public void convert_CustomBeanFactory() throws IOException {
        CsvSchema schema = makeTestCsvSchema();

        String input = "John;Doe";
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        converter.setBeanFactory(new BeanFactory<TstPerson>() {
            @Override
            public TstPerson createBean(Line line) {
                return new TstPerson();
            }

            @Override
            public void assignCellToBean(String lineType, TstPerson bean, Cell cell) {
                // Just convert to upper case and assign.
                switch(cell.getName()){
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

    @Test
    public void setGetComposeConfig() {
        Schema schema = new CsvSchema();
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        BeanComposeConfig composeConfig = new BeanComposeConfig();
        converter.setComposeConfig(composeConfig);
        assertSame(composeConfig, converter.getComposeConfig());
    }


    @Test
    public void setGetParseConfig() {
        Schema schema = new CsvSchema();
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        TextParseConfig config = new TextParseConfig();
        converter.setParseConfig(config);
        assertSame(config, converter.getParseConfig());
    }
}