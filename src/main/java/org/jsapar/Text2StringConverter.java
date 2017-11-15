package org.jsapar;

import org.jsapar.compose.string.StringComposedEventListener;
import org.jsapar.compose.string.StringComposer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Converts one text input to an output of Stream of String.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 */
public class Text2StringConverter extends AbstractConverter {
    private final Schema parseSchema;
    private final Schema composeSchema;
    private TextParseConfig parseConfig = new TextParseConfig();

    public Text2StringConverter(Schema parseSchema, Schema composeSchema) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
    }

    public Text2StringConverter(Schema parseSchema, Schema composeSchema, TextParseConfig parseConfig) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
        this.parseConfig = parseConfig;
    }

    /**
     * @param reader The reader to read input from
     * @param composedEventListener The string composed event listener that get notification of each line.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     */
    public long convert(Reader reader, StringComposedEventListener composedEventListener) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);
        ConvertTask convertTask = new ConvertTask(parseTask, new StringComposer(composeSchema, composedEventListener));
        return execute(convertTask);
    }

    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
