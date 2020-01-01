package org.jsapar;

import org.jsapar.schema.*;
import org.jsapar.text.TextParseConfig;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class Text2StringConverterTest {

    @Test
    public void convert() throws IOException {
        CsvSchema parseSchema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("test-line").withCells("c1", "c2", "c3").build())
                .build();
        StringSchema composeSchema = StringSchema.builder()
                .withLine(StringSchemaLine.builder("test-line").withCells("c2", "c1", "c3").build())
                .build();

        Text2StringConverter converter = new Text2StringConverter(parseSchema, composeSchema);
        String source = "v11;v12;v13\nv21;v22;v23\n";
        converter.convertForEach(new StringReader(source), (cells, lineType, lineNumber) -> {
            assertEquals("test-line", lineType);
            switch ((int) lineNumber) {
                case 1:
                    assertArrayEquals(new String[]{"v12", "v11", "v13"}, cells.toArray());
                    break;
                case 2:
                    assertArrayEquals(new String[]{"v22", "v21", "v23"}, cells.toArray());
                    break;
            }
        });
    }

    @Test
    public void Text2StringConverter() {
        CsvSchema parseSchema = new CsvSchema();
        CsvSchema composeSchema = new CsvSchema();
        TextParseConfig config = new TextParseConfig();
        Text2StringConverter converter = new Text2StringConverter(parseSchema, composeSchema, config);
        assertSame(config, converter.getParseConfig());
    }

    @Test
    public void getParseConfig() {
        Text2StringConverter converter = new Text2StringConverter(null, null);
        assertNotNull(converter.getParseConfig());
    }

    @Test
    public void setParseConfig() {
        Text2StringConverter converter = new Text2StringConverter(null, null);
        TextParseConfig config = new TextParseConfig();
        converter.setParseConfig(config);
        assertSame(config, converter.getParseConfig());
    }
}