package org.jsapar;

import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Converts one text input to another text output. For instance converting from CSV to fixed with format.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 * @see org.jsapar.concurrent.ConcurrentText2TextConverter
 */
public class Text2TextConverter extends AbstractConverter {
    private final Schema parseSchema;
    private final Schema composeSchema;
    private TextParseConfig parseConfig = new TextParseConfig();

    public Text2TextConverter(Schema parseSchema, Schema composeSchema) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
    }

    public Text2TextConverter(Schema parseSchema, Schema composeSchema, TextParseConfig parseConfig) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
        this.parseConfig = parseConfig;
    }

    /**
     * @param reader The reader to read input from
     * @param writer The writer to write converted result to.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     */
    public long convert(Reader reader, Writer writer) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);
        ConvertTask convertTask = new ConvertTask(parseTask, new TextComposer(composeSchema, writer));
        return execute(convertTask);
    }

    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
