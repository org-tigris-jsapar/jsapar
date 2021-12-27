package org.jsapar;

import org.jsapar.error.ValidationAction;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.Schema;
import org.jsapar.text.TextParseConfig;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TextParserTest {


    @Test
    public void testGetSetConfig() {
        TextParseConfig config = new TextParseConfig();
        TextParser parser = new TextParser(makeInputSchema(), config);
        assertSame(config, parser.getParseConfig());
        TextParseConfig newConfig = new TextParseConfig();
        parser.setParseConfig(newConfig);
        assertSame(newConfig, parser.getParseConfig());
    }


    @Test
    public void stream() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLine("a", l->l
                        .withCell("type", c->c.withLineCondition(v->v.equals("A")))
                        .withCell("gg"))
                .build();

        String text = "x;yyy\nA;BBB\nX;YYY\nA;CCC";
        TextParseConfig config = new TextParseConfig();
        config.setOnUndefinedLineType(ValidationAction.OMIT_LINE);
        TextParser parser = new TextParser(schema, config);
        try(Reader reader = new StringReader(text)) {
            List<Line> result = parser.stream(reader).collect(Collectors.toList());
            assertEquals(2, result.size());
            assertEquals("BBB", result.get(0).getCell("gg").map(Cell::getStringValue).orElse(null));
        }
    }

    private Schema<?> makeInputSchema() {
        return CsvSchema.builder().build();
    }


}