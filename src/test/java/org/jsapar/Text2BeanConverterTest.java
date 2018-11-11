package org.jsapar;

import org.jsapar.compose.bean.BeanComposeConfig;
import org.jsapar.compose.bean.BeanComposeException;
import org.jsapar.compose.bean.BeanFactory;
import org.jsapar.compose.bean.BeanFactoryDefault;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class Text2BeanConverterTest {

    @Test
    public void setBeanFactory() {
    }

    @Test
    public void convert() throws IOException {
        CsvSchema schema = makeTestCsvSchema();

        String input = "John;Doe";
        Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(schema);
        try(Reader reader=new StringReader(input)){
            long count = converter.convert(reader, e -> {
                assertEquals("John", e.getBean().getFirstName());
                assertEquals("Doe", e.getBean().getLastName());
            });
            assertEquals(1, count);
        }
    }

    protected CsvSchema makeTestCsvSchema() {
        CsvSchema schema = new CsvSchema();
        schema.addSchemaLine(new CsvSchemaLine("org.jsapar.TstPerson")
                .addSchemaCell(new CsvSchemaCell("firstName"))
                .addSchemaCell(new CsvSchemaCell("lastName")));
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
            long count = converter.convert(reader, e -> {
                assertEquals("JOHN", e.getBean().getFirstName());
                assertEquals("DOE", e.getBean().getLastName());
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