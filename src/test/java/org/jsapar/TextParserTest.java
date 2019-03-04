package org.jsapar;

import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.Schema;
import org.junit.Test;

import static org.junit.Assert.*;

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

    private Schema makeInputSchema() {
        return new CsvSchema();
    }
}