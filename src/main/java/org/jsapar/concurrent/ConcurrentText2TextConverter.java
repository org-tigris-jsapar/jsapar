package org.jsapar.concurrent;

import org.jsapar.TextComposer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A multi threaded version of {@link org.jsapar.Text2TextConverter} where the composer is started in a separate worker
 * thread.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 *
 * @see ConcurrentConvertTask
 * @see org.jsapar.Text2TextConverter
 *
 */
public class ConcurrentText2TextConverter extends AbstractConverter{
    private final Schema parseSchema;
    private final Schema composeSchema;
    private TextParseConfig parseConfig = new TextParseConfig();

    public ConcurrentText2TextConverter(Schema parseSchema, Schema composeSchema) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
    }

    public ConcurrentText2TextConverter(Schema parseSchema, Schema composeSchema, TextParseConfig parseConfig) {
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
        ConvertTask convertTask = new ConcurrentConvertTask(parseTask, new TextComposer(composeSchema, writer));
        return execute(convertTask);
    }

    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
