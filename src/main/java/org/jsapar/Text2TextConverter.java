package org.jsapar;

import org.jsapar.convert.AbstractConverter;
import org.jsapar.parse.TextParseConfig;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by stejon0 on 2016-10-15.
 */
public class Text2TextConverter extends AbstractConverter {
    private final Schema parseSchema;
    private final Schema composeSchema;
    private TextParseConfig parseConfig = new TextParseConfig();

    public Text2TextConverter(Schema parseSchema, Schema composeSchema) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
    }

    public void convert(Reader reader, Writer writer) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);

        ConvertTask convertTask = new ConvertTask(parseTask, new TextComposer(composeSchema, writer));

        execute(convertTask);
    }

    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
